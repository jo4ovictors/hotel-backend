package br.edu.ifmg.hotelbao.dtos;

import br.edu.ifmg.hotelbao.entities.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "UserInsertDTO", description = "DTO used to insert a new user including password.")
public class UserInsertDTO extends UserDTO {

    @NotBlank(message = "Password is required")
    @Schema(
            description = "Password for the new user account",
            example = "StrongPassword123!",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String password;

    public UserInsertDTO() {
        super();
    }

    public UserInsertDTO(User entity) {
        super(entity);
        this.password = entity.getPassword();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}