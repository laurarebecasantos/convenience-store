package com.api.rest.conveniencestore.model;

import com.api.rest.conveniencestore.dto.UserDto;
import com.api.rest.conveniencestore.dto.UserUpdateDto;
import com.api.rest.conveniencestore.enums.Roles;
import com.api.rest.conveniencestore.enums.Status;
import com.api.rest.conveniencestore.exceptions.PasswordValidateException;
import com.api.rest.conveniencestore.exceptions.UsernameValidateException;
import com.api.rest.conveniencestore.validations.PasswordValidator;
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
import java.util.Collections;

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

    public void updateData(UserUpdateDto userUpdateDto, PasswordEncoder passwordEncoder) throws PasswordValidateException, UsernameValidateException {
        if (userUpdateDto.username() != null) {
            this.username = userUpdateDto.username();
        }
        PasswordValidator.validatePassword(userUpdateDto.password());
        String encryptedPassword = passwordEncoder.encode(userUpdateDto.password());
        this.password = encryptedPassword;
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
        return Collections.singletonList(() -> "ROLE: " + this.role);
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

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }
}