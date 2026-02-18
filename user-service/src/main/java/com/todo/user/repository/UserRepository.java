package com.todo.user.repository;

import com.todo.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    boolean existsByEmail(String email);

    Optional<User> findUserByEmail(String email);

    void deleteByEmail(String email);


}