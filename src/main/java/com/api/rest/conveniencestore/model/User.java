package com.api.rest.conveniencestore.model;

import com.api.rest.conveniencestore.dto.UserDto;
import com.api.rest.conveniencestore.dto.UserUpdateDto;
import com.api.rest.conveniencestore.enums.Roles;
import com.api.rest.conveniencestore.enums.Status;
import com.api.rest.conveniencestore.exceptions.UserNotValidPassword;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collection;

@Table(name = "users")
@Entity(name = "User")
@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    @JsonIgnore
    @Column(unique = true)
    @NotBlank(message = "Password cannot be blank")
    private String password;

    @JsonIgnore
    @Column(unique = true)
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email must be valid")
    private String email;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Roles role;

    public User(UserDto data) {
        this.username = data.username();
        this.password = data.password();
        this.email = data.email();
        this.status = Status.ACTIVE;
        this.role = Roles.USER;
    }

    public void updateData(UserUpdateDto userUpdateDto, PasswordEncoder passwordEncoder) throws UserNotValidPassword {
        if (userUpdateDto.username() != null) {
            this.username = userUpdateDto.username();
        }
        if (userUpdateDto.password() != null && !userUpdateDto.password().isBlank()) {
            validatePassword(userUpdateDto.password());
            String encryptedPassword = passwordEncoder.encode(userUpdateDto.password());
            this.password = encryptedPassword;
        }
    }

    private void validatePassword(String password) throws UserNotValidPassword {
        if (password.length() < 8) {
            throw new UserNotValidPassword("The password must be at least 8 characters long.");
        }
        if (!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$")) {
            throw new UserNotValidPassword("The password must contain at least one uppercase letter, one lowercase letter, and one number.");
        }
    }

    public void setStatus(Status status) { //setter status
        this.status = status;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(Roles role) {
        this.role = role;
    }


    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}

