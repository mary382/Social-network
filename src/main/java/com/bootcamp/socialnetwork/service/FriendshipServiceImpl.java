package com.bootcamp.socialnetwork.service;

import com.bootcamp.socialnetwork.domain.Friendship;
import com.bootcamp.socialnetwork.domain.FriendshipResponseCode;
import com.bootcamp.socialnetwork.domain.FriendshipStatus;
import com.bootcamp.socialnetwork.repository.FriendshipRepository;
import com.bootcamp.socialnetwork.service.dto.UserProfileDto;
import com.bootcamp.socialnetwork.web.rest.errors.FriendshipException;
import com.bootcamp.socialnetwork.web.rest.errors.UserBlockedException;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.bootcamp.socialnetwork.domain.FriendshipStatus.*;

@Service("friendshipService")
@Transactional
public class FriendshipServiceImpl implements FriendshipService {

    @Autowired
    FriendshipRepository friendshipRepository;
    @Autowired
    UserService userService;
    @Autowired
    MessageService messageService;

    private void checkIdentity(Long userId, Long friendId) throws FriendshipException {
        if (userId.equals(friendId)) {
            throw new FriendshipException("User id and friend id matches.");
        }
    }

    private void checkBlock(Friendship friendship) throws UserBlockedException {
        if (friendship.isBlock12()) {
            throw new UserBlockedException("User is blocked.");
        }
        if (friendship.isBlock21()) {
            throw new UserBlockedException("You are blocked by the user.");
        }
    }

    /**
     * Get paginated UserProfileDto by status
     *
     * @param userId id of the user
     * @param status Friendship status
     * @param page   number of the desired page.
     * @param size   amount of desired objects on page.
     * @return page of users given FriendshipStatus
     */
    private Page<UserProfileDto> getUsersByStatus(Long userId, FriendshipStatus status, Integer page, Integer size) {
        // page - 1, because indexing from 0
        Pageable pageRequest = new PageRequest(page - 1, size);
        Page<Long> friendshipPage =
                friendshipRepository.getUsersIdGivenFriendshipStatus(userId, status, pageRequest);
        List<UserProfileDto> users = new ArrayList<>();
        for (Long id : friendshipPage) {
            users.add(userService.findProfile(id));
        }
        return new PageImpl<>(users, pageRequest, friendshipPage.getTotalElements());
    }

    @Override
    public boolean inBlackList(Long userId, Long friendId) {
        Friendship friendship = friendshipRepository.findByUserIdAndFriendId(userId, friendId);
        return (friendship != null) && friendship.isBlock12();
    }

    @Override
    public FriendshipResponseCode addFriend(Long principalId, Long userId) throws UserBlockedException, FriendshipException {
        checkIdentity(principalId, userId);
        Friendship friendship1 = friendshipRepository.findByUserIdAndFriendId(principalId, userId);
        if (friendship1 == null) {
            friendshipRepository.save(new Friendship(principalId, userId, SEND_REQUEST));
            friendshipRepository.save(new Friendship(userId, principalId, INCOMING_REQUEST));
            return FriendshipResponseCode.REQUEST_SENT;
        }
        checkBlock(friendship1);
        Friendship friendship2 = friendshipRepository.findByUserIdAndFriendId(userId, principalId);
        switch (friendship1.getStatus()) {
            case FRIEND:
                throw new FriendshipException("You are already friends.");
            case INCOMING_REQUEST:
                friendship1.setStatus(FRIEND);
                friendship2.setStatus(FRIEND);
                return FriendshipResponseCode.REQUEST_APPROVED;
            case NONE:
                friendship1.setStatus(SEND_REQUEST);
                friendship2.setStatus(INCOMING_REQUEST);
                return FriendshipResponseCode.REQUEST_SENT;
            case SEND_REQUEST:
                throw new FriendshipException("Friend request has already been sent.");
        }
        return null;
    }

