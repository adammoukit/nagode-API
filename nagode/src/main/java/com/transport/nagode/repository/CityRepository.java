package com.transport.nagode.repository;

import com.transport.nagode.models.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CityRepository extends JpaRepository<City, UUID> {
    Optional<City> findByName(String cityName);
    List<City> findByCountry(String country);

    List<City> findByRegion(String region);

    boolean existsByName(String name);

    // Recherche par nom de ville (insensible à la casse)
    Optional<City> findByNameIgnoreCase(String name);

    // Recherche de villes par pays et région
    List<City> findByCountryAndRegion(String country, String region);

    // Recherche de villes avec un nom contenant le texte (pour l'autocomplete)
    @Query("SELECT c FROM City c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<City> findByNameContainingIgnoreCase(@Param("query") String query);

    // Compter le nombre de villes par pays
    @Query("SELECT c.country, COUNT(c) FROM City c GROUP BY c.country")
    List<Object[]> countCitiesByCountry();
}
