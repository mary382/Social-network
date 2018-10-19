package com.bootcamp.socialnetwork.web.rest;

import com.bootcamp.socialnetwork.service.UserService;
import com.bootcamp.socialnetwork.service.dto.UserDto;
import com.bootcamp.socialnetwork.web.rest.errors.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * REST controller for managing users.
 */
@RestController
@RequestMapping("/api")
public class UserApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserApiController.class);

    @Autowired
    private UserService userService;


    /**
     * Create a new user.
     *
     * @param userDto              the user to create.
     * @return the ResponseEntity with status 201 (Created) and with body containing
     * the new user, or with status 409 (Conflict) if the email or ID is already in use.
     */
    @PostMapping("/user/")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserDto userDto) {

        LOGGER.info("Creating user: {}.", userDto);

        try {
            userService.save(userDto);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (EntityAlreadyExistException e) {
            LOGGER.error("Unable to create. {}", e.getMessage());
            return new ResponseEntity<>(new CustomError(e.getMessage()), HttpStatus.CONFLICT);
        }
    }

    /**
     * Update an existing user.
     *
     * @param id      the ID of the user to update.
     * @param userDto the data to be assigned to the user.
     * @return the ResponseEntity with status 200 (OK), or with status 404 (Not Found)
     * if the user couldn't be found, or with status 403 (Forbidden) if the client has
     * no rights to update the user.
     */
    @PutMapping("/user/{id}")
    public ResponseEntity<?> updateUser(@PathVariable("id") Long id, @Valid @RequestBody UserDto userDto) {

        LOGGER.info("Updating user with ID {}.", id);

        try {
            userService.update(id, userDto);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            LOGGER.error("Unable to update. {}", e.getMessage());
            return new ResponseEntity<>(new CustomError(e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (UserUnauthorizedException | PrivilegeException e) {
            LOGGER.error("Unable to update. {}", e.getMessage());
            return new ResponseEntity<>(new CustomError(e.getMessage()), HttpStatus.FORBIDDEN);
        }
    }
}
