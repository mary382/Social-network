package com.bootcamp.socialnetwork.web.rest.errors;

public class UserBlockedException extends Exception {
    public UserBlockedException() {
    }

    public UserBlockedException(String message) {
        super(message);
    }
}
