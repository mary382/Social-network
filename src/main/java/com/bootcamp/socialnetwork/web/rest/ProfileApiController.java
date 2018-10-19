package com.bootcamp.socialnetwork.web.rest;

import com.bootcamp.socialnetwork.service.CommunityService;
import com.bootcamp.socialnetwork.service.PostService;
import com.bootcamp.socialnetwork.service.SecurityService;
import com.bootcamp.socialnetwork.service.UserService;
import com.bootcamp.socialnetwork.service.dto.CommunityDto;
import com.bootcamp.socialnetwork.service.dto.PostDto;
import com.bootcamp.socialnetwork.service.dto.UserProfileDto;
import com.bootcamp.socialnetwork.util.Response;
import com.bootcamp.socialnetwork.web.rest.errors.*;
import com.dropbox.core.DbxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

/**
 * REST controller for managing profiles.
 */
@RestController
@RequestMapping("/api")
public class ProfileApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileApiController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private CommunityService communityService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private PostService postService;


    /**
     * Get profile of a specific user by ID.
     *
     * @param id the ID of the profile to find.
     * @return the ResponseEntity with status 200 (OK) and with body containing the profile
     * found by ID, or with status 404 (Not Found).
     */
    @GetMapping("/profile/{id}")
    public ResponseEntity<?> getProfile(@PathVariable("id") Long id) {

        LOGGER.debug("Fetching profile with ID {}.", id);

        try {
            UserProfileDto userProfileDto = userService.findUserProfile(id);
            return new ResponseEntity<>(userProfileDto, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            LOGGER.error("Profile not found. {}", e.getMessage());
            return new ResponseEntity<>(new CustomError("Profile not found."), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Get profile of the principal.
     *
     * @return the ResponseEntity with status 200 (OK) and with body containing the profile
     * of the principal, or with status 204 (Not Content).
     */
    @GetMapping("/profile/principal/")
    public ResponseEntity<?> getPrincipal() {

        LOGGER.debug("Fetching profile of the principal.");

        try {
            UserProfileDto userProfileDto = securityService.getPrincipalProfile();
            return new ResponseEntity<>(userProfileDto, HttpStatus.OK);
        } catch (UserUnauthorizedException e) {
            LOGGER.error("Unable to fetch profile of the principal. {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    /**
     * Get profiles of all users.
     *
     * @return the ResponseEntity with status 200 (OK) and with body containing profiles of
     * all users, or with status 204 (No Content).
     */
    @GetMapping("/profile/")
    public ResponseEntity<List<UserProfileDto>> getAllProfiles() {

        LOGGER.debug("Fetching all profiles.");

        List<UserProfileDto> users = userService.findAllProfiles();
        if (users.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    /**
     * Update an existing profile.
     *
     * @param id             the ID of the profile to update.
     * @param userProfileDto the data to be assigned to the profile.
     * @return the ResponseEntity with status 200 (OK) and with body containing the updated
     * profile, or with status 404 (Not Found) if the profile couldn't be found, or with
     * status 403 (Forbidden) if the client has no rights to update the profile.
     */
    @PutMapping("/profile/{id}")
    public ResponseEntity<?> updateProfile(@PathVariable("id") Long id,
                                           @Valid @RequestBody UserProfileDto userProfileDto) {

        LOGGER.debug("Updating user with ID {}.", id);

        try {
            userProfileDto = userService.update(id, userProfileDto);
            return new ResponseEntity<>(userProfileDto, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            LOGGER.error("Unable to update. {}", e.getMessage());
            return new ResponseEntity<>(new CustomError(e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (UserUnauthorizedException | PrivilegeException e) {
            LOGGER.error("Unable to update. {}", e.getMessage());
            return new ResponseEntity<>(new CustomError(e.getMessage()), HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Gets all communities paginated for desired user.
     *
     * @param id   id of the desired user.
     * @param page number of the desired page.
     * @param size amount of desired objects on page.
     * @return the ResponseEntity with status 200 (OK),
     * or with status 401 (Unauthorized),
     * or with status 404 (Not Found) if the user couldn't be found,
     * or with status 204 (No Content) if communities not found.
     */
    @GetMapping("/profile/{id}/community")
    public ResponseEntity<Response> getPaginatedCommunities(@PathVariable("id") Long id,
                                                            @RequestParam(value = "page", required = false) Integer page,
                                                            @RequestParam(value = "size", required = false) Integer size) {

        LOGGER.debug("Fetching communities for user with id {}.", id);
        if ((page != null && size == null) || (page == null && size != null)) {
            return new ResponseEntity<>(
                    new Response(null, null, "Invalid parameter set"),
                    HttpStatus.BAD_REQUEST);
        }

        Page<CommunityDto> communityDtoPage;
        try {
            Long principalId = securityService.getPrincipalId();
            securityService.verifyUserAuthorization(principalId);
            communityDtoPage = communityService.getUserPaginatedCommunities(id, page, size);
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
        }

        if (communityDtoPage.getContent().isEmpty()) {
            LOGGER.error("Communities for user with id {} not found.", id);
            return new ResponseEntity<>(
                    new Response(null, null, "Communities not found"),
                    HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(
                new Response(communityDtoPage, null, null),
                HttpStatus.OK);
    }

    /**
     * Uploads avatar for desired user.
     *
     * @param id     id of the desired user.
     * @param avatar image representation.
     * @return the ResponseEntity with status 200 (OK),
     * or with status 401 (Unauthorized),
     * or with status 404 (Not Found) if the user couldn't be found,
     * or with status 500 (Internal Server Error),
     * or with status 400 (Bad Request),
     * or with status 403 (Forbidden) if the current user doesn't have enough rights,.
     */
    @PostMapping("/profile/{id}/avatar/upload")
    public ResponseEntity<Response> uploadUserAvatar(@PathVariable("id") Long id,
                                                     @RequestParam("avatar") MultipartFile avatar) {
        LOGGER.debug("User with id {} upload avatar", id);

        try {
            Long principalId = securityService.getPrincipalId();
            securityService.verifyUserAuthorization(principalId);
            userService.uploadAvatar(id, principalId, avatar);
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
        } catch (DbxException | IOException e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(
                    new Response(null, null, e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (UnsupportedFileTypeException e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(
                    new Response(null, null, e.getMessage()),
                    HttpStatus.BAD_REQUEST);
        } catch (PrivilegeException e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(
                    new Response(null, null, e.getMessage()),
                    HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<>(
                new Response(null, null, null),
                HttpStatus.OK);
    }

    /**
     * Delete avatar for desired user.
     *
     * @param id id of the desired user.
     * @return the ResponseEntity with status 200 (OK),
     * or with status 401 (Unauthorized),
     * or with status 404 (Not Found) if the user couldn't be found,
     * or with status 500 (Internal Server Error),
     * or with status 403 (Forbidden) if the current user doesn't have enough rights,.
     */
    @DeleteMapping("/profile/{id}/avatar/delete")
    public ResponseEntity<Response> deleteUserAvatar(@PathVariable("id") Long id) {
        LOGGER.debug("User with id {} delete avatar", id);

        try {
            Long principalId = securityService.getPrincipalId();
            securityService.verifyUserAuthorization(principalId);
            userService.deleteAvatar(id, principalId);
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

    @GetMapping("/profile/{id}/news")
    public ResponseEntity<Response> getPaginatedNews(@PathVariable("id") Long id,
                                                     @RequestParam(value = "page", required = false) Integer page,
                                                     @RequestParam(value = "size", required = false) Integer size) {

        if ((page != null && size == null) || (page == null && size != null)) {
            return new ResponseEntity<>(
                    new Response(null, null, "Invalid parameter set"),
                    HttpStatus.BAD_REQUEST);
        }

        Page<PostDto> postDtoPage;
        try {
            Long principalId = securityService.getPrincipalId();
            securityService.verifyUserAuthorization(principalId);
            postDtoPage = userService.getUserNews(id, principalId, page, size);
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
        }

        if (postDtoPage == null) return new ResponseEntity<>(
                new Response(null, null, "News not found"),
                HttpStatus.NO_CONTENT);

        return new ResponseEntity<>(
                new Response(postDtoPage, null, null),
                HttpStatus.OK);
    }

    @GetMapping("/profile/{id}/post")
    public ResponseEntity<Response> getPaginatedPosts(@PathVariable("id") Long id,
                                                      @RequestParam(value = "page", required = false) Integer page,
                                                      @RequestParam(value = "size", required = false) Integer size) {

        LOGGER.debug("Fetching posts.");
        if ((page != null && size == null) || (page == null && size != null)) {
            return new ResponseEntity<>(
                    new Response(null, null, "Invalid parameter set"),
                    HttpStatus.BAD_REQUEST);
        }

        Page<PostDto> postDtoPage = postService.findPaginatedByOwner(id, page, size);
        if (postDtoPage == null) return new ResponseEntity<>(
                new Response(null, null, "Posts not found"),
                HttpStatus.NO_CONTENT);

        return new ResponseEntity<>(
                new Response(postDtoPage, null, null),
                HttpStatus.OK);
    }
}
