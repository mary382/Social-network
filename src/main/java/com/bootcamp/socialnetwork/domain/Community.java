package com.bootcamp.socialnetwork.domain;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity of Community.
 *
 */
@Entity
@Table(name = "community")
public class Community extends AbstractEntity{

    private String title;

    @Column(name = "logo_url")
    private String logoUrl;

    private String info;

    /**
     * Describes the type of the community. Legal values are:
     * <ul>
     * <li>OPEN</li>
     * <li>CLOSED</li>
     * </ul>
     * By default - OPEN.
     */
    @Enumerated(EnumType.STRING)
    private CommunityType type;

    @ManyToOne(fetch = FetchType.LAZY)
    private User owner;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "community_participants",
            joinColumns = {@JoinColumn(name = "community_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")})
    private Set<User> participants;

    @Column(name = "participants_count")
    private Integer participantsCount;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "community_blocked_users",
            joinColumns = {@JoinColumn(name = "community_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")})
    private Set<User> blockedUsers;

    public Community() {
        participants = new HashSet<>();
        blockedUsers = new HashSet<>();
        participantsCount = 0;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public CommunityType getType() {
        return type;
    }

    public void setType(CommunityType type) {
        this.type = type;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Set<User> getParticipants() {
        return participants;
    }

    public void setParticipants(Set<User> participants) {
        this.participants = participants;
    }

    public Set<User> getBlockedUsers() {
        return blockedUsers;
    }

    public void setBlockedUsers(Set<User> blockedUsers) {
        this.blockedUsers = blockedUsers;
    }

    public Integer getParticipantsCount() {
        return participantsCount;
    }

    public void setParticipantsCount(Integer participantsCount) {
        this.participantsCount = participantsCount;
    }
}
