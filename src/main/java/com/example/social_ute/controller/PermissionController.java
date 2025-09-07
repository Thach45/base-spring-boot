package com.example.social_ute.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.social_ute.dto.Permission.PermissionRequestDTO;
import com.example.social_ute.dto.Permission.PermissionResponseDTO;
import com.example.social_ute.dto.User.ApiResponse;
import com.example.social_ute.service.PermissionService;

import java.util.List;

@RestController
@RequestMapping("/permissions")
public class PermissionController {
    @Autowired
    private PermissionService permissionService;

    @GetMapping
    public ApiResponse<List<PermissionResponseDTO>> getAllPermissions() {
        ApiResponse<List<PermissionResponseDTO>> response = new ApiResponse<>();
        response.setData(permissionService.getAllPermissions());
        return response;
    }

    @GetMapping("/{name}")
    public ApiResponse<PermissionResponseDTO> getPermissionByName(@PathVariable String name) {
        ApiResponse<PermissionResponseDTO> response = new ApiResponse<>();
        response.setData(permissionService.getPermissionByName(name).orElse(null));
        return response;
    }

    @PostMapping
    public ApiResponse<PermissionResponseDTO> createPermission(@RequestBody PermissionRequestDTO dto) {
        ApiResponse<PermissionResponseDTO> response = new ApiResponse<>();
        response.setData(permissionService.createPermission(dto));
        return response;
    }

    @PutMapping("/{name}")
    public ApiResponse<PermissionResponseDTO> updatePermission(@PathVariable String name, @RequestBody PermissionRequestDTO dto) {
        ApiResponse<PermissionResponseDTO> response = new ApiResponse<>();
        try {
            response.setData(permissionService.updatePermission(name, dto));
        } catch (RuntimeException e) {
            response.setData(null);
            response.setMessage("Permission not found");
        }
        return response;
    }

    @DeleteMapping("/{name}")
    public ApiResponse<Void> deletePermission(@PathVariable String name) {
        ApiResponse<Void> response = new ApiResponse<>();
        permissionService.deletePermission(name);
        response.setMessage("Deleted successfully");
        return response;
    }
}
