package com.example.social_ute.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.social_ute.entity.User;
import com.example.social_ute.enums.UserStatus;

import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    
    @Query("SELECT u FROM User u WHERE u.status = :status AND u.createdAt < :cutoffTime")
    List<User> findByStatusAndCreatedAtBefore(@Param("status") UserStatus status, @Param("cutoffTime") LocalDateTime cutoffTime);

    @Modifying
    @Transactional
    @Query("DELETE FROM User u WHERE u.status = :status AND u.createdAt < :cutoffTime")
    void deleteByStatusAndCreatedAtBefore(@Param("status") UserStatus status, @Param("cutoffTime") LocalDateTime cutoffTime);

    @Query("SELECT u FROM User u WHERE u.isEmailVerified = false AND u.createdAt < :cutoffTime")
    List<User> findByUnverifiedEmailAndCreatedAtBefore(@Param("cutoffTime") LocalDateTime cutoffTime);

    @Modifying
    @Transactional
    @Query("DELETE FROM User u WHERE u.isEmailVerified = false AND u.createdAt < :cutoffTime")
    void deleteByUnverifiedEmailAndCreatedAtBefore(@Param("cutoffTime") LocalDateTime cutoffTime);
}