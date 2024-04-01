package com.blackshoe.moongklheremobileapi.repository;

import com.blackshoe.moongklheremobileapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(final String email);
    boolean existsByEmail(final String email);
    Optional<User> findById(final UUID id);

    boolean existsByNickname(final String nickname);

    boolean existsByPhoneNumber(final String phoneNumber);
    Optional<User> findByEmailAndPassword(final String email, final String password);
}
