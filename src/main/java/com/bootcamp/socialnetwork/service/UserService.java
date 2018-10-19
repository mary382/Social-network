package com.bootcamp.socialnetwork.service;

import com.bootcamp.socialnetwork.service.dto.PostDto;
import com.bootcamp.socialnetwork.service.dto.UserDto;
import com.bootcamp.socialnetwork.service.dto.UserProfileDto;
import com.bootcamp.socialnetwork.web.rest.errors.*;
import com.dropbox.core.DbxException;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Service class for managing users.
 */
public interface UserService {

    // -------------------- Common --------------------

    boolean isExist(Long id);

    boolean isEmailUsed(String email);

    void checkExist(Long id) throws EntityNotFoundException;


    // -------------------- UserDto --------------------

    UserDto find(Long id);

    UserDto findUser(Long id) throws EntityNotFoundException;

    List<UserDto> findAll();

    void save(UserDto userDto) throws EntityAlreadyExistException;

    void update(Long id, UserDto userDto)
            throws EntityNotFoundException, UserUnauthorizedException, PrivilegeException;


    // -------------------- UserProfileDto --------------------

    UserProfileDto findProfile(Long id);

    UserProfileDto findUserProfile(Long id) throws EntityNotFoundException;

    UserProfileDto findProfileByEmail(String email) throws EntityNotFoundException;

    List<UserProfileDto> findAllProfiles();

    UserProfileDto update(Long id, UserProfileDto userProfileDto)
            throws EntityNotFoundException, UserUnauthorizedException, PrivilegeException;

    void uploadAvatar(Long id, Long principalId, MultipartFile avatar) throws EntityNotFoundException,
            UnsupportedFileTypeException, IOException, DbxException, PrivilegeException;

    void deleteAvatar(Long id, Long principalId) throws EntityNotFoundException, PrivilegeException, DbxException;

    Page<PostDto> getUserNews(Long id, Long principalId, Integer page, Integer size) throws EntityNotFoundException,
            PrivilegeException;
}
