package com.bootcamp.socialnetwork.web.rest;

import com.bootcamp.socialnetwork.domain.Friendship;
import com.bootcamp.socialnetwork.domain.FriendshipResponseCode;
import com.bootcamp.socialnetwork.repository.FriendshipRepository;
import com.bootcamp.socialnetwork.repository.UserRepository;
import com.bootcamp.socialnetwork.service.FriendshipService;
import com.bootcamp.socialnetwork.service.SecurityService;
import com.bootcamp.socialnetwork.service.UserService;
import com.bootcamp.socialnetwork.service.dto.UserProfileDto;
import com.bootcamp.socialnetwork.util.Response;
import com.bootcamp.socialnetwork.web.rest.errors.EntityNotFoundException;
import com.bootcamp.socialnetwork.web.rest.errors.FriendshipException;
import com.bootcamp.socialnetwork.web.rest.errors.UserBlockedException;
import com.bootcamp.socialnetwork.web.rest.errors.UserUnauthorizedException;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/")
public class FriendshipApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FriendshipApiController.class);
    @Autowired
    UserRepository userRepository;
    @Autowired
    private FriendshipService friendshipService;
    @Autowired
    private FriendshipRepository friendshipRepository;
    @Autowired
    private SecurityService securityService;
    @Autowired
    private UserService userService;

    private List<Integer> checkPaginatedParams(Integer page, Integer size) throws InvalidParameterException {
        if (page != null && size == null) {
            throw new InvalidParameterException("Invalid parameter set. Enter size.");
        }
        if (page == null && size != null) {
            throw new InvalidParameterException("Invalid parameter set. Enter page.");
        }
        if (page == null) {
            page = 1;
        }
        if (size == null) {
            size = Integer.MAX_VALUE;
        }
        return Stream.of(page, size).collect(Collectors.toList());
    }

    /**
     * Retrieve All Friendships
     *
     * @return the ResponseEntity with status 200 (OK)
     */
    @GetMapping("/friendship")
    public ResponseEntity<Response> listAllFriendships() {
        List<Friendship> friendships = friendshipRepository.findAll();
        return new ResponseEntity<>(new Response(friendships, null, null), HttpStatus.OK);
    }

    /**
     * Get friendship status between two users
     *
     * @param userId   id of first user
     * @param friendId id of second user
     * @return the ResponseEntity with status 200 (OK),
     * or with status 404 (Not Found) if user with such id not found,
     * or with status 409 (Conflict) if userId and friendId matches.
     */
    @GetMapping("/friendship/status")
    public ResponseEntity<Response> getFriendshipStatus(@RequestParam(value = "userId") Long userId,
                                                        @RequestParam(value = "friendId") Long friendId) {

        LOGGER.debug("Return friendship status in message between users with id {} and {}", userId, friendId);
        try {
            userService.checkExist(userId);
            userService.checkExist(friendId);
            Pair friendshipStatus = friendshipService.getFriendshipStatus(userId, friendId);
            return new ResponseEntity<>(new Response(friendshipStatus, null, null),
                    HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(new Response(null, null, e.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (FriendshipException e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(new Response(null, null, e.getMessage()),
                    HttpStatus.CONFLICT);
        }
    }

    /**
     * Add a friend
     *
     * @param userId id of user
     * @return the ResponseEntity with status 200 (OK),
     * or with status 401 (Unauthorized) if user is not authenticated,
     * or with status 403 (Forbidden) if one of two users is locked,
     * or with status 404 (Not Found) if user with such id not found,
     * or with status 409 (Conflict) if principalId and userId matches.
     */
    @PostMapping("/profile/principal/friend/add/{id}")
    public ResponseEntity<?> addFriend(@PathVariable("id") Long userId) {

        LOGGER.debug("Current user add to friends user with id {} ", userId);
        try {
            Long principalId = securityService.getPrincipalId();
            securityService.verifyUserAuthorization(principalId);
            userService.checkExist(userId);
            FriendshipResponseCode friendshipResponseCode = friendshipService.addFriend(principalId, userId);
            return new ResponseEntity<>(new Response(null, friendshipResponseCode.getMessage(), null),
                    HttpStatus.OK);
        } catch (UserUnauthorizedException e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(new Response(null, null, e.getMessage()),
                    HttpStatus.UNAUTHORIZED);
        } catch (UserBlockedException e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(new Response(null, null, e.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (EntityNotFoundException e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(new Response(null, null, e.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (FriendshipException e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(new Response(null, null, e.getMessage()),
                    HttpStatus.CONFLICT);
        }
    }

    /**
     * Delete Friend
     *
     * @param userId id of user
     * @return the ResponseEntity with status 200 (OK),
     * or with status 401 (Unauthorized) if user is not authenticated,
     * or with status 403 (Forbidden) if one of two users is locked,
     * or with status 404 (Not Found) if user with such id not found,
     * or with status 409 (Conflict) if principalId and userId matches.
     */
    @PutMapping("/profile/principal/friend/delete/{id}")
    public ResponseEntity<?> deleteFriend(@PathVariable("id") Long userId) {

        LOGGER.debug("Current user delete from friends user with id {} ", userId);
        try {
            Long principalId = securityService.getPrincipalId();
            securityService.verifyUserAuthorization(principalId);
            userService.checkExist(userId);
            FriendshipResponseCode friendshipResponseCode = friendshipService.deleteFriend(principalId, userId);
            return new ResponseEntity<>(new Response(null, friendshipResponseCode.getMessage(), null),
                    HttpStatus.OK);
        } catch (UserUnauthorizedException e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(new Response(null, null, e.getMessage()),
                    HttpStatus.UNAUTHORIZED);
        } catch (UserBlockedException e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(new Response(null, null, e.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (EntityNotFoundException e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(new Response(null, null, e.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (FriendshipException e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(new Response(null, null, e.getMessage()),
                    HttpStatus.CONFLICT);
        }
    }

    /**
     * Current user approve all incoming requests
     *
     * @return the ResponseEntity with status 200 (OK),
     * or with status 401 (Unauthorized) if user is not authenticated.
     */
    @PutMapping("/profile/principal/request/incoming/approve")
    public ResponseEntity<?> approveAllIncomingRequest() {
        LOGGER.debug("Current user approve all incoming requests");
        try {
            Long principalId = securityService.getPrincipalId();
            securityService.verifyUserAuthorization(principalId);
            friendshipService.approveAllIncomingRequests(principalId);
            return new ResponseEntity<>(new Response(null, null, null), HttpStatus.OK);
        } catch (UserUnauthorizedException e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(new Response(null, null, e.getMessage()),
                    HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Current user decline all incoming requests
     *
     * @return the ResponseEntity with status 200 (OK),
     * or with status 401 (Unauthorized) if user is not authenticated.
     */
    @PutMapping("/profile/principal/request/incoming/decline")
    public ResponseEntity<?> declineAllIncomingRequest() {
        LOGGER.debug("Current user decline all incoming requests");
        try {
            Long principalId = securityService.getPrincipalId();
            securityService.verifyUserAuthorization(principalId);
            friendshipService.declineAllIncomingRequests(principalId);
            return new ResponseEntity<>(new Response(null, null, null), HttpStatus.OK);
        } catch (UserUnauthorizedException e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(new Response(null, null, e.getMessage()),
                    HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Current user decline all outgoing requests
     *
     * @return the ResponseEntity with status 200 (OK),
     * or with status 401 (Unauthorized) if user is not authenticated.
     */
    @PutMapping("/profile/principal/request/outgoing/decline")
    public ResponseEntity<?> declineAllOutgoingRequest() {
        LOGGER.debug("Current user decline all incoming requests");
        try {
            Long principalId = securityService.getPrincipalId();
            securityService.verifyUserAuthorization(principalId);
            friendshipService.declineAllOutgoingRequests(principalId);
            return new ResponseEntity<>(new Response(null, null, null), HttpStatus.OK);
        } catch (UserUnauthorizedException e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(new Response(null, null, e.getMessage()),
                    HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Add user to the blacklist
     *
     * @param userId id of user
     * @param time   unlock time
     * @return the ResponseEntity with status 200 (OK),
     * or with status 401 (Unauthorized) if user is not authenticated,
     * or with status 404 (Not Found) if user with such id not found,
     * or with status 409 (Conflict) if principalId and userId matches.
     */
    @PostMapping("/profile/{id}/block")
    public ResponseEntity<?> blockUser(@PathVariable("id") Long userId,
                                       @RequestParam(value = "time", required = false) Long time) {

        LOGGER.debug("Current user block user with id {} ", userId);
        try {
            Long principalId = securityService.getPrincipalId();
            securityService.verifyUserAuthorization(principalId);
            userService.checkExist(userId);
            friendshipService.blockUser(principalId, userId, time);
            return new ResponseEntity<>(new Response(null, null, null),
                    HttpStatus.OK);
        } catch (UserUnauthorizedException e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(new Response(null, null, e.getMessage()),
                    HttpStatus.UNAUTHORIZED);
        } catch (EntityNotFoundException e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(new Response(null, null, e.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (FriendshipException e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(new Response(null, null, e.getMessage()),
                    HttpStatus.CONFLICT);
        }
    }

    /**
     * Delete user from blacklist
     *
     * @param userId id of user
     * @return the ResponseEntity with status 200 (OK),
     * or with status 401 (Unauthorized) if user is not authenticated,
     * or with status 404 (Not Found) if user with such id not found,
     * or with status 409 (Conflict) if principalId and userId matches.
     */
    @PutMapping("/profile/{id}/unblock")
    public ResponseEntity<?> unblockUser(@PathVariable("id") Long userId) {
        LOGGER.debug("Current user remove from blacklist user with id {}", userId);
        try {
            Long principalId = securityService.getPrincipalId();
            securityService.verifyUserAuthorization(principalId);
            userService.checkExist(userId);
            friendshipService.unBlockUser(principalId, userId);
            return new ResponseEntity<>(new Response(null, null, null), HttpStatus.OK);
        } catch (UserUnauthorizedException e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(new Response(null, null, e.getMessage()),
                    HttpStatus.UNAUTHORIZED);
        } catch (EntityNotFoundException e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(new Response(null, null, e.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (FriendshipException e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(new Response(null, null, e.getMessage()),
                    HttpStatus.CONFLICT);
        }
    }

    /**
     * Get users with time unblock from blacklist of current user
     *
     * @return the ResponseEntity with status 200 (OK),
     * or with status 204 (No Content) if blacklist is empty,
     * or with status 401 (Unauthorized) if user is not authenticated.
     */
    @GetMapping("/profile/principal/blacklist")
    public ResponseEntity<?> getBlacklist() {

        LOGGER.debug("Get blacklist of current user");
        List<Pair<UserProfileDto, String>> blockedUsers;
        try {
            Long principalId = securityService.getPrincipalId();
            securityService.verifyUserAuthorization(principalId);
            blockedUsers =
                    friendshipService.getBlockedUsers(principalId);
        } catch (UserUnauthorizedException e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(new Response(null, null, e.getMessage()),
                    HttpStatus.UNAUTHORIZED);
        }
        if (blockedUsers.isEmpty()) {
            return new ResponseEntity<>(new Response(null, null, null),
                    HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(new Response(blockedUsers, null, null),
                HttpStatus.OK);
    }

    /**
     * Get Paginated Followers of user
     *
     * @param userId id of user
     * @param page   number of the desired page.
     * @param size   amount of desired objects on page.
     * @return the ResponseEntity with status 200 (OK),
     * or with status 204 (No Content) if followers not found,
     * or with status 404 (Not Found) if user with such id not found.
     */
    @GetMapping("/profile/{id}/request/incoming")
    public ResponseEntity<?> getFollowers(@PathVariable("id") Long userId,
                                          @RequestParam(value = "page", required = false) Integer page,
                                          @RequestParam(value = "size", required = false) Integer size) {

        LOGGER.debug("Get followers of user with id {}", userId);
        List<Integer> params = checkPaginatedParams(page, size);
        Page<UserProfileDto> followers;
        try {
            userService.checkExist(userId);
            followers = friendshipService.getFollowers(userId, params.get(0), params.get(1));
        } catch (EntityNotFoundException e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(new Response(null, null, e.getMessage()),
                    HttpStatus.NOT_FOUND);
        }
        if (followers.getContent().isEmpty()) {
            return new ResponseEntity<>(new Response(null, null, null),
                    HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(new Response(followers, null, null),
                HttpStatus.OK);
    }

    /**
     * Get Paginated Friends
     *
     * @param userId if of user
     * @param page   number of the desired page.
     * @param size   amount of desired objects on page.
     * @return the ResponseEntity with status 200 (OK),
     * or with status 204 (No Content) if friends not found,
     * or with status 404 (Not Found) if user with such id not found.
     */
    @GetMapping("/profile/{id}/friend")
    public ResponseEntity<?> getFriends(@PathVariable("id") Long userId,
                                        @RequestParam(value = "page", required = false) Integer page,
                                        @RequestParam(value = "size", required = false) Integer size) {

        LOGGER.debug("Get friends of user with id {}", userId);
        List<Integer> params = checkPaginatedParams(page, size);
        Page<UserProfileDto> friends;
        try {
            userService.checkExist(userId);
            friends = friendshipService.getFriends(userId, params.get(0), params.get(1));
        } catch (EntityNotFoundException e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(new Response(null, null, e.getMessage()),
                    HttpStatus.NOT_FOUND);
        }
        if (friends.getContent().isEmpty()) {
            return new ResponseEntity<>(new Response(null, null, null),
                    HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(new Response(friends, null, null),
                HttpStatus.OK);
    }

    /**
     * Get Paginated Outgoing Requests of current user
     *
     * @param page number of the desired page.
     * @param size amount of desired objects on page.
     * @return the ResponseEntity with status 200 (OK),
     * or with status 204 (No Content) if outgoing requests not found,
     * or with status 401 (Unauthorized) if user is not authenticated.
     */
    @GetMapping("/profile/principal/request/outgoing")
    public ResponseEntity<?> getOutgoingRequests(@RequestParam(value = "page", required = false) Integer page,
                                                 @RequestParam(value = "size", required = false) Integer size) {

        LOGGER.debug("Get outgoing requests of current user");
        List<Integer> params = checkPaginatedParams(page, size);
        Page<UserProfileDto> outgoingRequests;
        try {
            Long principalId = securityService.getPrincipalId();
            securityService.verifyUserAuthorization(principalId);
            outgoingRequests = friendshipService.getOutgoingRequests(principalId,
                    params.get(0), params.get(1));
        } catch (UserUnauthorizedException e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(new Response(null, null, e.getMessage()),
                    HttpStatus.UNAUTHORIZED);
        }
        if (outgoingRequests.getContent().isEmpty()) {
            return new ResponseEntity<>(new Response(null, null, null),
                    HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(new Response(outgoingRequests, null, null),
                HttpStatus.OK);
    }

    /**
     * Get Paginated Mutual Friends
     *
     * @param userId   id of first user
     * @param friendId id of second user
     * @param page     number of the desired page.
     * @param size     amount of desired objects on page.
     * @return the ResponseEntity with status 200 (OK),
     * or with status 204 (No Content) if mutual friends not found,
     * or with status 404 (Not Found) if user with such id not found,
     * or with status 409 (Conflict) if userId and friendId matches.
     */
    @GetMapping("/friendship/mutualFriends")
    public ResponseEntity<?> getMutualFriends(@RequestParam(value = "userId") Long userId,
                                              @RequestParam(value = "friendId") Long friendId,
                                              @RequestParam(value = "page", required = false) Integer page,
                                              @RequestParam(value = "size", required = false) Integer size) {

        LOGGER.debug("Return mutual friends of users with id {} and {}", userId, friendId);
        List<Integer> params = checkPaginatedParams(page, size);
        Page<UserProfileDto> mutualFriends;
        try {
            userService.checkExist(userId);
            userService.checkExist(friendId);
            mutualFriends = friendshipService.getMutualFriends(userId, friendId, params.get(0), params.get(1));
        } catch (EntityNotFoundException e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(new Response(null, null, e.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (FriendshipException e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(new Response(null, null, e.getMessage()),
                    HttpStatus.CONFLICT);
        }
        if (mutualFriends.getContent().isEmpty()) {
            return new ResponseEntity<>(new Response(null, null, null),
                    HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(new Response(mutualFriends, null, null),
                HttpStatus.OK);
    }
}