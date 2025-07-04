package br.edu.ifmg.hotelbao.dtos;

import br.edu.ifmg.hotelbao.entities.User;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;

@Schema(name = "UserInvoiceDTO", description = "Data Transfer Object representing user information for invoicing purposes")
public class UserInvoiceDTO {

    @Schema(description = "Unique identifier of the user", example = "1")
    private Long id;

    @Schema(description = "User's first name", example = "John")
    private String firstName;

    @Schema(description = "User's last name", example = "Doe")
    private String lastName;

    @Schema(description = "User's email address", example = "john.doe@example.com")
    private String email;

    @Schema(description = "User's login username", example = "johndoe")
    private String login;

    @Schema(description = "User's phone number", example = "+55 31 91234-5678")
    private String phone;

    @Schema(description = "User's address details")
    private AddressDTO address;

    public UserInvoiceDTO() {
    }

    public UserInvoiceDTO(Long id, String firstName, String lastName, String email, String login, String phone, AddressDTO address) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.login = login;
        this.phone = phone;
        this.address = address;
    }

    public UserInvoiceDTO(User entity) {
        this.id = entity.getId();
        this.firstName = entity.getFirstName();
        this.lastName = entity.getLastName();
        this.email = entity.getEmail();
        this.login = entity.getLogin();
        this.phone = entity.getPhone();
        this.address = entity.getAddress() != null ? new AddressDTO(entity.getAddress()) : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public AddressDTO getAddress() {
        return address;
    }

    public void setAddress(AddressDTO address) {
        this.address = address;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof UserInvoiceDTO userDTO)) return false;
        return Objects.equals(id, userDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", login='" + login + '\'' +
                ", phone='" + phone + '\'' +
                ", address=" + address +
                '}';
    }
}