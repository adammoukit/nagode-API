package com.transport.nagode.service;

import com.transport.nagode.exceptions.DuplicatePermissionException;
import com.transport.nagode.exceptions.DuplicateRoleException;
import com.transport.nagode.exceptions.PermissionNotFoundException;
import com.transport.nagode.models.Permission;
import com.transport.nagode.models.Role;
import com.transport.nagode.repository.PermissionRepository;
import com.transport.nagode.repository.RoleRepository;
import org.springframework.stereotype.Service;

import javax.management.relation.RoleNotFoundException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionService permissionService;
    private final PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository, PermissionService permissionService, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionService = permissionService;
        this.permissionRepository = permissionRepository;
    }

//    public Role createRole(String name, String description) {
//        return createRole(name, description, new HashSet<>());
//    }

    public Role createRole(String name, Set<String> permissionNames) {
        // Vérifier si le rôle existe déjà
        Optional<Role> existingRole = roleRepository.findByName(name);

        if (existingRole.isPresent()) {
            throw new DuplicateRoleException("Le rôle existe déjà: " + name);
        }

        // Créer le nouveau rôle
        Role role = new Role();
        role.setName(name);

        // Assigner les permissions si fournies
        if (permissionNames != null && !permissionNames.isEmpty()) {
            Set<Permission> permissions = resolvePermissions(permissionNames);
            role.setPermissions(permissions);
        }

        return roleRepository.save(role);
    }

    public Role findRoleByName(String roleName) throws RoleNotFoundException {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new RoleNotFoundException("Rôle non trouvé: " + roleName));
    }

    /**
     * Méthode pour assigner une permission à un rôle
     */
    public Role addPermissionToRole(String roleName, String permissionName) throws RoleNotFoundException {
        Role role = findRoleByName(roleName);
        Permission permission = permissionService.findPermissionByName(permissionName);

        // Vérifier si la permission est déjà assignée
        if (role.getPermissions().contains(permission)) {
            throw new DuplicatePermissionException(
                    "La permission '" + permissionName + "' est déjà assignée au rôle '" + roleName + "'"
            );
        }

        // Ajouter la permission au rôle
        role.getPermissions().add(permission);

        return roleRepository.save(role);
    }

    /**
     * Méthode pour assigner plusieurs permissions à un rôle
     */
    public Role addPermissionsToRole(String roleName, Set<String> permissionNames) throws RoleNotFoundException {
        Role role = findRoleByName(roleName);
        Set<Permission> permissions = resolvePermissions(permissionNames);

        // Ajouter toutes les nouvelles permissions
        role.getPermissions().addAll(permissions);

        return roleRepository.save(role);
    }

    /**
     * Méthode pour retirer une permission d'un rôle
     */
    public Role removePermissionFromRole(String roleName, String permissionName) throws RoleNotFoundException {
        Role role = findRoleByName(roleName);
        Permission permission = permissionService.findPermissionByName(permissionName);

        // Retirer la permission du rôle
        boolean removed = role.getPermissions().remove(permission);

        if (!removed) {
            throw new PermissionNotFoundException(
                    "La permission '" + permissionName + "' n'est pas assignée au rôle '" + roleName + "'"
            );
        }

        return roleRepository.save(role);
    }

    /**
     * Méthode pour remplacer toutes les permissions d'un rôle
     */
    public Role setRolePermissions(String roleName, Set<String> permissionNames) throws RoleNotFoundException {
        Role role = findRoleByName(roleName);
        Set<Permission> permissions = resolvePermissions(permissionNames);

        // Remplacer toutes les permissions
        role.setPermissions(permissions);

        return roleRepository.save(role);
    }

    /**
     * Méthode pour récupérer toutes les permissions d'un rôle
     */
    public Set<Permission> getRolePermissions(String roleName) throws RoleNotFoundException {
        Role role = findRoleByName(roleName);
        return role.getPermissions();
    }

    /**
     * Méthode utilitaire pour résoudre les noms de permissions en objets Permission
     */
    private Set<Permission> resolvePermissions(Set<String> permissionNames) {
        Set<Permission> permissions = new HashSet<>();

        for (String permissionName : permissionNames) {
            Permission permission = permissionService.findPermissionByName(permissionName);
            permissions.add(permission);
        }

        return permissions;
    }

    /**
     * Méthode pour vérifier si un rôle a une permission spécifique
     */
    public boolean hasPermission(String roleName, String permissionName) throws RoleNotFoundException {
        Role role = findRoleByName(roleName);
        return role.getPermissions().stream()
                .anyMatch(permission -> permission.getName().equals(permissionName));
    }

    /**
     * Méthode pour créer les rôles par défaut du système
     */
    public void initializeDefaultRolesAndPermissions() {
        // Créer les permissions de base
        createDefaultPermissions();

        // Créer les rôles par défaut avec leurs permissions
        createDefaultRoles();
    }

    private void createDefaultPermissions() {

        // --- BOOKING (réservations) ---
        createPermissionIfNotExists("BOOKING_CREATE", "Créer une réservation");
        createPermissionIfNotExists("BOOKING_VIEW", "Voir les réservations");
        createPermissionIfNotExists("BOOKING_UPDATE", "Modifier une réservation");
        createPermissionIfNotExists("BOOKING_CANCEL", "Annuler une réservation");
        createPermissionIfNotExists("BOOKING_DELETE", "Supprimer une réservation");

        // --- TRIP (trajets) ---
        createPermissionIfNotExists("TRIP_CREATE", "Créer un trajet");
        createPermissionIfNotExists("TRIP_VIEW", "Voir les trajets");
        createPermissionIfNotExists("TRIP_UPDATE", "Mettre à jour un trajet");
        createPermissionIfNotExists("TRIP_UPDATE_STATUS", "Mettre à jour le statut du trajet");
        createPermissionIfNotExists("TRIP_DELETE", "Supprimer un trajet");
        createPermissionIfNotExists("TRIP_MANAGE", "Gérer les trajets");

        // --- VEHICLE (véhicules) ---
        createPermissionIfNotExists("VEHICLE_CREATE", "Créer un véhicule");
        createPermissionIfNotExists("VEHICLE_VIEW", "Voir les véhicules");
        createPermissionIfNotExists("VEHICLE_UPDATE", "Mettre à jour un véhicule");
        createPermissionIfNotExists("VEHICLE_DELETE", "Supprimer un véhicule");

        // --- USER (utilisateurs) ---
        createPermissionIfNotExists("USER_CREATE", "Créer un utilisateur");
        createPermissionIfNotExists("USER_VIEW", "Voir les utilisateurs");
        createPermissionIfNotExists("USER_UPDATE", "Mettre à jour un utilisateur");
        createPermissionIfNotExists("USER_DELETE", "Supprimer un utilisateur");
        createPermissionIfNotExists("USER_MANAGE", "Gérer les utilisateurs");

        // --- ROLE / PERMISSION ---
        createPermissionIfNotExists("ROLE_MANAGE", "Gérer les rôles");
        createPermissionIfNotExists("PERMISSION_MANAGE", "Gérer les permissions");

        // --- PAYMENT (paiements) ---
        createPermissionIfNotExists("PAYMENT_CREATE", "Créer un paiement");
        createPermissionIfNotExists("PAYMENT_VIEW", "Voir les paiements");
        createPermissionIfNotExists("PAYMENT_VALIDATE", "Valider un paiement");
        createPermissionIfNotExists("PAYMENT_REFUND", "Rembourser un paiement");

        // --- PROFILE (profil utilisateur) ---
        createPermissionIfNotExists("PROFILE_VIEW", "Voir son profil");
        createPermissionIfNotExists("PROFILE_UPDATE", "Mettre à jour son profil");

        // --- NOTIFICATIONS ---
        createPermissionIfNotExists("NOTIFICATION_VIEW", "Voir les notifications");
        createPermissionIfNotExists("NOTIFICATION_MANAGE", "Gérer les notifications");

        // --- SYSTEM & DASHBOARD ---
        createPermissionIfNotExists("SYSTEM_CONFIG", "Configurer le système");
        createPermissionIfNotExists("DASHBOARD_VIEW", "Voir le tableau de bord");
        createPermissionIfNotExists("LOG_VIEW", "Voir les journaux système");
    }


    private void createPermissionIfNotExists(String name, String description) {
        if (permissionRepository.findByName(name).isEmpty()) {
            permissionService.createPermission(name, description);
        }
    }

    private void createDefaultRoles() {
        // Rôle PASSENGER
        // PASSENGER
        if (roleRepository.findByName("ROLE_PASSENGER").isEmpty()) {
            Set<String> passengerPermissions = Set.of(
                    "BOOKING_CREATE", "BOOKING_VIEW", "BOOKING_CANCEL",
                    "PAYMENT_CREATE", "PAYMENT_VIEW", "PROFILE_VIEW", "PROFILE_UPDATE", "NOTIFICATION_VIEW"
            );
            createRole("ROLE_PASSENGER", passengerPermissions);
        }

        // DRIVER
        if (roleRepository.findByName("ROLE_DRIVER").isEmpty()) {
            Set<String> driverPermissions = Set.of(
                    "TRIP_VIEW", "TRIP_UPDATE_STATUS", "VEHICLE_VIEW",
                    "VEHICLE_UPDATE", "PAYMENT_VIEW", "NOTIFICATION_VIEW"
            );
            createRole("ROLE_DRIVER",  driverPermissions);
        }

        // ADMIN
        if (roleRepository.findByName("ROLE_ADMIN").isEmpty()) {
            Set<String> adminPermissions = Set.of(
                    "BOOKING_CREATE", "BOOKING_VIEW", "BOOKING_UPDATE", "BOOKING_CANCEL", "BOOKING_DELETE",
                    "TRIP_CREATE", "TRIP_VIEW", "TRIP_UPDATE", "TRIP_DELETE",
                    "VEHICLE_CREATE", "VEHICLE_VIEW", "VEHICLE_UPDATE", "VEHICLE_DELETE",
                    "USER_CREATE", "USER_VIEW", "USER_UPDATE", "USER_DELETE",
                    "ROLE_MANAGE", "PERMISSION_MANAGE",
                    "PAYMENT_VIEW", "PAYMENT_VALIDATE", "PAYMENT_REFUND",
                    "SYSTEM_CONFIG", "DASHBOARD_VIEW", "NOTIFICATION_MANAGE", "LOG_VIEW"
            );
            createRole("ROLE_ADMIN",  adminPermissions);
        }

    }
}
