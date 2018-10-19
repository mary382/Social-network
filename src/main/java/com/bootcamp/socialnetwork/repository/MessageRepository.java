package com.bootcamp.socialnetwork.repository;

import com.bootcamp.socialnetwork.domain.AbstractRepository;
import com.bootcamp.socialnetwork.domain.Conversation;
import com.bootcamp.socialnetwork.domain.Message;
import com.bootcamp.socialnetwork.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the Message entity.
 */
@Repository
public interface MessageRepository extends AbstractRepository<Message> {

    List<Message> findAllByAuthor(User author);

    List<Message> findAllByConversation(Conversation conversation);

    @Query("SELECT m FROM Message m JOIN m.statuses s " +
            "WHERE m.conversation = :c AND KEY(s) = :p AND (s = 'READ' OR s = 'UNREAD')")
    List<Message> findAvailableByConversation(@Param("c") Conversation conversation, @Param("p") Long participantId);

    @Query("SELECT m FROM Message m JOIN m.statuses s " +
            "WHERE m.conversation = :c AND KEY(s) = :p AND (s = 'READ' OR s = 'UNREAD')")
    Page<Message> findAvailableByConversation(@Param("c") Conversation conversation,
                                              @Param("p") Long participantId,
                                              Pageable pageable);

    @Query("SELECT m FROM Message m JOIN m.statuses s " +
            "WHERE m.conversation = :c AND KEY(s) = :p AND s = 'UNREAD'")
    List<Message> findUnreadByConversation(@Param("c") Conversation conversation, @Param("p") Long participantId);

    @Query("SELECT m FROM Message m JOIN m.statuses s " +
            "WHERE m.author.id = :a AND :r MEMBER m.conversation.participants AND s = 'BLOCKED'")
    List<Message> findBlockedByAuthorAndRecipient(@Param("a") Long authorId, @Param("r") User recipient);
}
