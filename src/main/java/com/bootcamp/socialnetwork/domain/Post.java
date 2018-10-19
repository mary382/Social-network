package com.bootcamp.socialnetwork.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity of Post.
 */
@Entity
@Table(name = "post")
public class Post extends AbstractEntity {

    @Column
    private boolean enabled;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User author;

    /**
     * Owner of the post (Group (negative), User (positive)).
     */
    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Column(nullable = false)
    private Long time;

    @Column(length = 1000, nullable = false)
    private String text;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "post_attachments",
            joinColumns = {@JoinColumn(name = "post_id")},
            inverseJoinColumns = {@JoinColumn(name = "attachment_id")})
    private List<Attachment> attachments;


    public Post() {
        attachments = new ArrayList<>();
    }


    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
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

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }
}
