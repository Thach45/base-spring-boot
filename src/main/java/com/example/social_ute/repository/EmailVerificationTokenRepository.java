package com.example.social_ute.repository;

import com.example.social_ute.entity.EmailVerificationToken;
import com.example.social_ute.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, String> {
    Optional<EmailVerificationToken> findByToken(String token);
    
    @Query("SELECT t FROM EmailVerificationToken t WHERE t.token = :token AND t.expiresAt > :now")
    Optional<EmailVerificationToken> findValidToken(String token, LocalDateTime now);

    @Modifying
    @Transactional
    @Query("DELETE FROM EmailVerificationToken t WHERE t.expiresAt < :now")
    void deleteExpiredTokens(LocalDateTime now);
    
    @Modifying
    @Transactional
    void deleteByUser(User user);
}
