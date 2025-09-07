package com.example.social_ute.dto.Role;

import java.util.Set;

public class RoleRequestDTO {
    private String name;
    private String description;
    private Set<String> permissionNames;

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
    public Set<String> getPermissionNames() {
        return permissionNames;
    }
    public void setPermissionNames(Set<String> permissionNames) {
        this.permissionNames = permissionNames;
    }
}
