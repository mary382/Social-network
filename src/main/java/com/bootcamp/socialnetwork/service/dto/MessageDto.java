package com.bootcamp.socialnetwork.service.dto;

import org.hibernate.validator.constraints.NotBlank;

import java.util.HashMap;
import java.util.Map;

/**
 * DTO of entity Message.
 */
public class MessageDto extends AbstractDto {

    @NotBlank
    private Long conversationId;

    private UserProfileDto author;

    private Long time;

    @NotBlank
    private String text;

    private Map<Long, String> statuses;


    public MessageDto() {
        this.statuses = new HashMap<>();
    }


    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }

    public UserProfileDto getAuthor() {
        return author;
    }

    public void setAuthor(UserProfileDto author) {
        this.author = author;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Map<Long, String> getStatuses() {
        return statuses;
    }

    public void setStatuses(Map<Long, String> statuses) {
        this.statuses = statuses;
    }


    @Override
    public String toString() {
        return "MessageDto{" +
                "id=" + super.getId() +
                ", conversationId=" + conversationId +
                ", author=" + author +
                ", time=" + time +
                ", text='" + text + '\'' +
                ", statuses=" + statuses +
                '}';
    }
}
