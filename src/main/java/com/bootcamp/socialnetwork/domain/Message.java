package com.bootcamp.socialnetwork.domain;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Entity of Message.
 */
@Entity
@Table(name = "message")
public class Message extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation", nullable = false)
    private Conversation conversation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User author;

    @Column(nullable = false)
    private Long time;

    @Column(length = 1000, nullable = false)
    private String text;

    @ElementCollection
    @JoinTable(name = "message_status",
            joinColumns = @JoinColumn(name = "message_id"))
    @MapKeyColumn(name = "participant_id")
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Map<Long, MessageStatus> statuses;


    public Message() {
        this.statuses = new HashMap<>();
    }


    public Conversation getConversation() {
        return conversation;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
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

    public Map<Long, MessageStatus> getStatuses() {
        return statuses;
    }

    public void setStatuses(Map<Long, MessageStatus> statuses) {
        this.statuses = statuses;
    }
}
