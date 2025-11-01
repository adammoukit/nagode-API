package com.transport.nagode.initializer;

import com.transport.nagode.service.CityService;
import com.transport.nagode.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1) // Exécuter en premier
public class DataInitializer implements CommandLineRunner {
    private final RoleService roleService;
    private final CityService cityService;

    public DataInitializer(RoleService roleService, CityService cityService) {
        this.roleService = roleService;
        this.cityService = cityService;
    }

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Override
    public void run(String... args) throws Exception {
        logger.info("Début de l'initialisation des données...");

        try {
            // 1. Initialiser les villes
            logger.info("Initialisation des villes...");
            cityService.initializeDefaultCities();

            // 2. Initialiser les rôles et permissions
            logger.info("Initialisation des rôles et permissions...");
            roleService.initializeDefaultRolesAndPermissions();

            logger.info("Initialisation des données terminée avec succès!");

        } catch (Exception e) {
            logger.error("Erreur lors de l'initialisation des données: {}", e.getMessage(), e);
        }
    }

}