    @Override
    public FriendshipResponseCode deleteFriend(Long principalId, Long userId)
            throws UserBlockedException, FriendshipException {
        checkIdentity(principalId, userId);
        Friendship friendship1 = friendshipRepository.findByUserIdAndFriendId(principalId, userId);
        if (friendship1 == null) {
            throw new FriendshipException("User is not a friend.");
        }
        checkBlock(friendship1);
        Friendship friendship2 = friendshipRepository.findByUserIdAndFriendId(userId, principalId);
        switch (friendship1.getStatus()) {
            case FRIEND:
                friendship1.setStatus(INCOMING_REQUEST);
                friendship2.setStatus(SEND_REQUEST);
                return FriendshipResponseCode.FRIEND_REMOVED;
            case INCOMING_REQUEST:
                friendship1.setStatus(NONE);
                friendship2.setStatus(NONE);
                return FriendshipResponseCode.REQUEST_DECLINED;
            case NONE:
                throw new FriendshipException("User is not a friend.");
            case SEND_REQUEST:
                friendship1.setStatus(NONE);
                friendship2.setStatus(NONE);
                return FriendshipResponseCode.REQUEST_SUGGESTION_DELETED;
        }
        return null;
    }

    @Override
    public Pair<Integer, String> getFriendshipStatus(Long userId, Long friendId) throws FriendshipException {
        checkIdentity(userId, friendId);
        Friendship friendship = friendshipRepository.findByUserIdAndFriendId(userId, friendId);
        if (friendship == null) {
            return new Pair<>(0, NONE.getMessage());
        }
        Long timeUnblock;
        SimpleDateFormat sdf = new SimpleDateFormat("dd MM yyyy HH:mm");
        if (friendship.isBlock12() && friendship.isBlock21()) {
            String message;
            timeUnblock = friendship.getTimeUnblock12();
            if (timeUnblock != null) {
                Date time = new Date(timeUnblock);
                message = "User is blocked until " + sdf.format(time);
            } else {
                message = "User is blocked";
            }
            timeUnblock = friendship.getTimeUnblock21();
            if (timeUnblock != null) {
                Date time = new Date(timeUnblock);
                message += ".\nYou are blocked until " + sdf.format(time);
            } else {
                message += ".\nYou are blocked";
            }
            return new Pair<>(6, message);
        }
        if (friendship.isBlock12()) {
            timeUnblock = friendship.getTimeUnblock12();
            if (timeUnblock != null) {
                Date time = new Date(timeUnblock);
                return new Pair<>(4, "User is blocked until " + sdf.format(time));
            } else {
                return new Pair<>(4, "User is blocked");
            }
        }
        if (friendship.isBlock21()) {
            timeUnblock = friendship.getTimeUnblock21();
            if (timeUnblock != null) {
                Date time = new Date(timeUnblock);
                return new Pair<>(5, "You are blocked until " + sdf.format(time));
            } else {
                return new Pair<>(5, "You are blocked");
            }
        }
        switch (friendship.getStatus()) {
            case FRIEND:
                return new Pair<>(1, FRIEND.getMessage());
            case INCOMING_REQUEST:
                return new Pair<>(2, INCOMING_REQUEST.getMessage());
            case NONE:
                return new Pair<>(0, NONE.getMessage());
            case SEND_REQUEST:
                return new Pair<>(3, SEND_REQUEST.getMessage());
        }
        return new Pair<>(0, NONE.getMessage());
    }

    @Override
    public List<Pair<UserProfileDto, String>> getBlockedUsers(Long principalId) {
        List<Friendship> blockedUsers = friendshipRepository.getBlockedUsersId(principalId);
        List<Pair<UserProfileDto, String>> blacklist = new ArrayList<>();
        Long timeUnblock;
        SimpleDateFormat sdf = new SimpleDateFormat("dd MM yyyy HH:mm");
        String block;
        for (Friendship friendship : blockedUsers) {
            timeUnblock = friendship.getTimeUnblock12();
            if (timeUnblock != null) {
                Date time = new Date(timeUnblock);
                block = "until " + sdf.format(time);
            } else {
                block = "permanently";
            }
            blacklist.add(new Pair<>(userService.findProfile(friendship.getFriendId()), block));
        }
        return blacklist;
    }

    @Override
    public Page<UserProfileDto> getFollowers(Long userId, Integer page, Integer size) {
        return getUsersByStatus(userId, INCOMING_REQUEST, page, size);
    }

    @Override
    public Page<UserProfileDto> getFriends(Long userId, Integer page, Integer size) {
        return getUsersByStatus(userId, FRIEND, page, size);
    }

