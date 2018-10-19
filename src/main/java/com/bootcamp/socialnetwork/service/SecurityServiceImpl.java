package com.bootcamp.socialnetwork.service;

import com.bootcamp.socialnetwork.service.dto.UserProfileDto;
import com.bootcamp.socialnetwork.web.rest.errors.EntityNotFoundException;
import com.bootcamp.socialnetwork.web.rest.errors.UserUnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class SecurityServiceImpl implements SecurityService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityServiceImpl.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserService userService;


    @Override
    public void autoLogin(String username, String password) {

        UserDetails userDetails = userDetailsService.loadUserByUsername(username.toLowerCase());
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());

        authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        if (usernamePasswordAuthenticationToken.isAuthenticated()) {
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            LOGGER.debug(String.format("Logged in as %s successfully.", username));
        }
    }

    @Override
    public String findLoggedInUsername() {

        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals("anonymousUser")) {
            return null;
        }

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getUsername();
    }

    @Override
    public String getPrincipalUsername() throws UserUnauthorizedException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal().equals("anonymousUser")) {
            throw new UserUnauthorizedException("Unauthorized.");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }

    @Override
    public UserProfileDto getPrincipalProfile() throws UserUnauthorizedException {
        try {
            return userService.findProfileByEmail(getPrincipalUsername());
        } catch (EntityNotFoundException e) {
            throw new UserUnauthorizedException("Unauthorized.");
        }
    }

    /**
     * Gets authorized user id.
     *
     * @return authorized user id.
     */
    @Override
    public Long getPrincipalId() {
        try {
            String loggedInUsername = findLoggedInUsername();
            return userService.findProfileByEmail(loggedInUsername).getId();
        } catch (EntityNotFoundException e) {
            return null;
        }
    }

    /**
     * Verifies if user is authorized.
     *
     * @param principalId authorized user id
     * @return true if user is authorized.
     * @throws UserUnauthorizedException if user is unauthorized.
     */
    @Override
    public boolean verifyUserAuthorization(Long principalId) throws UserUnauthorizedException {
        if (principalId == null) throw new UserUnauthorizedException("Unauthorized");
        return true;
    }


    public boolean isAuthenticated() throws UserUnauthorizedException {
        if (getPrincipalId() == null) throw new UserUnauthorizedException("Unauthorized");
        return true;
    }
}
