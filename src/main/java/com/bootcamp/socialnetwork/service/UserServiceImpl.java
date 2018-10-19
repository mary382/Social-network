package com.bootcamp.socialnetwork.service;

import com.bootcamp.socialnetwork.domain.Role;
import com.bootcamp.socialnetwork.domain.User;
import com.bootcamp.socialnetwork.repository.UserRepository;
import com.bootcamp.socialnetwork.service.dto.PostDto;
import com.bootcamp.socialnetwork.service.dto.UserDto;
import com.bootcamp.socialnetwork.service.dto.UserProfileDto;
import com.bootcamp.socialnetwork.util.AttachmentManager;
import com.bootcamp.socialnetwork.util.AttachmentType;
import com.bootcamp.socialnetwork.util.DropboxClient;
import com.bootcamp.socialnetwork.web.rest.errors.*;
import com.dropbox.core.DbxException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service("userService")
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    SecurityService securityService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostService postService;


    // -------------------- Common --------------------

    @Override
    public boolean isExist(Long id) {
        return userRepository.exists(id);
    }

    @Override
    public boolean isEmailUsed(String email) {
        return userRepository.findByEmail(email) != null;
    }

    @Override
    public void checkExist(Long id) throws EntityNotFoundException {
        if (!isExist(id)) {
            throw new EntityNotFoundException(String.format("User with id %d not found.", id));
        }
    }


    // -------------------- UserDto --------------------

    @Override
    public UserDto find(Long id) {

        User user = userRepository.findOne(id);
        return (user == null) ?
                null : modelMapper.map(user, UserDto.class);
    }

    @Override
    public UserDto findUser(Long id) throws EntityNotFoundException {

        User user = userRepository.findOne(id);

        if (user == null) {
            throw new EntityNotFoundException("User not found.");
        }

        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public List<UserDto> findAll() {

        List<UserDto> users = new ArrayList<>();
        for (User user : userRepository.findAll()) {
            users.add(modelMapper.map(user, UserDto.class));
        }

        return users;
    }

    @Override
    public void save(UserDto userDto) throws EntityAlreadyExistException {

        if (userDto.getId() != null && isExist(userDto.getId())) {
            throw new EntityAlreadyExistException("User with this ID already exist.");
        }
        if (isEmailUsed(userDto.getEmail())) {
            throw new EntityAlreadyExistException("This email is already in use.");
        }

        User user = modelMapper.map(userDto, User.class);
        user.setEmail(user.getEmail().toLowerCase());
        user.setEnabled(true);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.getRoles().add(new Role(user, "ROLE_USER"));

        userRepository.save(user);
    }

    @Override
    public void update(Long id, UserDto userDto)
            throws EntityNotFoundException, UserUnauthorizedException, PrivilegeException {

        User user = userRepository.findOne(id);

        if (user == null) {
            throw new EntityNotFoundException("User not found.");
        }
        if (!user.getId().equals(securityService.getPrincipalProfile().getId())) {
            throw new PrivilegeException("User can update only his own account.");
        }

        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setSex(userDto.getSex());
        user.setBirthday(userDto.getBirthday());

        userRepository.save(user);
    }


    // -------------------- UserProfileDto --------------------

    @Override
    public UserProfileDto findProfile(Long id) {

        User user = userRepository.findOne(id);
        return (user == null) ?
                null : modelMapper.map(user, UserProfileDto.class);
    }

    @Override
    public UserProfileDto findUserProfile(Long id) throws EntityNotFoundException {

        User user = userRepository.findOne(id);

        if (user == null) {
            throw new EntityNotFoundException("User not found.");
        }

        return modelMapper.map(user, UserProfileDto.class);
    }

    @Override
    public UserProfileDto findProfileByEmail(String email) throws EntityNotFoundException {

        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new EntityNotFoundException("User not found.");
        }

        return modelMapper.map(user, UserProfileDto.class);
    }

    @Override
    public List<UserProfileDto> findAllProfiles() {

        List<UserProfileDto> users = new ArrayList<>();
        for (User user : userRepository.findAll()) {
            users.add(modelMapper.map(user, UserProfileDto.class));
        }

        return users;
    }

    @Override
    public UserProfileDto update(Long id, UserProfileDto userProfileDto)
            throws EntityNotFoundException, UserUnauthorizedException, PrivilegeException {

        User user = userRepository.findOne(id);

        if (user == null) {
            throw new EntityNotFoundException("User not found.");
        }
        if (!user.getId().equals(securityService.getPrincipalProfile().getId())) {
            throw new PrivilegeException("User can update only his own profile.");
        }

        user.setFirstName(userProfileDto.getFirstName());
        user.setLastName(userProfileDto.getLastName());
        user.setSex(userProfileDto.getSex());
        user.setBirthday(userProfileDto.getBirthday());
        user.setImageUrl(userProfileDto.getImageUrl());
        user.setCountry(userProfileDto.getCountry());
        user.setCity(userProfileDto.getCity());
        user.setResume(userProfileDto.getResume());

        userRepository.save(user);
        return modelMapper.map(user, UserProfileDto.class);
    }

    @Override
    public void uploadAvatar(Long id, Long principalId, MultipartFile avatar) throws EntityNotFoundException,
            UnsupportedFileTypeException, IOException, DbxException, PrivilegeException {
        UserProfileDto userProfileDto = findUserProfile(id);
        if (!principalId.equals(userProfileDto.getId())) throw new PrivilegeException(
                String.format("User with id %d does not have enough rights.", principalId));

        String fileExtension = AttachmentManager.getFileExtension(avatar.getOriginalFilename());
        if (AttachmentManager.getAttachmentType(fileExtension) != AttachmentType.IMAGE)
            throw new UnsupportedFileTypeException(
                    String.format("File with extension %s can not be avatar (avatar is only image)", fileExtension));

        String url = DropboxClient.uploadFile(avatar.getInputStream(), String.format("/user/%d/avatar.jpg", id));
        // Verifies if shared link is already exist.
        if (url != null) {
            userProfileDto.setImageUrl(url);
            userRepository.save(modelMapper.map(userProfileDto, User.class));
        }
    }

    @Override
    public void deleteAvatar(Long id, Long principalId) throws EntityNotFoundException, PrivilegeException, DbxException {
        UserProfileDto userProfileDto = findUserProfile(id);
        if (!principalId.equals(userProfileDto.getId())) throw new PrivilegeException(
                String.format("User with id %d does not have enough rights.", principalId));

        if (userProfileDto.getImageUrl() != null) {
            DropboxClient.removeFolder(String.format("/user/%d/avatar.jpg", id));
            userProfileDto.setImageUrl(null);
            userRepository.save(modelMapper.map(userProfileDto, User.class));
        }
    }

    @Override
    public Page<PostDto> getUserNews(Long id, Long principalId, Integer page, Integer size)
            throws EntityNotFoundException, PrivilegeException {

        UserProfileDto userProfileDto = findUserProfile(id);
        if (!principalId.equals(userProfileDto.getId())) throw new PrivilegeException(
                String.format("User with id %d does not have enough rights.", principalId));

        return postService.findPaginatedNews(id, page, size);
    }
}
