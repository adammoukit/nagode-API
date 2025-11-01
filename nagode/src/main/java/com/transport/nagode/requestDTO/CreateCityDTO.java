package com.transport.nagode.requestDTO;

public class CreateCityDTO {
    private String name;
    private String country;
    private String region;
    private Double latitude;
    private Double longitude;

    public CreateCityDTO() {
    }

    public CreateCityDTO(String name, String region, String country) {
        this.name = name;
        this.region = region;
        this.country = country;
    }

    public CreateCityDTO(String name, String country, String region, Double latitude, Double longitude) {
        this.name = name;
        this.country = country;
        this.region = region;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
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
