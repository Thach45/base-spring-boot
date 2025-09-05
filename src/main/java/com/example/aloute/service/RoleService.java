package com.example.aloute.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.aloute.dto.Permission.PermissionResponseDTO;
import com.example.aloute.dto.Role.RoleRequestDTO;
import com.example.aloute.dto.Role.RoleResponseDTO;
import com.example.aloute.entity.Permission;
import com.example.aloute.entity.Role;
import com.example.aloute.repository.PermissionRepository;
import com.example.aloute.repository.RoleRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    public List<RoleResponseDTO> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public Optional<RoleResponseDTO> getRoleByName(String name) {
        return roleRepository.findById(name)
                .map(this::toResponseDTO);
    }

    public RoleResponseDTO createRole(RoleRequestDTO dto) {
        Role role = new Role();
        role.setName(dto.getName());
        role.setDescription(dto.getDescription());
        if (dto.getPermissionNames() != null) {
            Set<Permission> permissions = dto.getPermissionNames().stream()
                .map(name -> permissionRepository.findById(name).orElse(null))
                .filter(p -> p != null)
                .collect(Collectors.toSet());
            role.setPermissions(permissions);
        }
        Role saved = roleRepository.save(role);
        return toResponseDTO(saved);
    }

    public RoleResponseDTO updateRole(String name, RoleRequestDTO dto) {
        Role role = roleRepository.findById(name)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        role.setDescription(dto.getDescription());
        if (dto.getPermissionNames() != null) {
            Set<Permission> permissions = dto.getPermissionNames().stream()
                .map(n -> permissionRepository.findById(n).orElse(null))
                .filter(p -> p != null)
                .collect(Collectors.toSet());
            role.setPermissions(permissions);
        }
        Role updated = roleRepository.save(role);
        return toResponseDTO(updated);
    }

    public void deleteRole(String name) {
        Role role = roleRepository.findById(name)
            .orElseThrow(() -> new RuntimeException("Role not found"));
        // Xóa liên kết với Permission trước khi xóa Role
        role.getPermissions().clear();
        roleRepository.save(role);
        roleRepository.deleteById(name);
    }

    private RoleResponseDTO toResponseDTO(Role role) {
        RoleResponseDTO dto = new RoleResponseDTO();
        dto.setName(role.getName());
        dto.setDescription(role.getDescription());
        if (role.getPermissions() != null) {
            Set<PermissionResponseDTO> permissionDTOs = role.getPermissions().stream()
                .map(p -> {
                    PermissionResponseDTO pdto = new PermissionResponseDTO();
                    pdto.setName(p.getName());
                    pdto.setDescription(p.getDescription());
                    return pdto;
                })
                .collect(Collectors.toSet());
            dto.setPermissions(permissionDTOs);
        }
        return dto;
    }
}
