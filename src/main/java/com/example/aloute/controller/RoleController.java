package com.example.aloute.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.aloute.dto.Role.RoleRequestDTO;
import com.example.aloute.dto.Role.RoleResponseDTO;
import com.example.aloute.dto.User.ApiResponse;
import com.example.aloute.service.RoleService;

import java.util.List;

@RestController
@RequestMapping("/role")
public class RoleController {
    @Autowired
    private RoleService roleService;

    @GetMapping
    public ApiResponse<List<RoleResponseDTO>> getAllRoles() {
        ApiResponse<List<RoleResponseDTO>> response = new ApiResponse<>();
        response.setData(roleService.getAllRoles());
        return response;
    }

    @GetMapping("/{name}")
    public ApiResponse<RoleResponseDTO> getRoleByName(@PathVariable String name) {
        ApiResponse<RoleResponseDTO> response = new ApiResponse<>();
        response.setData(roleService.getRoleByName(name).orElse(null));
        return response;
    }

    @PostMapping
    public ApiResponse<RoleResponseDTO> createRole(@RequestBody RoleRequestDTO dto) {
        ApiResponse<RoleResponseDTO> response = new ApiResponse<>();
        response.setData(roleService.createRole(dto));
        return response;
    }

    @PutMapping("/{name}")
    public ApiResponse<RoleResponseDTO> updateRole(@PathVariable String name, @RequestBody RoleRequestDTO dto) {
        ApiResponse<RoleResponseDTO> response = new ApiResponse<>();
        try {
            response.setData(roleService.updateRole(name, dto));
        } catch (RuntimeException e) {
            response.setData(null);
            response.setMessage("Role not found");
        }
        return response;
    }

    @DeleteMapping("/{name}")
    public ApiResponse<Void> deleteRole(@PathVariable String name) {
        ApiResponse<Void> response = new ApiResponse<>();
        roleService.deleteRole(name);
        response.setMessage("Deleted successfully");
        return response;
    }
}

