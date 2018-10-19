package com.bootcamp.socialnetwork.web.rest;

import com.bootcamp.socialnetwork.service.MessageService;
import com.bootcamp.socialnetwork.service.dto.MessageDto;
import com.bootcamp.socialnetwork.util.Response;
import com.bootcamp.socialnetwork.web.rest.errors.EntityAlreadyExistException;
import com.bootcamp.socialnetwork.web.rest.errors.EntityNotFoundException;
import com.bootcamp.socialnetwork.web.rest.errors.PrivilegeException;
import com.bootcamp.socialnetwork.web.rest.errors.UserUnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * REST controller for managing messages.
 */
@RestController
@RequestMapping("/api")
public class MessageApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageApiController.class);

    @Autowired
    private MessageService messageService;


    /**
     * Create a new message.
     *
     * @param messageDto the message to create.
     * @return the ResponseEntity with status 201 (Created) and with body containing
     * the new message, or with status 409 (Conflict) if the message ID is already in
     * use, or if the user is not authorized, or if the user tries to add the message
     * to conversations in which he is not a member, or if the author and principal
     * IDs do not match.
     */
    @PostMapping("/message/")
    public ResponseEntity<?> createMessage(@Valid @RequestBody MessageDto messageDto) {

        LOGGER.info("Creating message: {}.", messageDto);

        try {
            messageDto = messageService.save(messageDto);
            return new ResponseEntity<>(new Response(messageDto, null, null), HttpStatus.CREATED);
        } catch (EntityAlreadyExistException | UserUnauthorizedException | PrivilegeException e) {
            LOGGER.error("Unable to create. {}", e.getMessage());
            return new ResponseEntity<>(new Response(null, null, e.getMessage()), HttpStatus.CONFLICT);
        }
    }

    /**
     * Get message by ID.
     *
     * @param id the ID of the message to find.
     * @return the ResponseEntity with status 200 (OK) and with body containing the
     * message found by ID, or with status 404 (Not Found), or with status 409 (Conflict)
     * if the user does not have rights to access the message.
     */
    @GetMapping("/message/{id}")
    public ResponseEntity<?> getMessage(@PathVariable("id") Long id) {

        LOGGER.info("Fetching message with ID {}.", id);

        try {
            MessageDto messageDto = messageService.find(id);
            return new ResponseEntity<>(new Response(messageDto, null, null), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            LOGGER.error("Conversation not found. {}", e.getMessage());
            return new ResponseEntity<>(new Response(null, null, e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (UserUnauthorizedException | PrivilegeException e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(new Response(null, null, e.getMessage()), HttpStatus.CONFLICT);
        }
    }

    /**
     * Get messages of a specific conversation.
     *
     * @param conversationId the ID of the conversation.
     * @return the ResponseEntity with status 200 (OK) and with body containing
     * messages of the conversation, or with status 204 (No Content), or with
     * status 403 (Forbidden) if the user is not a member of the conversation.
     */
    @GetMapping("/conversation/{conversationId}/message")
    public ResponseEntity<?> getAllMessagesOfConversation(@PathVariable("conversationId") Long conversationId,
                                                          @RequestParam(value = "page", required = false) Integer page,
                                                          @RequestParam(value = "size", required = false) Integer size) {

        LOGGER.info("Fetching messages of the conversation with ID {}.", conversationId);

        try {
            Page<MessageDto> messages = messageService.findPaginatedByConversation(
                    conversationId,
                    page,
                    size);
            return messages.getContent().isEmpty() ?
                    new ResponseEntity<>(HttpStatus.NO_CONTENT) :
                    new ResponseEntity<>(new Response(messages, null, null), HttpStatus.OK);
        } catch (UserUnauthorizedException | PrivilegeException e) {
            LOGGER.error("Unable to fetch messages of the conversation. {}", e.getMessage());
            return new ResponseEntity<>(new Response(null, null, e.getMessage()), HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Update an existing message.
     *
     * @param id         the ID of the message to update.
     * @param messageDto the data to be assigned to the message.
     * @return the ResponseEntity with status 200 (OK) and with body containing the
     * updated message, or with status 404 (Not Found) if the message couldn't be
     * found, or with status 409 (Conflict) if user is not authorized or tries to
     * update a message in a conversation that does not contain the user himself.
     */
    @PutMapping("/message/{id}")
    public ResponseEntity<?> updateMessage(@PathVariable("id") Long id, @Valid @RequestBody MessageDto messageDto) {

        LOGGER.info("Updating message with ID {}.", id);

        try {
            messageDto = messageService.update(id, messageDto);
            return new ResponseEntity<>(new Response(messageDto, null, null), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            LOGGER.error("Unable to update. {}", e.getMessage());
            return new ResponseEntity<>(new Response(null, null, e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (UserUnauthorizedException | PrivilegeException e) {
            LOGGER.error("Unable to update. {}", e.getMessage());
            return new ResponseEntity<>(new Response(null, null, e.getMessage()), HttpStatus.CONFLICT);
        }
    }

    /**
     * Delete message by ID.
     *
     * @param id the ID of the message to delete.
     * @return the ResponseEntity with status 204 (No Content) if the message
     * deleted successfully, or with status 404 (Not Found) if the message could
     * not be found, or with status 409 (Conflict) if user is not authorized or
     * tries to update a message in a conversation that does not contain the user
     * himself.
     */
    @DeleteMapping("/message/{id}")
    public ResponseEntity<?> deleteMessage(@PathVariable("id") Long id) {

        LOGGER.info("Deleting message with ID {}.", id);

        try {
            messageService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (EntityNotFoundException e) {
            LOGGER.error("Unable to delete. {}", e.getMessage());
            return new ResponseEntity<>(new Response(null, null, e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (UserUnauthorizedException | PrivilegeException e) {
            LOGGER.error("Unable to delete. {}", e.getMessage());
            return new ResponseEntity<>(new Response(null, null, e.getMessage()), HttpStatus.CONFLICT);
        }
    }
}
