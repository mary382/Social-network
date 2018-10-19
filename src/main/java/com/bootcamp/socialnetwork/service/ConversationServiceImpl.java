package com.bootcamp.socialnetwork.service;

import com.bootcamp.socialnetwork.domain.Conversation;
import com.bootcamp.socialnetwork.domain.Message;
import com.bootcamp.socialnetwork.domain.MessageStatus;
import com.bootcamp.socialnetwork.domain.User;
import com.bootcamp.socialnetwork.repository.ConversationRepository;
import com.bootcamp.socialnetwork.repository.MessageRepository;
import com.bootcamp.socialnetwork.repository.UserRepository;
import com.bootcamp.socialnetwork.service.dto.ConversationDto;
import com.bootcamp.socialnetwork.service.dto.UserProfileDto;
import com.bootcamp.socialnetwork.web.rest.errors.EntityAlreadyExistException;
import com.bootcamp.socialnetwork.web.rest.errors.EntityNotFoundException;
import com.bootcamp.socialnetwork.web.rest.errors.PrivilegeException;
import com.bootcamp.socialnetwork.web.rest.errors.UserUnauthorizedException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service("conversationService")
@Transactional
public class ConversationServiceImpl implements ConversationService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    SecurityService securityService;


    @Override
    public boolean isExist(Long id) {
        return conversationRepository.exists(id);
    }

    @Override
    public ConversationDto find(Long id)
            throws EntityNotFoundException, UserUnauthorizedException, PrivilegeException {

        Conversation conversation = conversationRepository.findOne(id);

        if (conversation == null) {
            throw new EntityNotFoundException("Conversation not found.");
        }
        if (!conversation.getParticipants().contains(
                userRepository.findOne(securityService.getPrincipalProfile().getId()))) {
            throw new PrivilegeException("The conversation does not contain the user.");
        }

        return modelMapper.map(conversation, ConversationDto.class);
    }

    @Override
    public ConversationDto findWithoutVerification(Long id) throws EntityNotFoundException {

        Conversation conversation = conversationRepository.findOne(id);

        if (conversation == null) {
            throw new EntityNotFoundException("Conversation not found.");
        }

        return modelMapper.map(conversation, ConversationDto.class);
    }

    @Override
    public List<ConversationDto> findAll() {

        List<ConversationDto> conversations = new ArrayList<>();
        for (Conversation conversation : conversationRepository.findAll()) {
            conversations.add(modelMapper.map(conversation, ConversationDto.class));
        }

        return conversations;
    }

    @Override
    public List<ConversationDto> findAllByParticipant(Long participantId) {

        List<ConversationDto> conversations = new ArrayList<>();
        for (Conversation conversation :
                conversationRepository.findAllByParticipantsContaining(userRepository.findOne(participantId))) {
            conversations.add(modelMapper.map(conversation, ConversationDto.class));
        }

        return conversations;
    }

    @Override
    public List<ConversationDto> findAllByParticipants(Set<UserProfileDto> participants) {

        List<User> users = new ArrayList<>();
        for (UserProfileDto participant : participants) {
            users.add(modelMapper.map(participant, User.class));
        }

        List<ConversationDto> conversations = new ArrayList<>();
        for (Conversation conversation : conversationRepository.findAllByParticipants(users.get(0), users.get(1))) {
            conversations.add(modelMapper.map(conversation, ConversationDto.class));
        }

        return conversations;
    }

    @Override
    public Long findQuantityOfConversationsContainingUnreadMessages() throws UserUnauthorizedException {

        User principal = userRepository.findOne(securityService.getPrincipalProfile().getId());
        return conversationRepository.findQuantityOfConversationsContainingUnreadMessages(
                principal,
                principal.getId());
    }

    @Override
    public void resetUnreadMessagesOfConversation(Long id)
            throws EntityNotFoundException, UserUnauthorizedException, PrivilegeException {

        Conversation conversation = conversationRepository.findOne(id);

        if (conversation == null) {
            throw new EntityNotFoundException("Conversation not found.");
        }
        User principal = userRepository.findOne(securityService.getPrincipalProfile().getId());
        if (!conversation.getParticipants().contains(principal)) {
            throw new PrivilegeException("The conversation does not contain the principal.");
        }

        conversation.getNotificationsQuantity().put(principal.getId(), 0L);
        for (Message message : messageRepository.findUnreadByConversation(conversation, principal.getId())) {
            if (message.getStatuses().get(principal.getId()) == MessageStatus.UNREAD) {
                message.getStatuses().put(principal.getId(), MessageStatus.READ);
                message.getStatuses().put(message.getAuthor().getId(), MessageStatus.READ);
                messageRepository.save(message);
            }
        }

        conversationRepository.save(conversation);
    }

    @Override
    public ConversationDto save(ConversationDto conversationDto)
            throws EntityAlreadyExistException, UserUnauthorizedException, PrivilegeException {

        if (conversationDto.getId() != null && isExist(conversationDto.getId())) {
            throw new EntityAlreadyExistException("Conversation with this ID already exist.");
        }
        if (!conversationDto.getParticipants().contains(securityService.getPrincipalProfile())) {
            throw new PrivilegeException("User can not create a conversation that does not contain the user himself.");
        }
        if (conversationDto.getParticipants().size() == 2) {
            List<ConversationDto> list = findAllByParticipants(conversationDto.getParticipants());
            if (!list.isEmpty()) {
                return list.get(0);
            }
        }

        Conversation conversation = modelMapper.map(conversationDto, Conversation.class);
        conversation.setLastModified(System.currentTimeMillis());
        for (User participant : conversation.getParticipants()) {
            conversation.getNotificationsQuantity().put(participant.getId(), 0L);
        }

        conversationRepository.save(conversation);
        return modelMapper.map(conversation, ConversationDto.class);
    }

    @Override
    public ConversationDto update(Long id, ConversationDto conversationDto)
            throws EntityNotFoundException, UserUnauthorizedException, PrivilegeException {

        Conversation conversation = conversationRepository.findOne(id);

        if (conversation == null) {
            throw new EntityNotFoundException("Conversation not found.");
        }
        if (!conversation.getParticipants().contains(
                userRepository.findOne(securityService.getPrincipalProfile().getId()))) {
            throw new PrivilegeException("User can not update a conversation that does not contain the user himself.");
        }

        conversation.setLastModified(System.currentTimeMillis());

        conversationRepository.save(conversation);
        return modelMapper.map(conversation, ConversationDto.class);
    }
}
