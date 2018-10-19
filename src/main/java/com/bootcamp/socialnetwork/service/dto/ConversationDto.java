package com.bootcamp.socialnetwork.service.dto;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * DTO of entity Conversation.
 */
public class ConversationDto extends AbstractDto {

    private Set<UserProfileDto> participants;

    private Map<Long, Long> notificationsQuantity;

    private Long lastModified;


    public ConversationDto() {
        this.notificationsQuantity = new HashMap<>();
    }


    public Set<UserProfileDto> getParticipants() {
        return participants;
    }

    public void setParticipants(Set<UserProfileDto> participants) {
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


    @Override
    public String toString() {
        return "ConversationDto{" +
                "id=" + super.getId() +
                ", participants=" + participants +
                ", notificationsQuantity=" + notificationsQuantity +
                ", lastModified=" + lastModified +
                '}';
    }
}
