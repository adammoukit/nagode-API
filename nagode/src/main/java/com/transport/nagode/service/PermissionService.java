package com.transport.nagode.service;

import com.transport.nagode.exceptions.DuplicatePermissionException;
import com.transport.nagode.exceptions.PermissionNotFoundException;
import com.transport.nagode.models.Permission;
import com.transport.nagode.repository.PermissionRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PermissionService {
    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public Permission createPermission(String name, String description) {
        // Vérifier si la permission existe déjà
        Optional<Permission> existingPermission = permissionRepository.findByName(name);

        if (existingPermission.isPresent()) {
            throw new DuplicatePermissionException("La permission existe déjà: " + name);
        }

        // Créer la nouvelle permission
        Permission permission = new Permission();
        permission.setName(name);
        permission.setDescription(description);

        return permissionRepository.save(permission);
    }

    public Permission findPermissionByName(String permissionName) {
        return permissionRepository.findByName(permissionName)
                .orElseThrow(() -> new PermissionNotFoundException("Permission non trouvée: " + permissionName));
    }


    public Permission findOrCreatePermission(String name, String description) {
        return permissionRepository.findByName(name)
                .orElseGet(() -> createPermission(name, description));
    }
}
