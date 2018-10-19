package com.bootcamp.socialnetwork.domain;

import javax.persistence.*;

/**
 * Entity of Role.
 */
@Entity
@Table(name = "role", uniqueConstraints = @UniqueConstraint(columnNames = {"role", "user"}))
public class Role extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user", nullable = false)
    private User user;

    @Column(nullable = false, length = 45)
    private String role;


    public Role() {

    }

    public Role(User user, String role) {
        this.user = user;
        this.role = role;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
