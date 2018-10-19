package com.bootcamp.socialnetwork.service.dto;

import com.bootcamp.socialnetwork.domain.CommunityType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

/**
 * DTO of entity Community.
 *
 */
public class CommunityDto extends AbstractDto {

    @NotEmpty
    @Size(max = 100, min = 3)
    private String title;

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
    private CommunityType type;

    /**
     * Owner of the group.
     */
    private UserProfileDto owner;

    private Set<UserProfileDto> participants;

    private Integer participantsCount;

    private Set<UserProfileDto> blockedUsers;

    public CommunityDto() {
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

    public UserProfileDto getOwner() {
        return owner;
    }

    public void setOwner(UserProfileDto owner) {
        this.owner = owner;
    }

    public Set<UserProfileDto> getParticipants() {
        return participants;
    }

    public void setParticipants(Set<UserProfileDto> participants) {
        this.participants = participants;
    }

    public Set<UserProfileDto> getBlockedUsers() {
        return blockedUsers;
    }

    public void setBlockedUsers(Set<UserProfileDto> blockedUsers) {
        this.blockedUsers = blockedUsers;
    }

    public Integer getParticipantsCount() {
        return participantsCount;
    }

    public void setParticipantsCount(Integer participantsCount) {
        this.participantsCount = participantsCount;
    }

    @Override
    public String toString() {
        return "CommunityDto{" +
                "id='" + super.getId() + '\'' +
                ", title='" + title + '\'' +
                ", info='" + info + '\'' +
                ", owner=" + owner +
                ", participants=" + participants +
                ", blockedUsers=" + blockedUsers +
                '}';
    }

    @Override
    public boolean equals(Object o){
        if (o == null) return false;
        if (o == this) return true;
        if (!(o instanceof CommunityDto)) return false;
        CommunityDto communityDto = (CommunityDto) o;
        return title.equals(communityDto.getTitle());
    }

    @Override
    public int hashCode(){
        return getId().hashCode();
    }
}
