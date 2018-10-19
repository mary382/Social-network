package com.bootcamp.socialnetwork.service.dto;

import java.io.Serializable;

public abstract class AbstractDto implements Serializable{
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
