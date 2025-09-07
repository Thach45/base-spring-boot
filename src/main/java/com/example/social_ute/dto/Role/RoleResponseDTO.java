package com.example.social_ute.dto.Role;

import java.util.Set;

import com.example.social_ute.dto.Permission.PermissionResponseDTO;

public class RoleResponseDTO {
    private String name;
    private String description;
    private Set<PermissionResponseDTO> permissions;

    // Getters and setters
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Set<PermissionResponseDTO> getPermissions() {
        return permissions;
    }
    public void setPermissions(Set<PermissionResponseDTO> permissions) {
        this.permissions = permissions;
    }
}
