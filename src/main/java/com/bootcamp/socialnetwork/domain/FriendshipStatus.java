package com.bootcamp.socialnetwork.domain;

public enum FriendshipStatus {
    FRIEND("Friend"),
    INCOMING_REQUEST("Incoming friend request"),
    NONE("Not a friend"),
    SEND_REQUEST("Friend request was sent");

    private String message;

    FriendshipStatus(String message) {
        this.message = message;
    }

    FriendshipStatus() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
