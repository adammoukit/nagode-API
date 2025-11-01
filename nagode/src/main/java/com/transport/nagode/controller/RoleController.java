package com.transport.nagode.controller;

import com.transport.nagode.models.Permission;
import com.transport.nagode.models.Role;
import com.transport.nagode.service.RoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/roles")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/create-role")
    public ResponseEntity<?> createRole(@RequestBody CreateRoleRequest request) {
        try {
            Role role = roleService.createRole(request.getName(), request.getPermissions());
            return ResponseEntity.ok(role);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", e.getMessage())
            );
        }
    }

    @PostMapping("/{roleName}/permissions")
    public ResponseEntity<?> addPermissionToRole(
            @PathVariable String roleName,
            @RequestBody AddPermissionRequest request) {
        try {
            Role role = roleService.addPermissionToRole(roleName, request.getPermissionName());
            return ResponseEntity.ok(role);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", e.getMessage())
            );
        }
    }

    @PutMapping("/{roleName}/permissions")
    public ResponseEntity<?> setRolePermissions(
            @PathVariable String roleName,
            @RequestBody Set<String> permissionNames) {
        try {
            Role role = roleService.setRolePermissions(roleName, permissionNames);
            return ResponseEntity.ok(role);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", e.getMessage())
            );
        }
    }

    @DeleteMapping("/{roleName}/permissions/{permissionName}")
    public ResponseEntity<?> removePermissionFromRole(
            @PathVariable String roleName,
            @PathVariable String permissionName) {
        try {
            Role role = roleService.removePermissionFromRole(roleName, permissionName);
            return ResponseEntity.ok(role);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", e.getMessage())
            );
        }
    }

    @GetMapping("/{roleName}/permissions")
    public ResponseEntity<?> getRolePermissions(@PathVariable String roleName) {
        try {
            Set<Permission> permissions = roleService.getRolePermissions(roleName);
            return ResponseEntity.ok(permissions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", e.getMessage())
            );
        }
    }

    // DTOs pour les requêtes
    public static class CreateRoleRequest {
        private String name;
        private Set<String> permissions;

        public CreateRoleRequest() {
        }

        public CreateRoleRequest(String name, Set<String> permissions) {
            this.name = name;
            this.permissions = permissions;
        }

        // Getters et setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Set<String> getPermissions() { return permissions; }
        public void setPermissions(Set<String> permissions) { this.permissions = permissions; }
    }

    public static class AddPermissionRequest {
        private String permissionName;

        // Getters et setters
        public String getPermissionName() { return permissionName; }
        public void setPermissionName(String permissionName) { this.permissionName = permissionName; }
    }
}
