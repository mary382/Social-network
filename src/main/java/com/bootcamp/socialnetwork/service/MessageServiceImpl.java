package com.bootcamp.socialnetwork.service;

import com.bootcamp.socialnetwork.domain.Conversation;
import com.bootcamp.socialnetwork.domain.Message;
import com.bootcamp.socialnetwork.domain.MessageStatus;
import com.bootcamp.socialnetwork.domain.User;
import com.bootcamp.socialnetwork.repository.ConversationRepository;
import com.bootcamp.socialnetwork.repository.MessageRepository;
import com.bootcamp.socialnetwork.repository.UserRepository;
import com.bootcamp.socialnetwork.service.dto.MessageDto;
import com.bootcamp.socialnetwork.service.dto.UserProfileDto;
import com.bootcamp.socialnetwork.web.rest.errors.EntityAlreadyExistException;
import com.bootcamp.socialnetwork.web.rest.errors.EntityNotFoundException;
import com.bootcamp.socialnetwork.web.rest.errors.PrivilegeException;
import com.bootcamp.socialnetwork.web.rest.errors.UserUnauthorizedException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service("messageService")
@Transactional
public class MessageServiceImpl implements MessageService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendshipService friendshipService;

    @Autowired
    private SecurityService securityService;


    @Override
    public boolean isExist(Long id) {
        return messageRepository.exists(id);
    }

    @Override
    public MessageDto find(Long id)
            throws EntityNotFoundException, UserUnauthorizedException, PrivilegeException {

        Message message = messageRepository.findOne(id);

        if (message == null) {
            throw new EntityNotFoundException("Message not found.");
        }
        if (!message.getAuthor().getId().equals(securityService.getPrincipalProfile().getId())) {
            throw new PrivilegeException("User is not the author of this message");
        }

        return modelMapper.map(message, MessageDto.class);
    }

    @Override
    public List<MessageDto> findAll() {

        List<MessageDto> messages = new ArrayList<>();
        for (Message message : messageRepository.findAll()) {
            messages.add(modelMapper.map(message, MessageDto.class));
        }

        return messages;
    }

    @Override
    public List<MessageDto> findAllByAuthor(Long authorId) {

        List<MessageDto> messages = new ArrayList<>();
        for (Message message : messageRepository.findAllByAuthor(userRepository.findOne(authorId))) {
            messages.add(modelMapper.map(message, MessageDto.class));
        }

        return messages;
    }

    @Override
    public List<MessageDto> findAllByConversation(Long conversationId)
            throws UserUnauthorizedException, PrivilegeException {

        User principal = userRepository.findOne(securityService.getPrincipalProfile().getId());
        if (!conversationRepository.findOne(conversationId).getParticipants().contains(principal)) {
            throw new PrivilegeException("User is not a member of the conversation.");
        }

        List<MessageDto> messages = new ArrayList<>();
        for (Message message : messageRepository.findAvailableByConversation(
                conversationRepository.findOne(conversationId),
                principal.getId())) {
            messages.add(modelMapper.map(message, MessageDto.class));
        }

        return messages;
    }

    @Override
    public Page<MessageDto> findPaginatedByConversation(Long conversationId, Integer page, Integer size)
            throws UserUnauthorizedException, PrivilegeException {

        if (page == null || size == null) {
            size = Integer.MAX_VALUE;
            page = 1;
        }

        User principal = userRepository.findOne(securityService.getPrincipalProfile().getId());
        if (!conversationRepository.findOne(conversationId).getParticipants().contains(principal)) {
            throw new PrivilegeException("User is not a member of the conversation.");
        }

        Pageable pageRequest = new PageRequest(page - 1, size, Sort.Direction.DESC, "time");
        Page<Message> messagePage = messageRepository.findAvailableByConversation(
                conversationRepository.findOne(conversationId),
                principal.getId(),
                pageRequest);
        List<MessageDto> messages = new ArrayList<>();
        messagePage.getContent().forEach(message -> messages.add(modelMapper.map(message, MessageDto.class)));

        return new PageImpl<>(messages, pageRequest, messagePage.getTotalElements());
    }

    @Override
    public MessageDto save(MessageDto messageDto)
            throws EntityAlreadyExistException, UserUnauthorizedException, PrivilegeException {

        return save(messageDto, securityService.getPrincipalProfile());
    }

    @Override
    public MessageDto save(MessageDto messageDto, UserProfileDto profileDto)
            throws EntityAlreadyExistException, PrivilegeException {

        User principal = modelMapper.map(profileDto, User.class);

        if (messageDto.getId() != null && isExist(messageDto.getId())) {
            throw new EntityAlreadyExistException("Message with this ID already exist.");
        }
        if (!conversationRepository.findOne(messageDto.getConversationId()).getParticipants().contains(principal)) {
            throw new PrivilegeException("User can not add messages to conversations in which he is not a member.");
        }

        Message message = modelMapper.map(messageDto, Message.class);
        message.setAuthor(principal);
        message.setTime(System.currentTimeMillis());

        Conversation conversation = conversationRepository.findOne(message.getConversation().getId());
        for (User participant : conversation.getParticipants()) {
            MessageStatus status = !participant.getId().equals(principal.getId()) &&
                    friendshipService.inBlackList(participant.getId(), principal.getId()) ?
                    MessageStatus.BLOCKED : MessageStatus.UNREAD;
            message.getStatuses().put(participant.getId(), status);
            if (!participant.getId().equals(message.getAuthor().getId()) && status == MessageStatus.UNREAD) {
                conversation.setLastModified(message.getTime());
                conversation.getNotificationsQuantity().put(participant.getId(),
                        conversation.getNotificationsQuantity().get(participant.getId()) + 1L);
            }
        }

        conversationRepository.save(conversation);
        messageRepository.save(message);
        return modelMapper.map(message, MessageDto.class);
    }

    @Override
    public MessageDto update(Long id, MessageDto messageDto)
            throws EntityNotFoundException, UserUnauthorizedException, PrivilegeException {

        Message message = messageRepository.findOne(id);

        if (message == null) {
            throw new EntityNotFoundException("Message not found.");
        }
        if (!message.getAuthor().getId().equals(securityService.getPrincipalProfile().getId())) {
            throw new PrivilegeException("User can not update a message without being its author.");
        }

        message.setText(messageDto.getText());

        messageRepository.save(message);
        return modelMapper.map(message, MessageDto.class);
    }

    @Override
    public void unblock(Long authorId, Long recipientId) {

        for (Message message : messageRepository.findBlockedByAuthorAndRecipient(authorId,
                userRepository.findOne(recipientId))) {
            if (message.getStatuses().get(recipientId) != null) {
                message.getStatuses().put(recipientId, MessageStatus.UNREAD);
                message.getConversation().getNotificationsQuantity().put(recipientId,
                        message.getConversation().getNotificationsQuantity().get(recipientId) + 1L);
                messageRepository.save(message);
            }
        }
    }

    @Override
    public void delete(Long id) throws EntityNotFoundException, UserUnauthorizedException, PrivilegeException {

        Message message = messageRepository.findOne(id);
        if (message == null) {
            throw new EntityNotFoundException("Message not found.");
        }

        User principal = userRepository.findOne(securityService.getPrincipalProfile().getId());
        if (!message.getConversation().getParticipants().contains(principal)) {
            throw new PrivilegeException("User is not a member of the conversations.");
        }

        message.getStatuses().put(principal.getId(), MessageStatus.DELETED);

        messageRepository.save(message);
    }
}
