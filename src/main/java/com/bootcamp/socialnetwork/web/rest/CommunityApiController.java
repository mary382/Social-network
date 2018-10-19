package com.bootcamp.socialnetwork.web.rest;

import com.bootcamp.socialnetwork.service.CommunityService;
import com.bootcamp.socialnetwork.service.SecurityService;
import com.bootcamp.socialnetwork.service.dto.CommunityDto;
import com.bootcamp.socialnetwork.service.dto.PostDto;
import com.bootcamp.socialnetwork.util.Response;
import com.bootcamp.socialnetwork.web.rest.errors.*;
import com.dropbox.core.DbxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.io.IOException;

/**
 * REST controller for managing communities.
 *
 * @author Pavel Kasper
 */
@RestController
@RequestMapping("/api/community")
public class CommunityApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommunityApiController.class);

    @Autowired
    private CommunityService communityService;

    @Autowired
    private SecurityService securityService;

    // ------------------------------------------Basic--------------------------------------------------------------

    /**
     * Creates a new community.
     * <p>
     * Request example: http://localhost:8080/api/community/, method=POST
     *
     * @param communityDto the community to create.
     * @param uriComponentsBuilder the URI components builder.
     * @return the ResponseEntity with status 201 (Created),
     * or with status 401 (Unauthorized),
     * or with status 500 (Internal Server Error).
     */
    @PostMapping("")
    public ResponseEntity<?> createCommunity(@Valid @RequestBody CommunityDto communityDto,
                                             UriComponentsBuilder uriComponentsBuilder) {

        LOGGER.debug("Creating community : {}.", communityDto);
        try {
            Long principalId = securityService.getPrincipalId();
            securityService.verifyUserAuthorization(principalId);
            communityService.createCommunity(communityDto, principalId);
        } catch (UserUnauthorizedException e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(
                    new Response(null, null, e.getMessage()),
                    HttpStatus.UNAUTHORIZED);
        } catch (DbxException e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(
                    new Response(null, null, e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(uriComponentsBuilder
                .path("/api/community/{title}")
                .buildAndExpand(communityDto.getTitle())
                .toUri());

        return new ResponseEntity<>(
                new Response(null, null, null),
                headers,
                HttpStatus.CREATED);
    }

    /**
     * Gets desired community.
     * <p>
     * Request example: http://localhost:8080/api/community/1, method=GET
     *
     * @param id id of the desired community.
     * @return the ResponseEntity with status 200 (OK),
     * or with status 404 (Not Found) if community doesn't exist,
     * or with status 403 (Forbidden) if the current user blocked in community.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getCommunity(@PathVariable("id") Long id) {

        LOGGER.debug("Fetching community with id {}.", id);
        CommunityDto communityDto;
        try {
            Long principalId = securityService.getPrincipalId();
            communityDto = communityService.getCommunity(id, principalId);
        } catch (EntityNotFoundException e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(
                    new Response(null, null, e.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (PrivilegeException e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(new Response(null, null, e.getMessage()),
                    HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<>(
                new Response(communityDto, null, null),
                HttpStatus.OK);
    }

    /**
     * Gets all communities paginated.
     * <p>
     * Request example: http://localhost:8080/api/community?page=1&size=2, method=GET
     *
     * @param page number of the desired page.
     * @param size amount of desired objects on page.
     * @return the ResponseEntity with status 200 (OK),
     * or with status 204 (No Content) if communities not found.
     */
    @GetMapping("")
    public ResponseEntity<Response> getPaginatedCommunities(@RequestParam(value = "page", required = false) Integer page,
                                                            @RequestParam(value = "size", required = false) Integer size) {

        LOGGER.debug("Fetching communities.");
        if ((page != null && size == null) || (page == null && size != null)) {
            return new ResponseEntity<>(
                    new Response(null, null, "Invalid parameter set"),
                    HttpStatus.BAD_REQUEST);
        }

        Page<CommunityDto> communityDtoPage = communityService.getPaginatedCommunities(page, size);
        if (communityDtoPage == null) return new ResponseEntity<>(
                new Response(null, null, "Communities not found"),
                HttpStatus.NO_CONTENT);

        return new ResponseEntity<>(
                new Response(communityDtoPage, null, null),
                HttpStatus.OK);
    }

    /**
     * Updates desired community.
     * <p>
     * Request example: http://localhost:8080/api/community/1, method=PUT
     *
     * @param id id of the desired community.
     * @param communityDto new data for community.
     * @return the ResponseEntity with status 200 (OK),
     * or with status 401 (Unauthorized),
     * or with status 404 (Not Found) if the community doesn't exist,
     * or with status 403 (Forbidden) if the current user doesn't have enough rights.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCommunity(@PathVariable("id") Long id, @RequestBody CommunityDto communityDto) {

        LOGGER.debug("Updating community with id {}.", id);
        try {
            Long principalId = securityService.getPrincipalId();
            securityService.verifyUserAuthorization(principalId);
            communityDto = communityService.updateCommunity(id, communityDto, principalId);
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
        }

        return new ResponseEntity<>(
                new Response(communityDto, null, null),
                HttpStatus.OK);
    }

    /**
     * Deletes desired community.
     * <p>
     * Request example: http://localhost:8080/api/community/1, method=DELETE
     *
     * @param id id of the desired community.
     * @return the ResponseEntity with status 200 (OK),
     * or with status 401 (Unauthorized),
     * or with status 404 (Not Found) if the community couldn't be found,
     * or with status 403 (Forbidden) if the current user doesn't have enough rights,
     * or with status 500 (Internal Server Error).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCommunity(@PathVariable("id") Long id) {

        LOGGER.debug("Fetching & Deleting community with id {}.", id);
        try {
            Long principalId = securityService.getPrincipalId();
            securityService.verifyUserAuthorization(principalId);
            communityService.deleteCommunity(id, principalId);
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
        } catch (DbxException e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(
                    new Response(null, null, e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(
                new Response(null, null, null),
                HttpStatus.OK);
    }

    // ------------------------------------------Posts--------------------------------------------------------------

    /**
     * Creates post in desired community.
     * <p>
     * Request example: http://localhost:8080/api/community/1/post, method=POST
     *
     * @param id id of the desired community.
     * @param postDto the post to create.
     * @param uriComponentsBuilder the URI components builder.
     * @return the Response Entity with status 201 (Created),
     * or with status 404 (Not Found) if the community couldn't be found,
     * or with status 401 (Unauthorized),
     * or with status 403 (Forbidden) if the current user doesn't have enough rights,
     * or with status 500 (Internal Server Error).
     */
    @PostMapping("/{id}/post")
    public ResponseEntity<Response> createCommunityPost(@PathVariable("id") Long id,
                                                        @Valid @RequestBody PostDto postDto,
                                                        UriComponentsBuilder uriComponentsBuilder) {
        PostDto post = new PostDto();
        LOGGER.debug("Creating post {} in community with id {}", postDto, id);
        try {
            Long principalId = securityService.getPrincipalId();
            securityService.verifyUserAuthorization(principalId);
            post = communityService.createCommunityPost(id, postDto, principalId);
        } catch (EntityNotFoundException e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(
                    new Response(null, null, e.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (UserUnauthorizedException e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(
                    new Response(null, null, e.getMessage()),
                    HttpStatus.UNAUTHORIZED);
        } catch (PrivilegeException e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(
                    new Response(null, null, e.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (DbxException e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(
                    new Response(null, null, e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(uriComponentsBuilder
                .path("/api/community/{id}/post")
                .buildAndExpand(id)
                .toUri());

        return new ResponseEntity<>(
                new Response(post, null, null),
                headers,
                HttpStatus.CREATED);
    }

    /**
     * Gets desired post of desired community.
     * <p>
     * Request example: http://localhost:8080/api/community/1/post?postId=1, method=GET
     *
     * @param id id of the desired community.
     * @param postId id of the desired post.
     * @return the Response Entity with status 200 (OK),
     * or with status 404 (Not Found) if the community  or post couldn't be found,
     * or with status 403 (Forbidden) if the current user doesn't have enough rights.
     */
    @GetMapping(value = "/{id}/post", params = "postId")
    public ResponseEntity<Response> getCommunityPost(@PathVariable("id") Long id, @RequestParam("postId") Long postId) {

        LOGGER.debug("Fetching post with id {} from community with id {}", postId, id);
        PostDto postDto;
        try {
            Long principalId = securityService.getPrincipalId();
            postDto = communityService.getCommunityPost(id, postId, principalId);
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
        }

        return new ResponseEntity<>(
                new Response(postDto, null, null),
                HttpStatus.OK);
    }

    /**
     * Gets posts paginated for desired community.
     * <p>
     * Request example: http://localhost:8080/api/community/1/post?page=1&size=2, method=GET
     *
     * @param id id of the desired community.
     * @param page number of the desired page.
     * @param size amount of desired objects on page.
     * @return the ResponseEntity with status 200 (OK),
     * or with status 404 (Not Found) if the community couldn't be found,
     * or with status 403 (Forbidden) if the current user blocked in community,
     * or with status 204 (No Content) if communities not found.
     */
    @GetMapping("/{id}/post")
    public ResponseEntity<Response> getCommunityPaginatedPosts(@PathVariable("id") Long id,
                                                               @RequestParam(value = "page", required = false) Integer page,
                                                               @RequestParam(value = "size", required = false) Integer size) {

        LOGGER.debug("Fetching posts from community with id {}", id);
        if ((page != null && size == null) || (page == null && size != null)) {
            return new ResponseEntity<>(
                    new Response(null, null, "Invalid parameter set"),
                    HttpStatus.BAD_REQUEST);
        }

        Page<PostDto> postDtoPage;
        try {
            Long principalId = securityService.getPrincipalId();
            postDtoPage = communityService.getCommunityPaginatedPosts(id, principalId, page, size);
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
        }

        if (postDtoPage.getContent().isEmpty()) return new ResponseEntity<>(
                new Response(null, null, "Posts not found"),
                HttpStatus.NO_CONTENT);

        return new ResponseEntity<>(
                new Response(postDtoPage, null, null),
                HttpStatus.OK);
    }

    /**
     * Updates desired post of desired community.
     * <p>
     * Request example: http://localhost:8080/api/community/1/post?postId=1, method=PUT
     *
     * @param id id of the desired community.
     * @param postId id of the desired post.
     * @param postDto new data for post.
     * @return the Response Entity with status 200 (OK),
     * or with status 404 (Not Found) if the community or post couldn't be found,
     * or with status 403 (Forbidden) if the current user doesn't have enough rights,
     * or with status 401 (Unauthorized).
     */
    @PutMapping(value = "/{id}/post", params = "postId")
    public ResponseEntity<Response> updateCommunityPost(@PathVariable("id") Long id,
                                                        @RequestParam("postId") Long postId,
                                                        @Valid @RequestBody PostDto postDto) {

        LOGGER.debug("Updating post with id {} from community with id {}", postId, id);
        try {
            Long principalId = securityService.getPrincipalId();
            securityService.verifyUserAuthorization(principalId);
            postDto = communityService.updateCommunityPost(id, postId, principalId, postDto);
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
        } catch (UserUnauthorizedException e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(
                    new Response(null, null, e.getMessage()),
                    HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(
                new Response(postDto, null, null),
                HttpStatus.OK);
    }

    /**
     * Deletes the desired post of desired community.
     * <p>
     * Request example: http://localhost:8080/api/community/1/post?postId=1, method=DELETE
     *
     * @param id id of the desired community.
     * @param postId id of the desired post.
     * @return the Response Entity with status 200 (OK),
     * or with status 404 (Not Found) if the community or post couldn't be found,
     * or with status 403 (Forbidden) if the current user doesn't have enough rights,
     * or with status 401 (Unauthorized),
     * or with status 500 (Internal Server Error).
     */
    @DeleteMapping(value = "/{id}/post", params = "postId")
    public ResponseEntity<Response> deleteCommunityPost(@PathVariable("id") Long id,
                                                        @RequestParam("postId") Long postId) {

        LOGGER.debug("Fetching & Deleting post with id {} from community with id {}", postId, id);
        try {
            Long principalId = securityService.getPrincipalId();
            securityService.verifyUserAuthorization(principalId);
            communityService.deleteCommunityPost(id, postId, principalId);
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
        } catch (UserUnauthorizedException e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(
                    new Response(null, null, e.getMessage()),
                    HttpStatus.UNAUTHORIZED);
        } catch (DbxException e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(
                    new Response(null, null, e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(
                new Response(null, null, null),
                HttpStatus.OK);
    }

    /**
     * Uploads attachments for desired post of desired community.
     *
     * @param id id of the desired community.
     * @param postId id of the desired post.
     * @param attachments some count of files.
     * @return the ResponseEntity with status 200 (OK),
     * or with status 401 (Unauthorized),
     * or with status 404 (Not Found) if the community or post couldn't be found,
     * or with status 403 (Forbidden) if the current user doesn't have enough rights,
     * or with status 500 (Internal Server Error),
     * or with status 400 (Bad Request).
     */
    @PostMapping("/{id}/post/attach")
    public ResponseEntity<Response> uploadCommunityPostAttachments(@PathVariable("id") Long id,
                                                                   @RequestParam("postId") Long postId,
                                                                   @RequestParam("attachments") MultipartFile[] attachments) {
        try {
            if (attachments.length > 10) throw new Exception("The maximum count of attachments is 10");
            Long principalId = securityService.getPrincipalId();
            securityService.verifyUserAuthorization(principalId);
            communityService.uploadCommunityPostAttachments(id, postId, principalId, attachments);
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

    // ------------------------------------------Other--------------------------------------------------------------

    /**
     * Attaches authorized user to desired community.
     * <p>
     * Request example: http://localhost:8080/api/community/1/follow, method=PUT
     *
     * @param id id of the desired community.
     * @return the ResponseEntity with status 200 (OK),
     * or with status 401 (Unauthorized),
     * or with status 404 (Not Found) if the community doesn't exist,
     * or with status 403 (Forbidden) if the current user blocked in community,
     * or with status 409 (Conflict) if the user already follows community.
     */
    @PutMapping("/{id}/follow")
    public ResponseEntity<?> followCommunity(@PathVariable("id") Long id) {

        LOGGER.debug("Attach current user to community with id {}.", id);
        try {
            Long principalId = securityService.getPrincipalId();
            securityService.verifyUserAuthorization(principalId);
            communityService.followCommunity(id, principalId);
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
        } catch (CommunityException e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(
                    new Response(null, null, e.getMessage()),
                    HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(
                new Response(null, null, null),
                HttpStatus.OK);
    }

    /**
     * Unfollow authorized user from desired community.
     * <p>
     * Request example: http://localhost:8080/api/community/1/unfollow, method=PUT
     *
     * @param id id of the desired community.
     * @return the ResponseEntity with status 200 (OK),
     * or with the status 401 (Unauthorized),
     * or with status 404 (Not Found) if the community couldn't be found,
     * or with status 403 (Forbidden) if the current user blocked in community,
     * or with the status 409 (Conflict) if the current user not a community participant.
     */
    @PutMapping("/{id}/unfollow")
    public ResponseEntity<?> unfollowCommunity(@PathVariable("id") Long id) {

        LOGGER.debug("Unfollow current user from community with id {}.", id);
        try {
            Long principalId = securityService.getPrincipalId();
            securityService.verifyUserAuthorization(principalId);
            communityService.unfollowCommunity(id, principalId);
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
        } catch (CommunityException e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(
                    new Response(null, null, null),
                    HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(
                new Response(null, null, null),
                HttpStatus.OK);
    }

    /**
     * Blocks a user in the desired community.
     * <p>
     * Request example: http://localhost:8080/api/community/1/block?userId=1, method=GET
     *
     * @param id id of the desired community.
     * @param userId id of the desired user.
     * @return the ResponseEntity with status 200 (OK),
     * or with status 401 (Unauthorized),
     * or with status 404 (Not Found) if the community couldn't be found,
     * or with status 403 (Forbidden) if the current user doesn't have enough rights,
     * or with status 409 (Conflict) if the current user already blocked in the community.
     */
    @PutMapping(value = "/{id}/block", params = "userId")
    public ResponseEntity<?> blockUser(@PathVariable("id") Long id, @RequestParam("userId") Long userId) {

        LOGGER.debug("Block user with id {} from community with id {}", userId, id);
        try {
            Long principalId = securityService.getPrincipalId();
            securityService.verifyUserAuthorization(principalId);
            communityService.blockUser(id, userId, principalId);
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
        } catch (CommunityException e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(
                    new Response(null, null, e.getMessage()),
                    HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(
                new Response(null, null, null),
                HttpStatus.OK);
    }

    /**
     * Unblocks a user in the desired community.
     * <p>
     * Request example: http://localhost:8080/api/community/1/unblock?userId=1, method=PUT
     *
     * @param id id of the desired community.
     * @param userId id of the desired user.
     * @return the ResponseEntity with status 200 (OK),
     * or with status 401 (Unauthorized),
     * or with status 404 (Not Found) if the community couldn't be found,
     * or with status 403 (Forbidden) if the current user doesn't have enough rights,
     * or with status 409 (Conflict) if the current user not in blacklist community.
     */
    @PutMapping(value = "/{id}/unblock", params = "userId")
    public ResponseEntity<?> unblockUser(@PathVariable("id") Long id, @RequestParam("userId") Long userId) {

        LOGGER.debug("Unblock user with id {} from community with id {}", userId, id);
        try {
            Long principalId = securityService.getPrincipalId();
            securityService.verifyUserAuthorization(principalId);
            communityService.unblockUser(id, userId, principalId);
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
        } catch (CommunityException e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(
                    new Response(null, null, e.getMessage()),
                    HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(
                new Response(null, null, null),
                HttpStatus.OK);
    }

    /**
     * Uploads logotype for desired community.
     *
     * @param id id of the desired community.
     * @param logotype image representation.
     * @return the ResponseEntity with status 200 (OK),
     * or with status 404 (Not Found) if the community couldn't be found,
     * or with status 401 (Unauthorized),
     * or with status 403 (Forbidden) if the current user doesn't have enough rights,
     * or with status 500 (Internal Server Error),
     * or with status 400 (Bad Request).
     */
    @PostMapping("/{id}/upload")
    public ResponseEntity<Response> uploadCommunityLogotype(@PathVariable("id") Long id,
                                                            @RequestParam("logotype") MultipartFile logotype) {

        try {
            Long principalId = securityService.getPrincipalId();
            securityService.verifyUserAuthorization(principalId);
            communityService.uploadCommunityLogotype(id, principalId, logotype);
        } catch (EntityNotFoundException e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(
                    new Response(null, null, e.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (UserUnauthorizedException e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(
                    new Response(null, null, e.getMessage()),
                    HttpStatus.UNAUTHORIZED);
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
        } catch (UnsupportedFileTypeException e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(
                    new Response(null, null, e.getMessage()),
                    HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(
                new Response(null, null, null),
                HttpStatus.OK);
    }
}
