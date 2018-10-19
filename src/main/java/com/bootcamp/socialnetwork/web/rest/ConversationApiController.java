package com.bootcamp.socialnetwork.web.rest;

import com.bootcamp.socialnetwork.service.ConversationService;
import com.bootcamp.socialnetwork.service.SecurityService;
import com.bootcamp.socialnetwork.service.dto.ConversationDto;
import com.bootcamp.socialnetwork.util.Response;
import com.bootcamp.socialnetwork.web.rest.errors.EntityAlreadyExistException;
import com.bootcamp.socialnetwork.web.rest.errors.EntityNotFoundException;
import com.bootcamp.socialnetwork.web.rest.errors.PrivilegeException;
import com.bootcamp.socialnetwork.web.rest.errors.UserUnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * REST controller for managing conversations.
 */
@RestController
@RequestMapping("/api")
public class ConversationApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConversationApiController.class);

    @Autowired
    private ConversationService conversationService;

    @Autowired
    private SecurityService securityService;


    /**
     * Create a new conversation.
     *
     * @param conversationDto the conversation to create.
     * @return the ResponseEntity with status 201 (Created) and with body containing
     * created conversation, or with status 409 (Conflict) if the conversation ID is
     * already in use, user is not authorized or tries to create a conversation that
     * does not contain the user himself.
     */
    @PostMapping("/conversation/")
    public ResponseEntity<?> createConversation(@Valid @RequestBody ConversationDto conversationDto) {

        LOGGER.info("Creating conversation: {}.", conversationDto);

        try {
            conversationDto = conversationService.save(conversationDto);
            return new ResponseEntity<>(new Response(conversationDto, null, null), HttpStatus.CREATED);
        } catch (EntityAlreadyExistException | UserUnauthorizedException | PrivilegeException e) {
            LOGGER.error("Unable to create. {}", e.getMessage());
            return new ResponseEntity<>(new Response(null, null, e.getMessage()), HttpStatus.CONFLICT);
        }
    }

    /**
     * Get conversation by ID.
     *
     * @param id the ID of the conversation to find.
     * @return the ResponseEntity with status 200 (OK) and with body containing the
     * conversation found by ID, or with status 404 (Not Found), or with status 409
     * (Conflict) if the user does not have rights to access the conversation.
     */
    @GetMapping("/conversation/{id}")
    public ResponseEntity<?> getConversation(@PathVariable("id") Long id) {

        LOGGER.info("Fetching conversation with ID {}.", id);

        try {
            ConversationDto conversationDto = conversationService.find(id);
            return new ResponseEntity<>(new Response(conversationDto, null, null), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            LOGGER.error("Conversation not found. {}", e.getMessage());
            return new ResponseEntity<>(new Response(null, null, e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (UserUnauthorizedException | PrivilegeException e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(new Response(null, null, e.getMessage()), HttpStatus.CONFLICT);
        }
    }

    /**
     * Get all conversations of the principal.
     *
     * @return the ResponseEntity with status 200 (OK) and with body containing all
     * conversations of the principal, or with status 204 (No Content), or with
     * status 403 (Forbidden) if the user is not authorized.
     */
    @GetMapping("/user/principal/conversation/")
    public ResponseEntity<?> getAllConversationsOfPrincipal() {

        LOGGER.info("Fetching conversations of the principal.");

        try {
            Long principalId = securityService.getPrincipalProfile().getId();
            List<ConversationDto> conversations = conversationService.findAllByParticipant(principalId);
            if (conversations.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(new Response(conversations, null, null), HttpStatus.OK);
        } catch (UserUnauthorizedException e) {
            LOGGER.error("Unable to fetch conversations of principal. {}", e.getMessage());
            return new ResponseEntity<>(new Response(null, null, e.getMessage()), HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Get quantity of the principal's conversations containing unread messages.
     *
     * @return the ResponseEntity with status 200 (OK) and with body containing
     * quantity of the principal's conversations containing unread messages, or
     * with status 401 (Unauthorized).
     */
    @GetMapping("/user/principal/conversation/unopenedMessagesQuantity")
    public ResponseEntity<?> getQuantityOfConversationsContainingUnreadMessages() {

        LOGGER.info("Fetching unopened conversations of the principal.");

        try {
            Long quantity = conversationService.findQuantityOfConversationsContainingUnreadMessages();
            return new ResponseEntity<>(new Response(quantity, null, null), HttpStatus.OK);
        } catch (UserUnauthorizedException e) {
            LOGGER.error("Unable to fetch unopened conversations of principal. {}", e.getMessage());
            return new ResponseEntity<>(new Response(null, null, e.getMessage()),
                    HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Reset unread messages of a specific conversation.
     *
     * @return the ResponseEntity with status 200 (OK) or with status 409 (Conflict)
     * if the user does not have rights to reset unread messages of the conversation.
     */
    @PostMapping("/conversation/{id}/resetUnopenedMessagesQuantity")
    public ResponseEntity<?> resetUnreadMessagesOfConversation(@PathVariable("id") Long id) {

        LOGGER.info("Resetting unread messages of conversation with ID {}.", id);

        try {
            conversationService.resetUnreadMessagesOfConversation(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (EntityNotFoundException | UserUnauthorizedException | PrivilegeException e) {
            LOGGER.error("Unable to reset unread messages. {}", e.getMessage());
            return new ResponseEntity<>(new Response(null, null, e.getMessage()), HttpStatus.CONFLICT);
        }
    }

    /**
     * Update an existing conversation.
     *
     * @param id              the ID of the conversation to update.
     * @param conversationDto the data to be assigned to the conversation.
     * @return the ResponseEntity with status 200 (OK) and with body containing the
     * updated conversation, or with status 404 (Not Found) if the conversation could
     * not be found, or with status 409 (Conflict) if user is not authorized or tries
     * to update a conversation that does not contain the user himself.
     */
    @PutMapping("/conversation/{id}")
    public ResponseEntity<?> updateConversation(@PathVariable("id") Long id,
                                                @Valid @RequestBody ConversationDto conversationDto) {

        LOGGER.info("Updating the conversation with ID {}.", id);

        try {
            conversationDto = conversationService.update(id, conversationDto);
            return new ResponseEntity<>(new Response(conversationDto, null, null), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            LOGGER.error("Unable to update. {}", e.getMessage());
            return new ResponseEntity<>(new Response(null, null, e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (UserUnauthorizedException | PrivilegeException e) {
            LOGGER.error("Unable to update. {}", e.getMessage());
            return new ResponseEntity<>(new Response(null, null, e.getMessage()), HttpStatus.CONFLICT);
        }
    }
}
