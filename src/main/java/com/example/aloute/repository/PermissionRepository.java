package com.example.aloute.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.aloute.entity.Permission;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, String> {
    // Additional query methods if needed
}
