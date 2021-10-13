package com.example.socialNetwork.demo.repository;

import com.example.socialNetwork.demo.entity.UserA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserA, Long> {

    Optional<UserA> findUserAByUsername(String username);

    Optional<UserA> findUserAByEmail(String email);

    Optional<UserA> findUserAById (Long id);
}
