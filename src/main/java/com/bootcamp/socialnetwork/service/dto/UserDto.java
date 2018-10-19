package com.bootcamp.socialnetwork.service.dto;

import com.bootcamp.socialnetwork.config.Constants;
import com.bootcamp.socialnetwork.domain.Sex;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * Base DTO of entity User.
 */
public class UserDto extends AbstractDto {

    @Pattern(regexp = Constants.EMAIL_REGEX)
    @Size(max = 100)
    private String email;

    @Pattern(regexp = Constants.PASSWORD_REGEX)
    private String password;

    @Pattern(regexp = Constants.PROPER_NOUN_REGEX)
    private String firstName;

    @Pattern(regexp = Constants.PROPER_NOUN_REGEX)
    private String lastName;

    private Sex sex;

    private Date birthday;


    public UserDto() {

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


    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o == this) return true;
        if (!(o instanceof UserDto)) return false;
        UserDto userDto = (UserDto) o;
        return email.equals(userDto.getEmail());
    }

    @Override
    public int hashCode() {
        return email.hashCode();
    }

    @Override
    public String toString() {
        return "UserDto{" +
                "id='" + super.getId() + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", sex=" + sex +
                ", birthday=" + birthday +
                '}';
    }
}
