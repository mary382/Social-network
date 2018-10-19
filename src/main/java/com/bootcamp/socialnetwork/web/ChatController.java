package com.bootcamp.socialnetwork.web;

import com.bootcamp.socialnetwork.service.ConversationService;
import com.bootcamp.socialnetwork.service.MessageService;
import com.bootcamp.socialnetwork.service.UserService;
import com.bootcamp.socialnetwork.service.dto.MessageDto;
import com.bootcamp.socialnetwork.service.dto.UserProfileDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class ChatController {

    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private MessageService messageService;

    @Autowired
    private ConversationService conversationService;

    @Autowired
    private UserService userService;


    @MessageMapping("/chat")
    public void send(MessageDto messageDto, Principal principal) throws Exception {
        messageDto = messageService.save(messageDto, userService.findProfileByEmail(principal.getName()));
        for (UserProfileDto participant :
                conversationService.findWithoutVerification(messageDto.getConversationId()).getParticipants()) {
            template.convertAndSend("/topic/messages/" + participant.getEmail(), messageDto);
        }
    }
}
