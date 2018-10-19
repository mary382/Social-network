package com.bootcamp.socialnetwork.domain;

import com.bootcamp.socialnetwork.util.AttachmentType;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

@Entity
@Table(name = "attachment")
public class Attachment extends AbstractEntity {

    private String title;

    private String url;

    @Enumerated(EnumType.STRING)
    private AttachmentType type;

    public Attachment() {
    }

    public Attachment(String title, String url, AttachmentType type) {
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
}
