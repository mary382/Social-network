package com.bootcamp.socialnetwork.service;

import com.bootcamp.socialnetwork.domain.Community;
import com.bootcamp.socialnetwork.domain.CommunityType;
import com.bootcamp.socialnetwork.domain.Post;
import com.bootcamp.socialnetwork.repository.CommunityRepository;
import com.bootcamp.socialnetwork.repository.PostRepository;
import com.bootcamp.socialnetwork.service.dto.AttachmentDto;
import com.bootcamp.socialnetwork.service.dto.CommunityDto;
import com.bootcamp.socialnetwork.service.dto.PostDto;
import com.bootcamp.socialnetwork.service.dto.UserProfileDto;
import com.bootcamp.socialnetwork.util.AttachmentManager;
import com.bootcamp.socialnetwork.util.AttachmentType;
import com.bootcamp.socialnetwork.util.DropboxClient;
import com.bootcamp.socialnetwork.web.rest.errors.CommunityException;
import com.bootcamp.socialnetwork.web.rest.errors.EntityNotFoundException;
import com.bootcamp.socialnetwork.web.rest.errors.PrivilegeException;
import com.bootcamp.socialnetwork.web.rest.errors.UnsupportedFileTypeException;
import com.dropbox.core.DbxException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

/**
 * @author Pavel Kasper
 */
