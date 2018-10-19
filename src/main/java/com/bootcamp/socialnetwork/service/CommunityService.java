package com.bootcamp.socialnetwork.service;

import com.bootcamp.socialnetwork.service.dto.CommunityDto;
import com.bootcamp.socialnetwork.service.dto.PostDto;
import com.bootcamp.socialnetwork.web.rest.errors.CommunityException;
import com.bootcamp.socialnetwork.web.rest.errors.EntityNotFoundException;
import com.bootcamp.socialnetwork.web.rest.errors.PrivilegeException;
import com.bootcamp.socialnetwork.web.rest.errors.UnsupportedFileTypeException;
import com.dropbox.core.DbxException;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface CommunityService {

    // ------------------------------------------Basic--------------------------------------------------------------

    /**
     * Finds desired community in database.
     *
     * @param id id of the desired community.
     * @return desired community.
     * @throws EntityNotFoundException if desired community not found in database.
     */
    CommunityDto findCommunity(Long id) throws EntityNotFoundException;

    /**
     * Creates a new community.
     *
     * @param communityDto data for new community.
     * @param principalId authorized user id.
     * @throws DbxException
     */
    void createCommunity(CommunityDto communityDto, Long principalId) throws DbxException;

    /**
     * Gets desired community.
     *
     * @param id id of the desired community.
     * @param principalId authorized user id.
     * @return desired community.
     * @throws EntityNotFoundException if desired community not found.
     * @throws PrivilegeException if desired community is closed,
     * or if authorized user blocked in desired community.
     */
    CommunityDto getCommunity(Long id, Long principalId) throws EntityNotFoundException, PrivilegeException;

    /**
     * Gets all communities in paginated format.
     *
     * @param page number of the desired page.
     * @param size amount of desired objects on page.
     * @return page with communities.
     */
    Page<CommunityDto> getPaginatedCommunities(Integer page, Integer size);

    /**
     * Updates desired community.
     *
     * @param id id of the desired community.
     * @param communityDto new data for desired community.
     * @param principalId authorized user id.
     * @return updated community.
     * @throws EntityNotFoundException if desired community not found.
     * @throws PrivilegeException if authorized user doesn't have right to update desired community.
     */
    CommunityDto updateCommunity(Long id, CommunityDto communityDto, Long principalId) throws EntityNotFoundException,
            PrivilegeException, IOException, DbxException;

    /**
     * Deletes desired community.
     *
     * @param id id of the desired community.
     * @param principalId authorized user id.
     * @throws EntityNotFoundException if desired community not found.
     * @throws PrivilegeException if authorized user doesn't have right to delete desired community.
     */
    void deleteCommunity(Long id, Long principalId) throws EntityNotFoundException,
            PrivilegeException, DbxException;

    // ------------------------------------------Posts--------------------------------------------------------------

    /**
     * Creates a new post in desired community.
     *
     * @param id id of the desired community.
     * @param postDto data for new post.
     * @param principalId authorized user id.
     * @throws EntityNotFoundException if desired community not found.
     * @throws PrivilegeException if authorized user doesn't have right to create new post in desired community.
     */
    PostDto createCommunityPost(Long id, PostDto postDto, Long principalId) throws EntityNotFoundException,
            PrivilegeException, DbxException;

    /**
     * Gets desired post in desired community.
     *
     * @param id id of the desired community.
     * @param postId id of the desired post.
     * @param principalId authorized user id.
     * @return desired post.
     * @throws EntityNotFoundException if desired community or desired post not found.
     * @throws PrivilegeException if desired community is closed,
     * or if authorized user blocked in desired community.
     */
    PostDto getCommunityPost(Long id, Long postId, Long principalId) throws EntityNotFoundException, PrivilegeException;

    /**
     * Gets all posts paginated for desired community.
     *
     * @param id id of the desired community.
     * @param principalId authorized user id.
     * @param page number of the desired page.
     * @param size amount of desired objects on page.
     * @return page with posts of desired community.
     * @throws EntityNotFoundException if desired community not found.
     * @throws PrivilegeException if desired community is closed,
     * or if authorized user blocked in desired community.
     */
    Page<PostDto> getCommunityPaginatedPosts(Long id, Long principalId, Integer page, Integer size)
            throws EntityNotFoundException, PrivilegeException;

    /**
     * Updates desired post in desired community.
     *
     * @param id id of the desired community.
     * @param postId id of the desired post.
     * @param principalId authorized user id.
     * @param postDto new data for desired post.
     * @return updated post.
     * @throws EntityNotFoundException if desired community or desired post not found.
     * @throws PrivilegeException if authorized user doesn't have right to update desired post in desired community.
     */
    PostDto updateCommunityPost(Long id, Long postId, Long principalId, PostDto postDto) throws EntityNotFoundException,
            PrivilegeException;

    /**
     * Deletes desired post in desired community.
     *
     * @param id id of the desired community.
     * @param postId id of the desired post.
     * @param principalId authorized user id.
     * @throws EntityNotFoundException if desired community or desired post not found.
     * @throws PrivilegeException if authorized user doesn't have right to delete desired post in desired community.
     */
    void deleteCommunityPost(Long id, Long postId, Long principalId) throws EntityNotFoundException,
            PrivilegeException, DbxException;


    void uploadCommunityPostAttachments(Long id, Long postId, Long principalId, MultipartFile[] attachments)
            throws EntityNotFoundException, PrivilegeException, IOException, DbxException, UnsupportedFileTypeException;

    // ------------------------------------------Users--------------------------------------------------------------

    /**
     * Gets all communities of desired user in paginated format.
     *
     * @param userId id of the desired user.
     * @param page number of the desired page.
     * @param size amount of desired objects on page.
     * @return page with communities of desired user.
     * @throws EntityNotFoundException if desired user not found.
     */
    Page<CommunityDto> getUserPaginatedCommunities(Long userId, Integer page, Integer size)
            throws EntityNotFoundException;

    // ------------------------------------------Other--------------------------------------------------------------

    /**
     * Attaches authorized user to desired community.
     *
     * @param id id of the desired community.
     * @param principalId authorized user id.
     * @throws EntityNotFoundException if desired community not found.
     * @throws CommunityException if authorized user is already follower.
     * @throws PrivilegeException if authorized user blocked in desired community.
     */
    void followCommunity(Long id, Long principalId) throws EntityNotFoundException,
            CommunityException, PrivilegeException;

    /**
     * Unfollow authorized user from desired community.
     *
     * @param id id of the desired community.
     * @param principalId authorized user id.
     * @throws EntityNotFoundException if desired community not found.
     * @throws CommunityException if authorized user not follower.
     * @throws PrivilegeException if authorized user blocked in desired community.
     */
    void unfollowCommunity(Long id, Long principalId) throws EntityNotFoundException,
            CommunityException, PrivilegeException;

    /**
     * Blocks desired user in the desired community.
     *
     * @param id id of the desired community.
     * @param userId id of the desired user.
     * @param principalId authorized user id.
     * @throws EntityNotFoundException if desired community or desired user not found.
     * @throws CommunityException if desired user is owner of community.
     * @throws PrivilegeException if desired user already blocked in desired community,
     * or if authorized user doesn't have right to block desired user.
     */
    void blockUser(Long id, Long userId, Long principalId) throws EntityNotFoundException,
            CommunityException, PrivilegeException;

    /**
     * Unblocks desired user in the desired community.
     *
     * @param id id of the desired community.
     * @param userId id of the desired user.
     * @param principalId authorized user id.
     * @throws EntityNotFoundException if desired community or desired user not found.
     * @throws CommunityException if desired user not in blacklist.
     * @throws PrivilegeException if authorized user doesn't have right to unblock desired user.
     */
    void unblockUser(Long id, Long userId, Long principalId) throws EntityNotFoundException,
            CommunityException, PrivilegeException;

    void uploadCommunityLogotype(Long id, Long principalId, MultipartFile logotype) throws EntityNotFoundException,
            PrivilegeException, IOException, DbxException, UnsupportedFileTypeException;
}
