package com.bootcamp.socialnetwork.web.rest.errors;

public class UserUnauthorizedException extends Exception {
    public UserUnauthorizedException(String message){
        super(message);
    }
}
