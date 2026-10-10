package com.example.usermvc.repository;

import com.example.usermvc.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Talks to the database (through Hibernate). You write no SQL.
 *
 * PROVIDED CODE - students do not need to change it.
 *
 * Methods you get for free from JpaRepository:
 *
 *   User save(User user)              insert a new user, or update an existing one
 *   List<User> findAll()              all users
 *   Optional<User> findById(Long id)  one user (Optional.empty() if not found)
 *   boolean existsById(Long id)       does this user exist?
 *   void deleteById(Long id)          delete one user
 */
public interface UserRepository extends JpaRepository<User, Long> {
}
