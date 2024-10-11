package com.api.rest.conveniencestore.service;

import com.api.rest.conveniencestore.dto.UserDto;
import com.api.rest.conveniencestore.dto.UserListingDto;
import com.api.rest.conveniencestore.dto.UserUpdateDto;
import com.api.rest.conveniencestore.enums.Roles;
import com.api.rest.conveniencestore.enums.Status;
import com.api.rest.conveniencestore.exceptions.UserNotFoundException;
import com.api.rest.conveniencestore.exceptions.UserNotValidPassword;
import com.api.rest.conveniencestore.model.User;
import com.api.rest.conveniencestore.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);

    @Transactional
    public User registerUser(UserDto userDto) {
        String encryptedPassword = passwordEncoder.encode(userDto.password());
        log.debug("Encrypted password: " + encryptedPassword);
        User user = new User(userDto);
        user.setPassword(encryptedPassword);
        return userRepository.save(user);
    }

    public List<UserListingDto> listUsers() {
        return userRepository.findByStatus(Status.ACTIVE)
                .stream()
                .map(UserListingDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public User updateUser(Long id, UserUpdateDto userUpdateDto) throws UserNotFoundException, UserNotValidPassword {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
        user.updateData(userUpdateDto, passwordEncoder);
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.getReferenceById(id);
        userRepository.delete(user);
    }

    @Transactional
    public User statusUserInactive(Long id, Status status) {
        User user = userRepository.getReferenceById(id);
        if (status != null) {
            user.setStatus(status.INACTIVE);
        }
        return userRepository.save(user);
    }

    @Transactional
    public User roleUserAdmin(Long id, Roles roles) {
        User user = userRepository.getReferenceById(id);
        if (roles != null) {
            user.setRole(roles.ADMIN);
        }
        return userRepository.save(user);
    }
}

