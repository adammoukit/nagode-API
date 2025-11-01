package com.transport.nagode.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "cities")
public class City extends AbstractEntity{

    @Column(nullable = false, unique = true)
    private String name;

    private String country; // "Togo", "Ghana", "Côte d'Ivoire", etc.

    private String region; // "Maritime", "Plateaux", "Savanes", etc.

    private Double latitude;

    private Double longitude;

    // Constructors
    public City() {}

    public City(String name, String country, String region) {
        this.name = name;
        this.country = country;
        this.region = region;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
