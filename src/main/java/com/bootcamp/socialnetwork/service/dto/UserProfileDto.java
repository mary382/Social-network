package com.bootcamp.socialnetwork.service.dto;

import com.bootcamp.socialnetwork.config.Constants;
import com.bootcamp.socialnetwork.domain.Sex;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.validation.constraints.Pattern;
import java.util.Date;

/**
 * Profile DTO of entity User.
 */
public class UserProfileDto extends AbstractDto {

    private String email;

    @JsonIgnore
    private String password;

    @Pattern(regexp = Constants.PROPER_NOUN_REGEX)
    private String firstName;

    @Pattern(regexp = Constants.PROPER_NOUN_REGEX)
    private String lastName;

    private Sex sex;

    private Date birthday;

    private String imageUrl;

    private String country;

    private String city;

    private String resume;


    public UserProfileDto() {

    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getResume() {
        return resume;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserProfileDto that = (UserProfileDto) o;

        return super.getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return super.getId().hashCode();
    }

    @Override
    public String toString() {
        return "UserProfileDto{" +
                "id='" + super.getId() + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", sex=" + sex +
                ", birthday=" + birthday +
                ", imageUrl='" + imageUrl + '\'' +
                ", country='" + country + '\'' +
                ", city='" + city + '\'' +
                ", resume='" + resume + '\'' +
                '}';
    }
}
