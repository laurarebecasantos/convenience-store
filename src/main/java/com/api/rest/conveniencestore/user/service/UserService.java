package com.api.rest.conveniencestore.user.service;

import com.api.rest.conveniencestore.user.dto.UserDto;
import com.api.rest.conveniencestore.user.dto.UserListingDto;
import com.api.rest.conveniencestore.user.dto.UserUpdateDto;
import com.api.rest.conveniencestore.shared.enums.Roles;
import com.api.rest.conveniencestore.shared.enums.Status;
import com.api.rest.conveniencestore.shared.exception.UserNotFoundException;
import com.api.rest.conveniencestore.shared.exception.PasswordValidateException;
import com.api.rest.conveniencestore.shared.exception.UsernameValidateException;
import com.api.rest.conveniencestore.user.model.User;
import com.api.rest.conveniencestore.user.repository.UserRepository;
import com.api.rest.conveniencestore.shared.utils.MessageConstants;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


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

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Transactional
    public User registerUser(UserDto userDto) {
        String encryptedPassword = passwordEncoder.encode(userDto.password());
        log.debug("Senha criptografada: " + encryptedPassword);
        User user = new User(userDto);
        user.setPassword(encryptedPassword);
        return userRepository.save(user);
    }

    public Page<UserListingDto> listUsers(Pageable pageable) {
        return userRepository.findByStatus(Status.ACTIVE, pageable)
                .map(UserListingDto::new);
    }

    @Transactional
    public User updateUser(Long id, UserUpdateDto userUpdateDto) throws UserNotFoundException, PasswordValidateException, UsernameValidateException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(String.format(MessageConstants.USER_NOT_FOUND, id)));
        user.updateData(userUpdateDto, passwordEncoder);
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(String.format(MessageConstants.USER_NOT_FOUND, id)));
        userRepository.delete(user);
    }

    @Transactional
    public User statusUserInactive(Long id, Status status) {
        return updateUserStatus(id, status);
    }

    @Transactional
    public User updateUserStatus(Long id, Status status) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(String.format(MessageConstants.USER_NOT_FOUND, id)));
        user.setStatus(status);
        return userRepository.save(user);
    }

    @Transactional
    public User roleUserAdmin(Long id, Roles roles) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(String.format(MessageConstants.USER_NOT_FOUND, id)));
        if (roles != null) {
            user.setRole(Roles.ADMIN);
        }
        return userRepository.save(user);
    }
}