@Service("communityService")
@Transactional
public class CommunityServiceImpl implements CommunityService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CommunityRepository communityRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;


    // ------------------------------------------Basic--------------------------------------------------------------

    @Override
    public CommunityDto findCommunity(Long id) throws EntityNotFoundException {
        Community community = communityRepository.findOne(id);
        if (community == null) throw new EntityNotFoundException(
                String.format("Community with id %d not found.", id));

        return modelMapper.map(community, CommunityDto.class);
    }

    @Override
    public void createCommunity(CommunityDto communityDto, Long principalId) throws DbxException {

        UserProfileDto userProfileDto = userService.findProfile(principalId);
        if (communityDto.getType() == null) communityDto.setType(CommunityType.OPEN);
        communityDto.setOwner(userProfileDto);
        communityDto.getParticipants().add(userProfileDto);
        communityDto.setParticipantsCount(communityDto.getParticipants().size());
        communityRepository.save(modelMapper.map(communityDto, Community.class));
    }

    @Override
    public CommunityDto getCommunity(Long id, Long principalId) throws EntityNotFoundException,
            PrivilegeException {

        CommunityDto communityDto = findCommunity(id);
        // If community type equals CLOSED — close comm for all users besides participants
        if ((principalId == null || !isCommunityMember(id, principalId)) && communityDto.getType() == CommunityType.CLOSED)
            throw new PrivilegeException("User does not have enough rights.");
        if (principalId != null) {
            checkUserLock(communityDto.getBlockedUsers(), principalId);
        }

        return communityDto;
    }

    @Override
    public Page<CommunityDto> getPaginatedCommunities(Integer page, Integer size) {

        if (page == null && size == null) {
            page = 1;
            size = (int) communityRepository.count();
            if (size == 0) return null;
        }

        // page - 1, because indexing from 0
        Pageable pageRequest = new PageRequest(page - 1, size, Sort.Direction.DESC, "participantsCount");
        List<CommunityDto> communityDtoList = new ArrayList<>();
        Page<Community> communityPage = communityRepository.findAll(pageRequest);
        communityPage.getContent()
                .forEach(community -> communityDtoList.add(modelMapper.map(community, CommunityDto.class)));

        return new PageImpl<>(communityDtoList, pageRequest, communityPage.getTotalElements());
    }

    @Override
    public CommunityDto updateCommunity(Long id, CommunityDto communityDto, Long principalId)
            throws EntityNotFoundException, PrivilegeException, IOException, DbxException {

        CommunityDto community = findCommunity(id);
        checkUserPower(community.getOwner().getId(), principalId);
        community.setTitle(communityDto.getTitle());
        community.setInfo(communityDto.getInfo());
        if (communityDto.getType() != null) community.setType(communityDto.getType());
        communityRepository.save(modelMapper.map(community, Community.class));

        return community;
    }

    @Override
    public void deleteCommunity(Long id, Long principalId) throws EntityNotFoundException,
            PrivilegeException, DbxException {

        CommunityDto communityDto = findCommunity(id);
        checkUserPower(communityDto.getOwner().getId(), principalId);
        postService.deleteAllByOwner(-1 * communityDto.getId());
        communityRepository.delete(id);
    }

    // -------------------------------------------Posts--------------------------------------------------------------

    @Override
    public PostDto createCommunityPost(Long id, PostDto postDto, Long principalId) throws EntityNotFoundException,
            PrivilegeException, DbxException {

        CommunityDto communityDto = findCommunity(id);
        checkUserPower(communityDto.getOwner().getId(), principalId);
        postDto.setOwnerId(-1 * communityDto.getId());
        postDto.setAuthor(modelMapper.map(communityDto.getOwner(), UserProfileDto.class));
        postDto.setTime(System.currentTimeMillis());
        Post newPost = postRepository.save(modelMapper.map(postDto, Post.class));
        DropboxClient.createFolder(String.format("/community/%d/post/%d", id, newPost.getId()));
        postDto.setId(newPost.getId());
        return postDto;
    }

    @Override
    public PostDto getCommunityPost(Long id, Long postId, Long principalId) throws EntityNotFoundException,
            PrivilegeException {

        CommunityDto communityDto = findCommunity(id);
        if ((principalId == null || !isCommunityMember(id, principalId)) && communityDto.getType() == CommunityType.CLOSED)
            throw new PrivilegeException("User does not have enough rights.");
        if (principalId != null) {
            checkUserLock(communityDto.getBlockedUsers(), principalId);
        }

        return postService.findPost(postId);
    }

    @Override
    public Page<PostDto> getCommunityPaginatedPosts(Long id, Long principalId, Integer page, Integer size)
            throws EntityNotFoundException, PrivilegeException {

        CommunityDto communityDto = findCommunity(id);
        if ((principalId == null || !isCommunityMember(id, principalId)) && communityDto.getType() == CommunityType.CLOSED)
            throw new PrivilegeException("User does not have enough rights.");
        if (principalId != null) {
            checkUserLock(communityDto.getBlockedUsers(), principalId);
        }

        return postService.findPaginatedByOwner(-1 * id, page, size);
    }

    @Override
    public PostDto updateCommunityPost(Long id, Long postId, Long principalId, PostDto postDto)
            throws EntityNotFoundException, PrivilegeException {

        CommunityDto communityDto = findCommunity(id);
        checkUserPower(communityDto.getOwner().getId(), principalId);
        PostDto post = postService.findPost(postId);
        post.setText(postDto.getText());
        post.setAttachments(postDto.getAttachments());
        postService.save(post);

        return post;
    }

    @Override
    public void deleteCommunityPost(Long id, Long postId, Long principalId) throws EntityNotFoundException,
            PrivilegeException, DbxException {

        CommunityDto communityDto = findCommunity(id);
        checkUserPower(communityDto.getOwner().getId(), principalId);
        PostDto postDto = postService.findPost(postId);
        DropboxClient.removeFolder(String.format("/community/%d/post/%d", id, postId));
        postRepository.delete(postId);
    }

    @Override
    public void uploadCommunityPostAttachments(Long id, Long postId, Long principalId, MultipartFile[] attachments)
            throws EntityNotFoundException, PrivilegeException, IOException, DbxException, UnsupportedFileTypeException {

        CommunityDto communityDto = findCommunity(id);
        checkUserPower(communityDto.getOwner().getId(), principalId);
        PostDto postDto = postService.findPost(postId);

        for (MultipartFile attachment : attachments) {
            String filename = attachment.getOriginalFilename();
            String fileExtension = AttachmentManager.getFileExtension(filename);
            AttachmentType type = AttachmentManager.getAttachmentType(fileExtension);

            if (type == null) throw new UnsupportedFileTypeException(
                    String.format("File extension %s not supported", fileExtension));

            String url = DropboxClient.uploadFile(attachment.getInputStream(),
                    String.format("/community/%d/post/%d/%s", id, postId, filename));

            postDto.getAttachments().add(new AttachmentDto(attachment.getOriginalFilename(), url, type));
        }

        Collections.sort(postDto.getAttachments());
        postService.save(postDto);
    }

    // -------------------------------------------Users--------------------------------------------------------------

    @Override
    public Page<CommunityDto> getUserPaginatedCommunities(Long userId, Integer page, Integer size)
            throws EntityNotFoundException {

        if (page == null && size == null) {
            page = 1;
            size = Integer.MAX_VALUE;
        }

        // Is user with userId exist?
        UserProfileDto userProfileDto = userService.findUserProfile(userId);
        Pageable pageRequest = new PageRequest(page - 1, size, Sort.Direction.DESC, "participantsCount");
        List<CommunityDto> communityDtoList = new ArrayList<>();
        Page<Community> communityPage = communityRepository.findAllByParticipantsId(userId, pageRequest);
        communityPage.getContent()
                .forEach(community -> communityDtoList.add(modelMapper.map(community, CommunityDto.class)));

        return new PageImpl<>(communityDtoList, pageRequest, communityPage.getTotalElements());
    }

    // -------------------------------------------Other--------------------------------------------------------------

    @Override
    public void uploadCommunityLogotype(Long id, Long principalId, MultipartFile logotype) throws EntityNotFoundException,
            PrivilegeException, IOException, DbxException, UnsupportedFileTypeException {

        CommunityDto communityDto = findCommunity(id);
        checkUserPower(communityDto.getOwner().getId(), principalId);
        String fileExtension = AttachmentManager.getFileExtension(logotype.getOriginalFilename());
        if (AttachmentManager.getAttachmentType(fileExtension) != AttachmentType.IMAGE)
            throw new UnsupportedFileTypeException(
                    String.format("File with extension %s can not be image", fileExtension));

        String url = DropboxClient.uploadFile(logotype.getInputStream(),
                String.format("/community/%d/logotype.jpg", id));

        // Verifies if shared link is already exist.
        if (url != null) {
            communityDto.setLogoUrl(url);
            communityRepository.save(modelMapper.map(communityDto, Community.class));
        }
    }

    @Override
    public void followCommunity(Long id, Long principalId) throws EntityNotFoundException,
            CommunityException, PrivilegeException {

        CommunityDto communityDto = findCommunity(id);
        checkUserLock(communityDto.getBlockedUsers(), principalId);
        if (!isCommunityMember(id, principalId)) {
            UserProfileDto userProfileDto = userService.findProfile(principalId);
            communityDto.getParticipants().add(userProfileDto);
            communityDto.setParticipantsCount(communityDto.getParticipants().size());
            communityRepository.save(modelMapper.map(communityDto, Community.class));
        } else throw new CommunityException(
                String.format("User with id %d already participant of community with id %d", principalId, id));
    }

    @Override
    public void unfollowCommunity(Long id, Long principalId) throws EntityNotFoundException,
            CommunityException, PrivilegeException {

        CommunityDto communityDto = findCommunity(id);
        checkUserLock(communityDto.getBlockedUsers(), principalId);
        UserProfileDto userDto = userService.findProfile(principalId);

        if (!communityDto.getParticipants().remove(userDto)) throw new CommunityException(
                String.format("User with id %d is not a member of this community", principalId));
        communityDto.setParticipantsCount(communityDto.getParticipants().size());

        communityRepository.save(modelMapper.map(communityDto, Community.class));
    }

    @Override
    public void blockUser(Long id, Long userId, Long principalId) throws EntityNotFoundException,
            CommunityException, PrivilegeException {

        CommunityDto communityDto = findCommunity(id);
        UserProfileDto userProfileDto = userService.findUserProfile(userId);
        Long communityOwnerId  = communityDto.getOwner().getId();
        if (communityOwnerId.equals(userId)) throw new CommunityException(
                String.format("User with id %d is community owner", userId));
        checkUserPower(communityOwnerId, principalId);

        if (communityDto.getParticipants().contains(userProfileDto)) {
            communityDto.getParticipants().remove(userProfileDto);
            communityDto.setParticipantsCount(communityDto.getParticipantsCount() - 1);
        }

        if (communityDto.getBlockedUsers().contains(userProfileDto)) throw new PrivilegeException(
                String.format("User with id %d already in community blacklist.", userId));

        communityDto.getBlockedUsers().add(userProfileDto);
        communityRepository.save(modelMapper.map(communityDto, Community.class));
    }

    @Override
    public void unblockUser(Long id, Long userId, Long principalId) throws EntityNotFoundException,
            CommunityException, PrivilegeException {

        CommunityDto communityDto = findCommunity(id);
        UserProfileDto userProfileDto = userService.findUserProfile(userId);
        checkUserPower(communityDto.getOwner().getId(), principalId);

        if (!communityDto.getBlockedUsers().remove(userProfileDto)) throw new CommunityException(
                String.format("User with id %d not in community blacklist.", userId));

        communityRepository.save(modelMapper.map(communityDto, Community.class));
    }

    // -------------------------------------------Private-------------------------------------------------------------

    /**
     * Verifies if the user is the owner of the community.
     *
     * @param ownerId id of owner of some community.
     * @param userId id of user.
     * @throws PrivilegeException if user is not owner of the community.
     */
    private void checkUserPower(Long ownerId, Long userId) throws PrivilegeException {
        if (!userId.equals(ownerId)) throw new PrivilegeException(
                String.format("User with id %d does not have enough rights.", userId));
    }

    /**
     * Verifies if the user is the participant of the desired community.
     *
     * @param id id of the desired community.
     * @param userId id of user.
     * @return true if user is participant, false if user is not participant.
     */
    private boolean isCommunityMember(Long id, Long userId) {
        Community community = communityRepository.findByIdAndParticipantsId(id, userId);
        return community != null;

    }

    /**
     * Verifies if the user is in blacklist.
     *
     * @param blacklist blacklist of some community.
     * @param userId id of user.
     * @throws PrivilegeException if user blocked in community.
     */
    private void checkUserLock(Set<UserProfileDto> blacklist, Long userId) throws PrivilegeException {
        for (UserProfileDto userProfileDto : blacklist) {
            if (userProfileDto.getId().equals(userId)) throw new PrivilegeException(
                    String.format("User with id %d does not have enough rights.", userId));
        }
    }
}
