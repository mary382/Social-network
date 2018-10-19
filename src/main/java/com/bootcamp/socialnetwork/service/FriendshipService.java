package com.bootcamp.socialnetwork.service;

import com.bootcamp.socialnetwork.domain.FriendshipResponseCode;
import com.bootcamp.socialnetwork.service.dto.UserProfileDto;
import com.bootcamp.socialnetwork.web.rest.errors.FriendshipException;
import com.bootcamp.socialnetwork.web.rest.errors.UserBlockedException;
import javafx.util.Pair;
import org.springframework.data.domain.Page;

import java.util.List;

public interface FriendshipService {

    boolean inBlackList(Long userId, Long friendId);

    FriendshipResponseCode addFriend(Long principalId, Long userId) throws UserBlockedException, FriendshipException;

    FriendshipResponseCode deleteFriend(Long principalId, Long userId) throws UserBlockedException, FriendshipException;

    /**
     * @param userId   id of first user
     * @param friendId id of second user
     * @return 0 - the user is not a friend
     * 1 - the user is a friend
     * 2 - there is an incoming friend request from the user
     * 3 - a friend request was sent to the user
     * 4 - User is blocked
     * 5 - You are blocked by the user.
     * 6 - User is blocked and you are blocked by the user.
     * @throws FriendshipException if userId and friendId matches
     */
    Pair<Integer, String> getFriendshipStatus(Long userId, Long friendId) throws FriendshipException;

    List<Pair<UserProfileDto, String>> getBlockedUsers(Long principalId);

    Page<UserProfileDto> getFollowers(Long userId, Integer page, Integer size);

    Page<UserProfileDto> getFriends(Long userId, Integer page, Integer size);

    Page<UserProfileDto> getMutualFriends(Long userId, Long friendId, Integer page, Integer size)
            throws FriendshipException;

    Page<UserProfileDto> getOutgoingRequests(Long principalId, Integer page, Integer size);

    void approveAllIncomingRequests(Long principalId);

    void blockUser(Long principalId, Long userId, Long time) throws FriendshipException;

    void checkBlock(Long userId, Long friendId) throws UserBlockedException, FriendshipException;

    void declineAllIncomingRequests(Long principalId);

    void declineAllOutgoingRequests(Long principalId);

    void unBlockUser(Long principalId, Long userId) throws FriendshipException;
}
