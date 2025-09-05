package com.example.aloute.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.aloute.entity.BlacklistedToken;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken, Long> {
    
    Optional<BlacklistedToken> findByToken(String token);
    
    @Query("SELECT CASE WHEN COUNT(bt) > 0 THEN true ELSE false END FROM BlacklistedToken bt WHERE bt.token = :token")
    boolean existsByToken(@Param("token") String token);
    
    @Query("DELETE FROM BlacklistedToken bt WHERE bt.expiresAt < :now")
    void deleteExpiredTokens(@Param("now") LocalDateTime now);
}