    @Override
    public Page<UserProfileDto> getMutualFriends(Long userId, Long friendId, Integer page, Integer size)
            throws FriendshipException {
        checkIdentity(userId, friendId);
        List<Long> friendsId1 = friendshipRepository.getUsersIdGivenFriendshipStatus(userId, FRIEND);
        List<Long> friendsId2 = friendshipRepository.getUsersIdGivenFriendshipStatus(friendId, FRIEND);
        friendsId1.retainAll(friendsId2);
        List<UserProfileDto> mutualFriends = new ArrayList<>();
        for (Long id : friendsId1) {
            mutualFriends.add(userService.findProfile(id));
        }
        Pageable pageRequest = new PageRequest(page - 1, size);
        int min = (size * (page - 1) > mutualFriends.size()) ? mutualFriends.size() : size * (page - 1);
        int max = (size * (page) > mutualFriends.size()) ? mutualFriends.size() : size * (page);
        return new PageImpl<>(mutualFriends.subList(min, max), pageRequest, mutualFriends.size());
    }

    @Override
    public Page<UserProfileDto> getOutgoingRequests(Long principalId, Integer page, Integer size) {
        return getUsersByStatus(principalId, SEND_REQUEST, page, size);
    }

    @Override
    public void approveAllIncomingRequests(Long principalId) {
        List<Friendship> friendships = friendshipRepository.findAllByUserIdAndStatusAndBlock12AndBlock21
                (principalId, INCOMING_REQUEST, false, false);
        for (Friendship friendship : friendships) {
            Friendship friendship1 = friendshipRepository.findByUserIdAndFriendId
                    (friendship.getFriendId(), friendship.getUserId());
            friendship.setStatus(FRIEND);
            friendship1.setStatus(FRIEND);
        }
    }

    @Override
    public void blockUser(Long principalId, Long userId, Long time) throws FriendshipException {
        checkIdentity(principalId, userId);
        if (time != null && time < System.currentTimeMillis()) {
            throw new FriendshipException("Invalid time.");
        }
        Friendship friendship1 = friendshipRepository.findByUserIdAndFriendId(principalId, userId);
        Friendship friendship2 = friendshipRepository.findByUserIdAndFriendId(userId, principalId);
        if (friendship1 != null) {
            friendship1.setBlock12(true);
            friendship1.setTimeUnblock12(time);
            friendship2.setBlock21(true);
            friendship2.setTimeUnblock21(time);
        } else {
            friendshipRepository.save(new Friendship(principalId, userId, NONE, true, time));
            friendshipRepository.save(new Friendship(userId, principalId, NONE));
        }
    }

    @Override
    public void checkBlock(Long userId, Long friendId) throws UserBlockedException, FriendshipException {
        checkIdentity(userId, friendId);
        Friendship friendship = friendshipRepository.findByUserIdAndFriendId(userId, friendId);
        if (friendship.isBlock12()) {
            throw new UserBlockedException
                    (String.format("User with id %d is blocked by user with id %d", friendId, userId));
        }
        if (friendship.isBlock21()) {
            throw new UserBlockedException
                    (String.format("User with id %d is blocked by user with id %d", userId, friendId));
        }
    }

    @Override
    public void declineAllIncomingRequests(Long principalId) {
        List<Friendship> friendships = friendshipRepository.findAllByUserIdAndStatusAndBlock12AndBlock21
                (principalId, INCOMING_REQUEST, false, false);
        for (Friendship friendship : friendships) {
            Friendship friendship1 = friendshipRepository.findByUserIdAndFriendId
                    (friendship.getFriendId(), friendship.getUserId());
            friendship.setStatus(NONE);
            friendship1.setStatus(NONE);
        }
    }

    @Override
    public void declineAllOutgoingRequests(Long principalId) {
        List<Friendship> friendships = friendshipRepository.findAllByUserIdAndStatusAndBlock12AndBlock21
                (principalId, SEND_REQUEST, false, false);
        for (Friendship friendship : friendships) {
            Friendship friendship1 = friendshipRepository.findByUserIdAndFriendId
                    (friendship.getFriendId(), friendship.getUserId());
            friendship.setStatus(NONE);
            friendship1.setStatus(NONE);
        }
    }

    @Override
    public void unBlockUser(Long principalId, Long userId) throws FriendshipException {
        checkIdentity(principalId, userId);
        Friendship friendship1 = friendshipRepository.findByUserIdAndFriendId(principalId, userId);
        if (friendship1 == null || !friendship1.isBlock12()) {
            throw new FriendshipException("User is not blocked.");
        }
        Friendship friendship2 = friendshipRepository.findByUserIdAndFriendId(userId, principalId);
        friendship1.setBlock12(false);
        friendship1.setTimeUnblock12(null);
        friendship2.setBlock21(false);
        friendship2.setTimeUnblock21(null);
        messageService.unblock(userId, principalId);
    }
}