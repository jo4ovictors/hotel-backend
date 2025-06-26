package br.edu.ifmg.hotelbao.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "RequestTokenDTO", description = "Data Transfer Object used to request a password reset or authentication token using an email address.")
public class RequestTokenDTO {

    @Schema(
            description = "User's email address used to request the token",
            example = "user@example.com"
    )
    @NotBlank(message = "Field is required")
    @Email(message = "Invalid email address")
    private String email;

    public RequestTokenDTO() {
    }

    public RequestTokenDTO(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
