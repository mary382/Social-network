package com.bootcamp.socialnetwork.repository;

import com.bootcamp.socialnetwork.domain.AbstractRepository;
import com.bootcamp.socialnetwork.domain.Conversation;
import com.bootcamp.socialnetwork.domain.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the Conversation entity.
 */
@Repository
public interface ConversationRepository extends AbstractRepository<Conversation> {

    List<Conversation> findAllByParticipantsContaining(User participant);

    @Query("SELECT c FROM Conversation c " +
            "WHERE :p1 MEMBER c.participants AND :p2 MEMBER c.participants " +
            "AND SIZE(c.participants) = 2")
    List<Conversation> findAllByParticipants(@Param("p1") User participant1, @Param("p2") User participant2);

    @Query("SELECT COUNT(c) FROM Conversation c JOIN c.notificationsQuantity n " +
            "WHERE :p MEMBER c.participants AND KEY(n) = :id AND n > 0")
    Long findQuantityOfConversationsContainingUnreadMessages(@Param("p") User participant,
                                                             @Param("id") Long participantId);
}
