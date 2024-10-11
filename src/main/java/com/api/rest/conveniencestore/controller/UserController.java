package com.api.rest.conveniencestore.controller;

import com.api.rest.conveniencestore.dto.UserDto;
import com.api.rest.conveniencestore.dto.UserListingDto;
import com.api.rest.conveniencestore.enums.Roles;
import com.api.rest.conveniencestore.exceptions.*;
import com.api.rest.conveniencestore.model.User;
import com.api.rest.conveniencestore.service.UserService;
import com.api.rest.conveniencestore.dto.UserUpdateDto;
import com.api.rest.conveniencestore.enums.Status;
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

    @PostMapping
    @Transactional
    public ResponseEntity<User> register(@Valid @RequestBody UserDto userDto) throws UserRegistrationException, UserNotValidPassword, UserEmailNotFoundException{ //O spring se integra com o valid para aplicar as validações dos campos
        if (userDto.username().length() < 3 || userDto.username().isEmpty() ) {
            throw new UserRegistrationException("Username must be between 3 and 20 characters:");
        }

        if (userService.existsByUsername(userDto.username())) {
            throw new UserRegistrationException("User already registered with the name: " + userDto.username());
        }

        if (userService.existsByEmail(userDto.email())) {
            throw new UserEmailNotFoundException("Email already registered." + userDto.email());
        }

        validatePassword(userDto.password());

        User savedUser = userService.registerUser(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @GetMapping
    public ResponseEntity<List<UserListingDto>> list() throws UserListingNullException {
        var returnList = userService.listUsers();
        if (returnList.isEmpty()) {
            throw new UserListingNullException("No registered users were found.");
        }
        return ResponseEntity.ok(returnList);
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<User> update( @PathVariable Long id, @Valid @RequestBody UserUpdateDto updateDto, UserDto userDto) throws UserNotFoundException, UserNotValidPassword {
        User updateUser = userService.updateUser(id, updateDto);
        return ResponseEntity.ok(updateUser);
    }


    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> delete(@PathVariable Long id) throws UserNotFoundException {
        if (!userService.existsById(id)) {
            throw new UserNotFoundException("User with id " + id + " not found");
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
            throw new UserInvalidStatusException("Invalid status: " + statusString);
        }

        if (!Status.INACTIVE.equals(statusInactive)) {
            throw new UserInvalidStatusException("The status can only be changed to INACTIVE.");
        }

        if (!userService.existsById(id)) {
            throw new UserNotFoundException("User with ID: " + id + " not found.");
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
            throw new UserInvalidRolesException("Invalid role: " + rolesString);
        }

        if (!Roles.ADMIN.equals(rolesAdmin)) {
            throw new UserInvalidRolesException("The role can only be changed to ADMIN.");
        }

        if (!userService.existsById(id)) {
            throw new UserNotFoundException("User with ID: " + id + " not found.");
        }

        User updatedRoleAdmin = userService.roleUserAdmin(id, rolesAdmin);
        return ResponseEntity.ok(updatedRoleAdmin);
    }

    private void validatePassword(String password) throws UserNotValidPassword {
        if (password.length() < 8) {
            throw new UserNotValidPassword("The password must be at least 8 characters long.");
        }
        if (!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$")) {
            throw new UserNotValidPassword("The password must contain at least one uppercase letter, one lowercase letter, and one number.");
        }
    }

}
