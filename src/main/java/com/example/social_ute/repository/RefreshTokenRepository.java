package com.example.social_ute.repository;

import com.example.social_ute.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    
    Optional<RefreshToken> findByToken(String token);
    
    @Query("SELECT CASE WHEN COUNT(rt) > 0 THEN true ELSE false END FROM RefreshToken rt WHERE rt.token = :token AND rt.revoked = false AND rt.expiresAt > :now")
    boolean existsByTokenAndNotRevokedAndNotExpired(@Param("token") String token, @Param("now") LocalDateTime now);
    
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiresAt < :now")
    @Modifying
    @Transactional
    void deleteExpiredTokens(@Param("now") LocalDateTime now);
    
    @Query("DELETE FROM RefreshToken rt WHERE rt.email = :email")
    @Modifying
    @Transactional
    void deleteByEmail(@Param("email") String email);
    
    @Query("DELETE FROM RefreshToken rt WHERE rt.token = :token")
    @Modifying
    @Transactional
    void deleteToken(@Param("token") String token);
}