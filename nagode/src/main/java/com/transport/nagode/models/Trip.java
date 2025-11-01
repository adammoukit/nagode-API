package com.transport.nagode.models;


import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "trips")
public class Trip extends AbstractEntity{
    @Column(nullable = false, unique = true)
    private String tripNumber; // Ex: "LOM-ABJ-20241201-001"

    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne
    @JoinColumn(name = "driver_id", nullable = false)
    private User driver;

    @ManyToOne
    @JoinColumn(name = "departure_city_id", nullable = false)
    private City departureCity;

    @ManyToOne
    @JoinColumn(name = "arrival_city_id", nullable = false)
    private City arrivalCity;

    @Column(nullable = false)
    private LocalDateTime scheduledDeparture;

    private LocalDateTime actualDeparture;

    private LocalDateTime estimatedArrival;

    private LocalDateTime actualArrival;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TripStatus status = TripStatus.SCHEDULED;

    private Double pricePerSeat;

    private Double cargoPricePerKg;

    private Integer availableSeats;

    private String routeDescription; // Ex: "Lomé - Accra - Abidjan"

    @ElementCollection
    @CollectionTable(name = "trip_intermediate_cities", joinColumns = @JoinColumn(name = "trip_id"))
    @Column(name = "city_order")
    private List<String> intermediateCities = new ArrayList<>();

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL)
    private List<Booking> bookings = new ArrayList<>();

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL)
    private List<Cargo> cargoList = new ArrayList<>();

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL)
    private List<TripTracking> trackingPoints = new ArrayList<>();

    // Constructors
    public Trip() {}

    public Trip(String tripNumber, Vehicle vehicle, User driver, City departureCity,
                City arrivalCity, LocalDateTime scheduledDeparture) {
        this.tripNumber = tripNumber;
        this.vehicle = vehicle;
        this.driver = driver;
        this.departureCity = departureCity;
        this.arrivalCity = arrivalCity;
        this.scheduledDeparture = scheduledDeparture;
        this.availableSeats = vehicle.getPassengerCapacity();
    }

    public String getTripNumber() {
        return tripNumber;
    }

    public void setTripNumber(String tripNumber) {
        this.tripNumber = tripNumber;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public User getDriver() {
        return driver;
    }

    public void setDriver(User driver) {
        this.driver = driver;
    }

    public City getDepartureCity() {
        return departureCity;
    }

    public void setDepartureCity(City departureCity) {
        this.departureCity = departureCity;
    }

    public City getArrivalCity() {
        return arrivalCity;
    }

    public void setArrivalCity(City arrivalCity) {
        this.arrivalCity = arrivalCity;
    }

    public LocalDateTime getScheduledDeparture() {
        return scheduledDeparture;
    }

    public void setScheduledDeparture(LocalDateTime scheduledDeparture) {
        this.scheduledDeparture = scheduledDeparture;
    }

    public LocalDateTime getActualDeparture() {
        return actualDeparture;
    }

    public void setActualDeparture(LocalDateTime actualDeparture) {
        this.actualDeparture = actualDeparture;
    }

    public LocalDateTime getEstimatedArrival() {
        return estimatedArrival;
    }

    public void setEstimatedArrival(LocalDateTime estimatedArrival) {
        this.estimatedArrival = estimatedArrival;
    }

    public LocalDateTime getActualArrival() {
        return actualArrival;
    }

    public void setActualArrival(LocalDateTime actualArrival) {
        this.actualArrival = actualArrival;
    }

    public TripStatus getStatus() {
        return status;
    }

    public void setStatus(TripStatus status) {
        this.status = status;
    }

    public Double getPricePerSeat() {
        return pricePerSeat;
    }

    public void setPricePerSeat(Double pricePerSeat) {
        this.pricePerSeat = pricePerSeat;
    }

    public Double getCargoPricePerKg() {
        return cargoPricePerKg;
    }

    public void setCargoPricePerKg(Double cargoPricePerKg) {
        this.cargoPricePerKg = cargoPricePerKg;
    }

    public Integer getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(Integer availableSeats) {
        this.availableSeats = availableSeats;
    }

    public String getRouteDescription() {
        return routeDescription;
    }

    public void setRouteDescription(String routeDescription) {
        this.routeDescription = routeDescription;
    }

    public List<String> getIntermediateCities() {
        return intermediateCities;
    }

    public void setIntermediateCities(List<String> intermediateCities) {
        this.intermediateCities = intermediateCities;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    public List<Cargo> getCargoList() {
        return cargoList;
    }

    public void setCargoList(List<Cargo> cargoList) {
        this.cargoList = cargoList;
    }

    public List<TripTracking> getTrackingPoints() {
        return trackingPoints;
    }

    public void setTrackingPoints(List<TripTracking> trackingPoints) {
        this.trackingPoints = trackingPoints;
    }
}
