package com.bootcamp.socialnetwork.service;

import com.bootcamp.socialnetwork.service.dto.ConversationDto;
import com.bootcamp.socialnetwork.service.dto.UserProfileDto;
import com.bootcamp.socialnetwork.web.rest.errors.EntityAlreadyExistException;
import com.bootcamp.socialnetwork.web.rest.errors.EntityNotFoundException;
import com.bootcamp.socialnetwork.web.rest.errors.PrivilegeException;
import com.bootcamp.socialnetwork.web.rest.errors.UserUnauthorizedException;

import java.util.List;
import java.util.Set;

/**
 * Service class for managing conversations.
 */
public interface ConversationService {

    boolean isExist(Long id);

    ConversationDto find(Long id) throws EntityNotFoundException, UserUnauthorizedException, PrivilegeException;

    ConversationDto findWithoutVerification(Long id) throws EntityNotFoundException;

    List<ConversationDto> findAll();

    List<ConversationDto> findAllByParticipant(Long participantId);

    List<ConversationDto> findAllByParticipants(Set<UserProfileDto> participants);

    Long findQuantityOfConversationsContainingUnreadMessages() throws UserUnauthorizedException;

    void resetUnreadMessagesOfConversation(Long id)
            throws EntityNotFoundException, UserUnauthorizedException, PrivilegeException;

    ConversationDto save(ConversationDto conversationDto)
            throws EntityAlreadyExistException, UserUnauthorizedException, PrivilegeException;

    ConversationDto update(Long id, ConversationDto conversationDto)
            throws EntityNotFoundException, UserUnauthorizedException, PrivilegeException;
}
