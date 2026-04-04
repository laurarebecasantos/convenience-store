package com.api.rest.conveniencestore.user.repository;

import com.api.rest.conveniencestore.user.model.User;
import com.api.rest.conveniencestore.shared.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Optional;

public interface UserRepository extends JpaRepository <User, Long> {

    Collection<User> findByStatus(Status status);

    Page<User> findByStatus(Status status, Pageable pageable);

    Optional<UserDetails> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsById(Long id);

    boolean existsByEmail(String email);
}