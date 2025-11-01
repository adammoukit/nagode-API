package com.transport.nagode.service;

import com.transport.nagode.exceptions.CityNotFoundException;
import com.transport.nagode.exceptions.DuplicateCityException;
import com.transport.nagode.models.City;
import com.transport.nagode.repository.CityRepository;
import com.transport.nagode.requestDTO.CreateCityDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
public class CityService {
    private final CityRepository cityRepository;

    public CityService(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    private static final Logger logger = LoggerFactory.getLogger(CityService.class);

    public City createCity(CreateCityDTO createCityDTO) {
        // Vérifier si la ville existe déjà
        Optional<City> existingCity = cityRepository.findByName(createCityDTO.getName());

        if (existingCity.isPresent()) {
            throw new DuplicateCityException("Cette ville existe déjà dans la base de données: " + createCityDTO.getName());
        }

        // Créer la nouvelle ville
        City newCity = new City(createCityDTO.getName(), createCityDTO.getRegion(), createCityDTO.getCountry());
        // Optionnel: définir les coordonnées GPS si fournies
        if (createCityDTO.getLatitude() != null && createCityDTO.getLongitude() != null) {
            newCity.setLatitude(createCityDTO.getLatitude());
            newCity.setLongitude(createCityDTO.getLongitude());
        }
        return cityRepository.save(newCity);
    }

    // Méthode pour récupérer une ville par ID (utile pour UserService)
    public City findCityById(UUID cityId) {
        return cityRepository.findById(cityId)
                .orElseThrow(() -> new CityNotFoundException("Ville non trouvée avec l'ID: " + cityId));
    }


    // Méthode pour récupérer une ville par nom
    public Optional<City> findCityByName(String name) {
        return cityRepository.findByName(name);
    }

    // Méthode pour récupérer toutes les villes
    public List<City> findAllCities() {
        return cityRepository.findAll();
    }

    // Méthode pour récupérer les villes par pays
    public List<City> findCitiesByCountry(String country) {
        return cityRepository.findByCountry(country);
    }

    // Méthode pour récupérer les villes par région
    public List<City> findCitiesByRegion(String region) {
        return cityRepository.findByRegion(region);
    }

    /**
     * Méthode pour initialiser les villes par défaut
     * Crée 10 villes africaines si elles n'existent pas encore
     */
    public void initializeDefaultCities() {
        logger.info("Vérification des villes par défaut...");

        List<CityData> defaultCities = Arrays.asList(
                // Togo
                new CityData("Lomé", "Togo", "Maritime", 6.1725, 1.2314),
                new CityData("Kara", "Togo", "Kara", 9.5513, 1.1866),
                new CityData("Sokodé", "Togo", "Centrale", 8.9833, 1.1333),

                // Ghana
                new CityData("Accra", "Ghana", "Greater Accra", 5.6037, -0.1870),
                new CityData("Kumasi", "Ghana", "Ashanti", 6.7000, -1.6167),
                new CityData("Tamale", "Ghana", "Northern", 9.4075, -0.8533),

                // Côte d'Ivoire
                new CityData("Abidjan", "Côte d'Ivoire", "Lagunes", 5.3600, -4.0083),
                new CityData("Bouaké", "Côte d'Ivoire", "Vallée du Bandama", 7.6833, -5.0333),
                new CityData("Yamoussoukro", "Côte d'Ivoire", "Lacs", 6.8161, -5.2742),

                // Bénin
                new CityData("Cotonou", "Bénin", "Littoral", 6.3667, 2.4333)
        );

        int citiesCreated = 0;

        for (CityData cityData : defaultCities) {
            Optional<City> existingCity = cityRepository.findByName(cityData.name);

            if (existingCity.isEmpty()) {
                try {
                    City city = new City(cityData.name, cityData.country, cityData.region);
                    city.setLatitude(cityData.latitude);
                    city.setLongitude(cityData.longitude);

                    cityRepository.save(city);
                    citiesCreated++;
                    logger.info("Ville créée: {} - {}", cityData.name, cityData.country);

                } catch (Exception e) {
                    logger.warn("Erreur lors de la création de {}: {}", cityData.name, e.getMessage());
                }
            } else {
                logger.debug("Ville déjà existante: {}", cityData.name);
            }
        }

        logger.info("Initialisation des villes terminée. {} nouvelles villes créées.", citiesCreated);
    }

    /**
     * Classe interne pour stocker les données des villes par défaut
     */
    private static class CityData {
        String name;
        String country;
        String region;
        Double latitude;
        Double longitude;

        CityData(String name, String country, String region, Double latitude, Double longitude) {
            this.name = name;
            this.country = country;
            this.region = region;
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }

}
