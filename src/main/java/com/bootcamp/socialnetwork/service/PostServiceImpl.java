package com.bootcamp.socialnetwork.service;

import com.bootcamp.socialnetwork.domain.Post;
import com.bootcamp.socialnetwork.repository.PostRepository;
import com.bootcamp.socialnetwork.service.dto.AttachmentDto;
import com.bootcamp.socialnetwork.service.dto.CommunityDto;
import com.bootcamp.socialnetwork.service.dto.PostDto;
import com.bootcamp.socialnetwork.service.dto.UserProfileDto;
import com.bootcamp.socialnetwork.util.AttachmentManager;
import com.bootcamp.socialnetwork.util.AttachmentType;
import com.bootcamp.socialnetwork.util.DropboxClient;
import com.bootcamp.socialnetwork.web.rest.errors.*;
import com.dropbox.core.DbxException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service("postService")
@Transactional
public class PostServiceImpl implements PostService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommunityService communityService;

    @Autowired
    private FriendshipService friendshipService;

    @Autowired
    private SecurityService securityService;


    @Override
    public boolean isExist(Long id) {
        return postRepository.exists(id);
    }

    @Override
    public PostDto find(Long id) {

        Post post = postRepository.findOne(id);
        return (post == null) ?
                null : modelMapper.map(post, PostDto.class);
    }

    @Override
    public PostDto findPost(Long id) throws EntityNotFoundException {

        Post post = postRepository.findOne(id);

        if (post == null) {
            throw new EntityNotFoundException("Post not found.");
        }

        return modelMapper.map(post, PostDto.class);
    }

    @Override
    public List<PostDto> findAll() {

        List<PostDto> posts = new ArrayList<>();
        for (Post post : postRepository.findAll()) {
            posts.add(modelMapper.map(post, PostDto.class));
        }

        return posts;
    }

    @Override
    public List<PostDto> findAllByOwner(Long ownerId) {

        List<PostDto> posts = new ArrayList<>();
        for (Post post : postRepository.findAllByOwnerId(ownerId)) {
            posts.add(modelMapper.map(post, PostDto.class));
        }

        return posts;
    }

    @Override
    public Page<PostDto> findPaginatedByOwner(Long ownerId, Integer page, Integer size) {

        if (page == null && size == null) {
            size = Integer.MAX_VALUE;
            page = 1;
        }

        // page - 1, because indexing from 0
        Pageable pageRequest = new PageRequest(page - 1, size, Sort.Direction.DESC, "time");
        List<PostDto> postDtoList = new ArrayList<>();
        Page<Post> postPage = postRepository.findAllByOwnerId(ownerId, pageRequest);
        postPage.getContent()
                .forEach(post -> postDtoList.add(modelMapper.map(post, PostDto.class)));

        return new PageImpl<>(postDtoList, pageRequest, postPage.getTotalElements());
    }

    @Override
    public PostDto save(PostDto postDto) {

        Post post = modelMapper.map(postDto, Post.class);
        post.setEnabled(true);

        postRepository.save(post);
        return modelMapper.map(post, PostDto.class);
    }

    @Override
    public PostDto savePost(PostDto postDto)
            throws EntityAlreadyExistException, UserUnauthorizedException, PrivilegeException {

        if (postDto.getId() != null && isExist(postDto.getId())) {
            throw new EntityAlreadyExistException("Post with this ID already exist.");
        }
        if (!postDto.getAuthor().getId().equals(securityService.getPrincipalProfile().getId())) {
            throw new PrivilegeException("The author id does not match the principal id.");
        }
        if (friendshipService.inBlackList(postDto.getOwnerId(), postDto.getAuthor().getId())) {
            throw new PrivilegeException("You are blocked by user.");
        }
        Post post = modelMapper.map(postDto, Post.class);
        post.setEnabled(true);
        post.setTime(System.currentTimeMillis());

        postRepository.save(post);
        return modelMapper.map(post, PostDto.class);
    }

    @Override
    public PostDto update(Long id, PostDto postDto)
            throws EntityNotFoundException, UserUnauthorizedException, PrivilegeException {

        PostDto post = findPost(id);

        if (!post.getAuthor().getId().equals(securityService.getPrincipalProfile().getId())) {
            throw new PrivilegeException("User can not update a post without being its author.");
        }
        if (friendshipService.inBlackList(postDto.getOwnerId(), postDto.getAuthor().getId())) {
            throw new PrivilegeException("You are blocked by user.");
        }
        post.setText(postDto.getText());
        post.setAttachments(postDto.getAttachments());
        save(post);

        return post;
    }

    @Override
    public void delete(Long id, Long principalId) throws EntityNotFoundException, PrivilegeException {

        PostDto post = findPost(id);

        if (!post.getAuthor().getId().equals(principalId) && !post.getOwnerId().equals(principalId)) {
            throw new PrivilegeException("User don't have enough rights to delete a post.");
        }
        if (friendshipService.inBlackList(post.getOwnerId(), post.getAuthor().getId())) {
            throw new PrivilegeException("You are blocked by user.");
        }
        postRepository.delete(id);
    }

    @Override
    public void deleteAll() {
        postRepository.deleteAll();
    }

    @Override
    public void deleteAllByOwner(Long ownerId) {
        postRepository.deleteAllByOwnerId(ownerId);
    }

    @Override
    public Page<PostDto> findPaginatedNews(Long userId, Integer page, Integer size) throws EntityNotFoundException {

        List<PostDto> postDtoList = new ArrayList<>();
        communityService.getUserPaginatedCommunities(userId, null, null)
                .forEach(communityDto -> postDtoList.addAll(
                        findPaginatedByOwner(-1 * communityDto.getId(), null, null).getContent()
                ));

        friendshipService.getFriends(userId, 1, Integer.MAX_VALUE)
                .forEach(userProfileDto -> postDtoList.addAll(
                        findPaginatedByOwner(userProfileDto.getId(), null, null).getContent()
                ));

        postDtoList.addAll(findPaginatedByOwner(userId, null, null).getContent());

        if (page == null && size == null) {
            size = postDtoList.size();
            page = 1;
        }
        if (size == 0) return null;

        // Sorts in reverse order by time.
        Collections.sort(postDtoList);

        int fromIndex = (page - 1) * size;
        int toIndex;
        // For situation, when page=3, size=3 and postDtoList.size() equals 7
        if ((toIndex = (page - 1) * size + size) > postDtoList.size())
            toIndex = postDtoList.size();

        return new PageImpl<>(postDtoList.subList(fromIndex, toIndex),
                new PageRequest(page - 1, size),
                postDtoList.size());
    }

    @Override
    public void uploadPostAttachments(Long postId, Long principalId, MultipartFile[] attachments)
            throws EntityNotFoundException, PrivilegeException, IOException, DbxException, UnsupportedFileTypeException {

        PostDto postDto = find(postId);
        if (!postDto.getAuthor().getId().equals(principalId)) {
            throw new PrivilegeException("User can not upload attachments to post without being its author.");
        }

        for (int i = 0; i < attachments.length; i++) {
            String filename = attachments[i].getOriginalFilename();
            String fileExtension = AttachmentManager.getFileExtension(filename);
            AttachmentType type = AttachmentManager.getAttachmentType(fileExtension);

            if (type == null) throw new UnsupportedFileTypeException(
                    String.format("File extension %s not supported", fileExtension));

            String url = DropboxClient.uploadFile(attachments[i].getInputStream(),
                    String.format("/post/%d/%s", postId, filename));

            postDto.getAttachments().add(new AttachmentDto(attachments[i].getOriginalFilename(), url, type));
        }

        Collections.sort(postDto.getAttachments());
        save(postDto);
    }

}
