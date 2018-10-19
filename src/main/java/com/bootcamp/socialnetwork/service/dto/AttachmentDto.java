package com.bootcamp.socialnetwork.service.dto;

import com.bootcamp.socialnetwork.util.AttachmentType;

public class AttachmentDto extends AbstractDto implements Comparable<AttachmentDto>{

    private String title;

    private String url;

    private AttachmentType type;

    public AttachmentDto() {
    }

    public AttachmentDto(String title, String url, AttachmentType type) {
        this.title = title;
        this.url = url;
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public AttachmentType getType() {
        return type;
    }

    public void setType(AttachmentType type) {
        this.type = type;
    }

    @Override
    public int compareTo(AttachmentDto o) {
        return type.compareTo(o.getType());
    }
}
