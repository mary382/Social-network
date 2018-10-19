package com.bootcamp.socialnetwork.service;

import com.bootcamp.socialnetwork.service.dto.MessageDto;
import com.bootcamp.socialnetwork.service.dto.UserProfileDto;
import com.bootcamp.socialnetwork.web.rest.errors.EntityAlreadyExistException;
import com.bootcamp.socialnetwork.web.rest.errors.EntityNotFoundException;
import com.bootcamp.socialnetwork.web.rest.errors.PrivilegeException;
import com.bootcamp.socialnetwork.web.rest.errors.UserUnauthorizedException;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Service class for managing messages.
 */
public interface MessageService {

    boolean isExist(Long id);

    MessageDto find(Long id)
            throws EntityNotFoundException, UserUnauthorizedException, PrivilegeException;

    List<MessageDto> findAll();

    List<MessageDto> findAllByAuthor(Long authorId);

    List<MessageDto> findAllByConversation(Long conversationId)
            throws UserUnauthorizedException, PrivilegeException;

    Page<MessageDto> findPaginatedByConversation(Long conversationId, Integer page, Integer size)
            throws UserUnauthorizedException, PrivilegeException;

    MessageDto save(MessageDto messageDto)
            throws EntityAlreadyExistException, UserUnauthorizedException, PrivilegeException;

    MessageDto save(MessageDto messageDto, UserProfileDto principal)
            throws EntityAlreadyExistException, PrivilegeException;

    MessageDto update(Long id, MessageDto messageDto)
            throws EntityNotFoundException, UserUnauthorizedException, PrivilegeException;

    void unblock(Long authorId, Long recipientId);

    void delete(Long id)
            throws EntityNotFoundException, UserUnauthorizedException, PrivilegeException;
}
