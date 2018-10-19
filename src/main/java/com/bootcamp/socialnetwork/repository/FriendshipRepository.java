package com.bootcamp.socialnetwork.repository;

import com.bootcamp.socialnetwork.domain.AbstractRepository;
import com.bootcamp.socialnetwork.domain.Friendship;
import com.bootcamp.socialnetwork.domain.FriendshipStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendshipRepository extends AbstractRepository<Friendship> {

    Friendship findByUserIdAndFriendId(Long userId, Long friendId);

    @Query("SELECT f.friendId FROM Friendship f " +
            "WHERE f.userId = :id AND f.status = :status AND f.block12 = false AND f.block21 = false")
    List<Long> getUsersIdGivenFriendshipStatus(@Param("id") Long userId, @Param("status") FriendshipStatus status);

    @Query("SELECT f.friendId FROM Friendship f " +
            "WHERE f.userId = :id AND f.status = :status AND f.block12 = false AND f.block21 = false")
    Page<Long> getUsersIdGivenFriendshipStatus(@Param("id") Long userId, @Param("status") FriendshipStatus status, Pageable pageable);

    List<Friendship> findAllByUserIdAndStatusAndBlock12AndBlock21(Long userId, FriendshipStatus status, boolean block12, boolean block21);

    @Query("SELECT f FROM Friendship f WHERE f.userId = :id AND f.block12 = true")
    List<Friendship> getBlockedUsersId(@Param("id") Long userId);

    List<Friendship> findAllByTimeUnblock12LessThanEqual(Long time);
}
