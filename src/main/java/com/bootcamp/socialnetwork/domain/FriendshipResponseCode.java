package com.bootcamp.socialnetwork.domain;

public enum FriendshipResponseCode {
    FRIEND_REMOVED("User deleted from the current user's friend list."),
    REQUEST_APPROVED("Friend request from the user approved."),
    REQUEST_DECLINED("Friend request from the user declined."),
    REQUEST_SENT("A friend request was sent to the user."),
    REQUEST_SUGGESTION_DELETED("Friend request suggestion for the user deleted.");

    private String message;

    FriendshipResponseCode() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    FriendshipResponseCode(String message) {
        this.message = message;
    }
}
