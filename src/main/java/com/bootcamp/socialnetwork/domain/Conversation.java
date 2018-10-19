package com.bootcamp.socialnetwork.domain;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Entity of Conversation.
 */
@Entity
@Table(name = "conversation")
public class Conversation extends AbstractEntity {

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "conversation_participant",
            joinColumns = {@JoinColumn(name = "conversation_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")})
    private Set<User> participants;

    @ElementCollection
    @JoinTable(name = "conversation_notifications_quantity",
            joinColumns = @JoinColumn(name = "conversation_id"))
    @MapKeyColumn(name = "participant_id")
    @Column(name = "notifications_quantity")
    private Map<Long, Long> notificationsQuantity;

    private Long lastModified;


    public Conversation() {
        this.notificationsQuantity = new HashMap<>();
    }


    public Set<User> getParticipants() {
        return participants;
    }

    public void setParticipants(Set<User> participants) {
        this.participants = participants;
    }

    public Map<Long, Long> getNotificationsQuantity() {
        return notificationsQuantity;
    }

    public void setNotificationsQuantity(Map<Long, Long> notificationsQuantity) {
        this.notificationsQuantity = notificationsQuantity;
    }

    public Long getLastModified() {
        return lastModified;
    }

    public void setLastModified(Long lastModified) {
        this.lastModified = lastModified;
    }
}
