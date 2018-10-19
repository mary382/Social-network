package com.bootcamp.socialnetwork.service;

import com.bootcamp.socialnetwork.service.dto.UserProfileDto;
import com.bootcamp.socialnetwork.web.rest.errors.UserUnauthorizedException;

public interface SecurityService {

    void autoLogin(String username, String password);

    String findLoggedInUsername();

    String getPrincipalUsername() throws UserUnauthorizedException;

    UserProfileDto getPrincipalProfile() throws UserUnauthorizedException;

    Long getPrincipalId();

    boolean verifyUserAuthorization(Long principalId) throws UserUnauthorizedException;

    boolean isAuthenticated() throws UserUnauthorizedException;
}
