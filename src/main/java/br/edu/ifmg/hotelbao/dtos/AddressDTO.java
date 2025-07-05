package br.edu.ifmg.hotelbao.dtos;

import br.edu.ifmg.hotelbao.entities.Address;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;

@Schema(name = "AddressDTO", description = "Data Transfer Object representing an address, including street, city, state, postal code, and country.")
public class AddressDTO {

    @Schema(description = "Unique identifier of the address", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Street name and number", example = "123 Ocean Drive")
    private String street;

    @Schema(description = "City name", example = "Miami")
    private String city;

    @Schema(description = "State or province", example = "Florida")
    private String state;

    @Schema(description = "Postal code (ZIP or equivalent)", example = "33139")
    private String postalCode;

    @Schema(description = "Country name", example = "United States")
    private String country;

    public AddressDTO() {
    }

    public AddressDTO(Long id, String street, String city, String state, String postalCode, String country) {
        this.id = id;
        this.street = street;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.country = country;
    }

    public AddressDTO(Address entity) {
        this.id = entity.getId();
        this.street = entity.getStreet();
        this.city = entity.getCity();
        this.state = entity.getState();
        this.postalCode = entity.getPostalCode();
        this.country = entity.getCountry();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AddressDTO addressDTO)) return false;
        return Objects.equals(id, addressDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "AddressDTO{" +
                "id=" + id +
                ", street='" + street + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", country='" + country + '\'' +
                '}';
    }

}