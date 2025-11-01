package com.transport.nagode.authDto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public class RegisterRequest {
    @NotBlank(message = "Le prénom est obligatoire")
    @Size(min = 2, max = 50, message = "Le prénom doit contenir entre 2 et 50 caractères")
    private String firstName;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 50, message = "Le nom doit contenir entre 2 et 50 caractères")
    private String lastName;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    private String email;

    @Pattern(regexp = "^\\+?[0-9\\s\\-\\(\\)]{10,}$", message = "Format de téléphone invalide")
    private String phone;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    private String password;

    @NotBlank(message = "La confirmation du mot de passe est obligatoire")
    private String confirmPassword;

    private String address;

    private UUID cityId;

    private String driverLicenseNumber;

    private LocalDateTime licenseExpiryDate;

    private Set<String> roles; // Noms des rôles comme "ROLE_DRIVER", "ROLE_PASSENGER", "ROLE_ADMIN"

    // Constructeurs
    public RegisterRequest() {}

    public RegisterRequest(String firstName, String lastName, String email, String phone,
                           String password, String confirmPassword, String address,
                           UUID cityId, String driverLicenseNumber, LocalDateTime licenseExpiryDate,
                           Set<String> roles) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.address = address;
        this.cityId = cityId;
        this.driverLicenseNumber = driverLicenseNumber;
        this.licenseExpiryDate = licenseExpiryDate;
        this.roles = roles;
    }

    // Getters et setters
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public UUID getCityId() { return cityId; }
    public void setCityId(UUID cityId) { this.cityId = cityId; }

    public String getDriverLicenseNumber() { return driverLicenseNumber; }
    public void setDriverLicenseNumber(String driverLicenseNumber) { this.driverLicenseNumber = driverLicenseNumber; }

    public LocalDateTime getLicenseExpiryDate() { return licenseExpiryDate; }
    public void setLicenseExpiryDate(LocalDateTime licenseExpiryDate) { this.licenseExpiryDate = licenseExpiryDate; }

    public Set<String> getRoles() { return roles; }
    public void setRoles(Set<String> roles) { this.roles = roles; }

    // Méthode utilitaire pour la validation
    public boolean passwordsMatch() {
        return password != null && password.equals(confirmPassword);
    }

    @Override
    public String toString() {
        return "RegisterRequest{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", password='" + password + '\'' +
                ", confirmPassword='" + confirmPassword + '\'' +
                ", address='" + address + '\'' +
                ", cityId=" + cityId +
                ", driverLicenseNumber='" + driverLicenseNumber + '\'' +
                ", licenseExpiryDate=" + licenseExpiryDate +
                ", roles=" + roles +
                '}';
    }
}
