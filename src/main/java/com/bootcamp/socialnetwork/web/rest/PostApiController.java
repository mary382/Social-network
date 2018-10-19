package com.bootcamp.socialnetwork.web.rest;

import com.bootcamp.socialnetwork.service.PostService;
import com.bootcamp.socialnetwork.service.SecurityService;
import com.bootcamp.socialnetwork.service.dto.PostDto;
import com.bootcamp.socialnetwork.util.Response;
import com.bootcamp.socialnetwork.web.rest.errors.*;
import com.dropbox.core.DbxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

/**
 * REST controller for managing posts.
 */
@RestController
@RequestMapping("/api")
public class PostApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostApiController.class);

    @Autowired
    private PostService postService;
    @Autowired
    private SecurityService securityService;

    /**
     * Create a new post.
     *
     * @param postDto the post to create.
     * @return the ResponseEntity with status 201 (Created) and with body containing
     * the new post, or with status 409 (Conflict) if the author and principal IDs do
     * not match or if the post ID is already in use.
     */
    @PostMapping("/post/")
    public ResponseEntity<?> createPost(@Valid @RequestBody PostDto postDto) {

        LOGGER.info("Creating post: {}.", postDto);

        try {
            postDto = postService.savePost(postDto);
        } catch (EntityAlreadyExistException | UserUnauthorizedException | PrivilegeException e) {
            LOGGER.error("Unable to create. {}", e.getMessage());
            return new ResponseEntity<>(new CustomError(e.getMessage()), HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(postDto, HttpStatus.CREATED);
    }

    /**
     * Get post by ID.
     *
     * @param id the ID of the post to find.
     * @return the ResponseEntity with status 200 (OK) and with body containing the post
     * found by ID, or with status 404 (Not Found).
     */
    @GetMapping("/post/{id}")
    public ResponseEntity<?> getPost(@PathVariable("id") Long id) {

        LOGGER.info("Fetching Post with ID {}.", id);

        try {
            PostDto postDto = postService.findPost(id);
            return new ResponseEntity<>(postDto, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            LOGGER.error("Post not found. {}", e.getMessage());
            return new ResponseEntity<>(new CustomError("Post not found"), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Get all posts.
     *
     * @return the ResponseEntity with status 200 (OK) and with body containing all posts,
     * or with status 204 (No Content).
     */
    @GetMapping("/post/")
    public ResponseEntity<List<PostDto>> getAllPosts() {

        LOGGER.info("Fetching all posts.");

        List<PostDto> posts = postService.findAll();
        if (posts.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    /**
     * Get all posts the user owns.
     *
     * @param id the ID of the profile.
     * @return the ResponseEntity with status 200 (OK) and with body containing all posts
     * the user owns, or with status 204 (No Content).
     */
    @GetMapping("/profile/{id}/post/")
    public ResponseEntity<List<PostDto>> listAllPostsUserOwns(@PathVariable("id") Long id) {

        LOGGER.info("Fetching all posts the user with ID {} owns.", id);

        List<PostDto> posts = postService.findAllByOwner(id);
        if (posts.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    /**
     * Update an existing post.
     *
     * @param id      the ID of the post to update.
     * @param postDto the data to be assigned to the post.
     * @return the ResponseEntity with status 200 (OK) and with body containing the updated post,
     * or with status 404 (Not Found) if the post couldn't be found, or with status 403 (Forbidden)
     * if the client has no rights to update the post.
     */
    @PutMapping("/post/{id}")
    public ResponseEntity<?> updatePost(@PathVariable("id") Long id, @Valid @RequestBody PostDto postDto) {

        LOGGER.info("Updating post with ID {}.", id);

        try {
            postDto = postService.update(id, postDto);
            return new ResponseEntity<>(postDto, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            LOGGER.error("Unable to update. {}", e.getMessage());
            return new ResponseEntity<>(new CustomError(e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (UserUnauthorizedException | PrivilegeException e) {
            LOGGER.error("Unable to update. {}", e.getMessage());
            return new ResponseEntity<>(new CustomError(e.getMessage()), HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Uploads attachments for disered post of desired community.
     *
     * @param id          id of the desired post.
     * @param attachments some count of files.
     * @return the ResponseEntity with status 200 (OK),
     * or with status 401 (Unauthorized),
     * or with status 404 (Not Found) if the community or post couldn't be found,
     * or with status 403 (Forbidden) if the current user doesn't have enough rights,
     * or with status 500 (Internal Server Error),
     * or with status 400 (Bad Request).
     */
    @PostMapping("/post/{id}/attach")
    public ResponseEntity<Response> uploadPostAttachments(@PathVariable("id") Long id,
                                                          @RequestParam("attachments") MultipartFile[] attachments) {
        try {
            if (attachments.length > 10) throw new Exception("The maximum count of attachments is 10");
            Long principalId = securityService.getPrincipalId();
            securityService.verifyUserAuthorization(principalId);
            postService.uploadPostAttachments(id, principalId, attachments);
        } catch (UserUnauthorizedException e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(
                    new Response(null, null, e.getMessage()),
                    HttpStatus.UNAUTHORIZED);
        } catch (EntityNotFoundException e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(
                    new Response(null, null, e.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (PrivilegeException e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(
                    new Response(null, null, e.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (IOException | DbxException e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(
                    new Response(null, null, e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(
                    new Response(null, null, e.getMessage()),
                    HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(
                new Response(null, null, null),
                HttpStatus.OK);
    }

    /**
     * Delete the post by ID.
     *
     * @param id the ID of the post to delete.
     * @return the ResponseEntity with status 204 (No Content) if the post deleted successfully,
     * or with status 404 (Not Found) if the post couldn't be found, or with status 403 (Forbidden)
     * if the client has no rights to delete the post.
     */
    @DeleteMapping("/post/{id}")
    public ResponseEntity<?> deletePost(@PathVariable("id") Long id) {

        LOGGER.info("Deleting post with ID {}.", id);

        try {
            Long principalId = securityService.getPrincipalId();
            securityService.verifyUserAuthorization(principalId);
            postService.delete(id, principalId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (UserUnauthorizedException e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(
                    new Response(null, null, e.getMessage()),
                    HttpStatus.UNAUTHORIZED);
        } catch (EntityNotFoundException e) {
            LOGGER.error("Unable to delete. {}", e.getMessage());
            return new ResponseEntity<>(new CustomError(e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (PrivilegeException e) {
            LOGGER.error("Unable to delete. {}", e.getMessage());
            return new ResponseEntity<>(new CustomError(e.getMessage()), HttpStatus.CONFLICT);
        }
    }
}
