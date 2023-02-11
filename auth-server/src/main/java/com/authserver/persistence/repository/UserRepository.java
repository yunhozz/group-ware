package com.authserver.persistence.repository;

import com.authserver.persistence.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserCustomRepository {

    Optional<User> findByUserId(String userId);
    Optional<User> findByEmail(String email);
    boolean existsUserByEmail(String email);
}