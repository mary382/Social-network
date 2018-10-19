package com.bootcamp.socialnetwork.service;

import com.bootcamp.socialnetwork.service.dto.PostDto;
import com.bootcamp.socialnetwork.web.rest.errors.*;
import com.dropbox.core.DbxException;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Service class for managing posts.
 */
public interface PostService {

    boolean isExist(Long id);

    PostDto find(Long id);

    PostDto findPost(Long id) throws EntityNotFoundException;

    List<PostDto> findAll();

    List<PostDto> findAllByOwner(Long ownerId);

    /**
     * Gets posts of desired owner in paginated format.
     *
     * @param ownerId id of the desired owner.
     * @param page    number of the desired page.
     * @param size    amount of desired objects on page.
     * @return page with posts of desired owner.
     */
    Page<PostDto> findPaginatedByOwner(Long ownerId, Integer page, Integer size);

    PostDto save(PostDto postDto);

    PostDto savePost(PostDto postDto)
            throws EntityAlreadyExistException, UserUnauthorizedException, PrivilegeException;

    PostDto update(Long id, PostDto postDto)
            throws EntityNotFoundException, UserUnauthorizedException, PrivilegeException;

    void delete(Long id, Long principalId) throws EntityNotFoundException, PrivilegeException;

    void deleteAll();

    void deleteAllByOwner(Long ownerId);

    Page<PostDto> findPaginatedNews(Long userId, Integer page, Integer size) throws EntityNotFoundException;

    void uploadPostAttachments(Long postId, Long principalId, MultipartFile[] attachments)
            throws EntityNotFoundException, PrivilegeException, IOException, DbxException, UnsupportedFileTypeException;
}
