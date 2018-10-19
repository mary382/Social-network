package com.bootcamp.socialnetwork.service.dto;

import org.hibernate.validator.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO of entity Post.
 */
public class PostDto extends AbstractDto implements Comparable<PostDto> {

    private UserProfileDto author;

    /**
     * Owner of the post (Group (negative), User (positive)).
     */
    private Long ownerId;

    private Long time;

    @NotBlank
    private String text;

    private List<AttachmentDto> attachments;


    public PostDto() {
        attachments = new ArrayList<>();
    }


    public UserProfileDto getAuthor() {
        return author;
    }

    public void setAuthor(UserProfileDto author) {
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

    public List<AttachmentDto> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<AttachmentDto> attachments) {
        this.attachments = attachments;
    }


    @Override
    public int compareTo(PostDto postDto) {
        return postDto.getTime().compareTo(time);
    }

    @Override
    public String toString() {
        return "PostDto{" +
                "id=" + super.getId() +
                ", author=" + author +
                ", ownerId=" + ownerId +
                ", time=" + time +
                ", text='" + text + '\'' +
                '}';
    }
}
