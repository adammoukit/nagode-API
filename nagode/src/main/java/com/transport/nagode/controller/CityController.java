package com.transport.nagode.controller;

import com.transport.nagode.models.City;
import com.transport.nagode.requestDTO.CreateCityDTO;
import com.transport.nagode.service.CityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/cities")
public class CityController {
    private final CityService cityService;

    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @PostMapping("/create-new")
    public ResponseEntity<?> createCity(@RequestBody CreateCityDTO createCityDTO) {
        try {
            City city = cityService.createCity(createCityDTO);
            return ResponseEntity.ok(city);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", e.getMessage())
            );
        }
    }

    @GetMapping("/getCities")
    public ResponseEntity<List<City>> getAllCities() {
        List<City> cities = cityService.findAllCities();
        return ResponseEntity.ok(cities);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCityById(@PathVariable UUID id) {
        try {
            City city = cityService.findCityById(id);
            return ResponseEntity.ok(city);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", e.getMessage())
            );
        }
    }

    @GetMapping("/country/{country}")
    public ResponseEntity<List<City>> getCitiesByCountry(@PathVariable String country) {
        List<City> cities = cityService.findCitiesByCountry(country);
        return ResponseEntity.ok(cities);
    }

    @GetMapping("/region/{region}")
    public ResponseEntity<List<City>> getCitiesByRegion(@PathVariable String region) {
        List<City> cities = cityService.findCitiesByRegion(region);
        return ResponseEntity.ok(cities);
    }

    @GetMapping("/search")
    public ResponseEntity<List<City>> searchCities(@RequestParam String query) {
        // Cette méthode utilisera le repository pour la recherche insensible à la casse
        // Vous devrez l'implémenter dans le service si nécessaire
        List<City> cities = cityService.findAllCities().stream()
                .filter(city -> city.getName().toLowerCase().contains(query.toLowerCase()))
                .toList();
        return ResponseEntity.ok(cities);
    }
}
