package com.api.rest.conveniencestore.controller;

import com.api.rest.conveniencestore.dto.UserDto;
import com.api.rest.conveniencestore.dto.UserListingDto;
import com.api.rest.conveniencestore.enums.Roles;
import com.api.rest.conveniencestore.exceptions.*;
import com.api.rest.conveniencestore.model.User;
import com.api.rest.conveniencestore.service.UserService;
import com.api.rest.conveniencestore.dto.UserUpdateDto;
import com.api.rest.conveniencestore.enums.Status;
import com.api.rest.conveniencestore.utils.MessageConstants;
import com.api.rest.conveniencestore.validations.PasswordValidator;
import com.api.rest.conveniencestore.validations.UserValidator;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PasswordValidator passwordValidator;

    @Autowired
    private UserValidator userValidator;

    @PostMapping
    @Transactional
    public ResponseEntity<User> register(@Valid @RequestBody UserDto userDto) throws PasswordValidateException, UserEmailNotFoundException, UsernameValidateException {
        userValidator.validateUsername(userDto.username());

        if (userService.existsByEmail(userDto.email())) {
            throw new UserEmailNotFoundException(MessageConstants.EMAIL_ALREADY_REGISTERED + userDto.email());
        }
        passwordValidator.validatePassword(userDto.password());

        User savedUser = userService.registerUser(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @GetMapping
    public ResponseEntity<List<UserListingDto>> list() throws UserListingNullException {
        var returnList = userService.listUsers();
        if (returnList.isEmpty()) {
            throw new UserListingNullException(MessageConstants.NO_USERS_FOUND);
        }
        return ResponseEntity.ok(returnList);
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<User> update( @PathVariable Long id, @Valid @RequestBody UserUpdateDto userUpdateDto) throws UserNotFoundException, PasswordValidateException, UsernameValidateException {
        User updateUser = userService.updateUser(id, userUpdateDto);
        return ResponseEntity.ok(updateUser);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> delete(@PathVariable Long id) throws UserNotFoundException {
        if (!userService.existsById(id)) {
            throw new UserNotFoundException(String.format(MessageConstants.USER_NOT_FOUND, id));
        }
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    @Transactional
    public ResponseEntity<User> status( @PathVariable Long id, @Valid @RequestBody Map<String, String> statusRequest) throws UserInvalidStatusException, UserNotFoundException{
        String statusString = statusRequest.get("status");
        Status statusInactive;
        try {
            statusInactive = Status.fromValueStatus(statusString);
        } catch (IllegalArgumentException e) {
            throw new UserInvalidStatusException(MessageConstants.INVALID_STATUS + statusString);
        }

        if (!Status.INACTIVE.equals(statusInactive)) {
            throw new UserInvalidStatusException(MessageConstants.STATUS_INACTIVE);
        }

        if (!userService.existsById(id)) {
            throw new UserNotFoundException(String.format(MessageConstants.USER_NOT_FOUND, id));
        }

        User updatedStatusUser = userService.statusUserInactive(id, statusInactive);
        return ResponseEntity.ok(updatedStatusUser);
    }

    @PatchMapping("/{id}/roles")
    @Transactional
    public ResponseEntity<User> roles( @PathVariable Long id, @Valid @RequestBody Map<String, String> rolesRequest) throws UserInvalidRolesException, UserNotFoundException{
        String rolesString = rolesRequest.get("roles");
        Roles rolesAdmin;
        try {
            rolesAdmin= Roles.fromValueRoles(rolesString);
        } catch (IllegalArgumentException e) {
            throw new UserInvalidRolesException(MessageConstants.INVALID_ROLE + rolesString);
        }

        if (!Roles.ADMIN.equals(rolesAdmin)) {
            throw new UserInvalidRolesException(MessageConstants.ROLE_ADMIN);
        }

        if (!userService.existsById(id)) {
            throw new UserNotFoundException(String.format(MessageConstants.USER_NOT_FOUND, id));
        }

        User updatedRoleAdmin = userService.roleUserAdmin(id, rolesAdmin);
        return ResponseEntity.ok(updatedRoleAdmin);
    }
}