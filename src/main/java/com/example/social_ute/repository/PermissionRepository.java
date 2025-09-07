package com.example.social_ute.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.social_ute.entity.Permission;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, String> {
    // Additional query methods if needed
}
