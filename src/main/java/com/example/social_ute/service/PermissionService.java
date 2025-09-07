package com.example.social_ute.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.social_ute.dto.Permission.PermissionRequestDTO;
import com.example.social_ute.dto.Permission.PermissionResponseDTO;
import com.example.social_ute.entity.Permission;
import com.example.social_ute.repository.PermissionRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PermissionService {
    @Autowired
    private PermissionRepository permissionRepository;

    public List<PermissionResponseDTO> getAllPermissions() {
        return permissionRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public Optional<PermissionResponseDTO> getPermissionByName(String name) {
        return permissionRepository.findById((name))
                .map(this::toResponseDTO);
    }

    public PermissionResponseDTO createPermission(PermissionRequestDTO dto) {
        Permission permission = new Permission();
        permission.setName(dto.getName());
        permission.setDescription(dto.getDescription());
        Permission saved = permissionRepository.save(permission);
        return toResponseDTO(saved);
    }

    public PermissionResponseDTO updatePermission(String name, PermissionRequestDTO dto) {
        Permission permission = permissionRepository.findById((name))
                .orElseThrow(() -> new RuntimeException("Permission not found"));
        permission.setDescription(dto.getDescription());
        Permission updated = permissionRepository.save(permission);
        return toResponseDTO(updated);
    }

    public void deletePermission(String name) {
        permissionRepository.deleteById((name));
    }

    private PermissionResponseDTO toResponseDTO(Permission permission) {
        PermissionResponseDTO dto = new PermissionResponseDTO();
        dto.setName(permission.getName());
        dto.setDescription(permission.getDescription());
        return dto;
    }
}
