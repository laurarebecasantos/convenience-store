package com.api.rest.conveniencestore.repository;

import com.api.rest.conveniencestore.model.User;
import com.api.rest.conveniencestore.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Optional;

public interface UserRepository extends JpaRepository <User, Long> {

    Collection<User> findByStatus(Status status);

    Optional<UserDetails> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsById(Long id);

    boolean existsByEmail(String email);
}