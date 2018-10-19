package com.bootcamp.socialnetwork.domain;

import javax.persistence.*;

/**
 * Entity of Friendship
 */
@Entity
@Table(name = "friendship")
public class Friendship extends AbstractEntity {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "friend_id")
    private Long friendId;

    @Enumerated(EnumType.STRING)
    private FriendshipStatus status;

    private boolean block12;

    @Column(name = "time_unblock_12")
    private Long timeUnblock12;

    private boolean block21;

    @Column(name = "time_unblock_21")
    private Long timeUnblock21;

    public Friendship() {
    }

    public Friendship(Long userId, Long friendId, FriendshipStatus status,
                      boolean block12, Long timeUnblock12, boolean block21, Long timeUnblock21) {
        this.userId = userId;
        this.friendId = friendId;
        this.status = status;
        this.block12 = block12;
        this.timeUnblock12 = timeUnblock12;
        this.block21 = block21;
        this.timeUnblock21 = timeUnblock21;
    }

    public Friendship(Long userId, Long friendId, FriendshipStatus status) {
        this.userId = userId;
        this.friendId = friendId;
        this.status = status;
        block12 = false;
        timeUnblock12 = null;
        block21 = false;
        timeUnblock21 = null;
    }

    public Friendship(Long userId, Long friendId, FriendshipStatus status, boolean block12, Long timeUnblock12) {
        this.userId = userId;
        this.friendId = friendId;
        this.status = status;
        this.block12 = block12;
        this.timeUnblock12 = timeUnblock12;
        block21 = false;
        timeUnblock21 = null;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getFriendId() {
        return friendId;
    }

    public void setFriendId(Long friendId) {
        this.friendId = friendId;
    }

    public FriendshipStatus getStatus() {
        return status;
    }

    public void setStatus(FriendshipStatus status) {
        this.status = status;
    }

    public boolean isBlock12() {
        return block12;
    }

    public void setBlock12(boolean block12) {
        this.block12 = block12;
    }

    public Long getTimeUnblock12() {
        return timeUnblock12;
    }

    public void setTimeUnblock12(Long timeUnblock12) {
        this.timeUnblock12 = timeUnblock12;
    }

    public boolean isBlock21() {
        return block21;
    }

    public void setBlock21(boolean block21) {
        this.block21 = block21;
    }

    public Long getTimeUnblock21() {
        return timeUnblock21;
    }

    public void setTimeUnblock21(Long timeUnblock21) {
        this.timeUnblock21 = timeUnblock21;
    }
}
