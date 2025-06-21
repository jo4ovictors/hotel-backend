package br.edu.ifmg.hotelbao.dtos;

import br.edu.ifmg.hotelbao.entities.User;
import jakarta.validation.constraints.NotBlank;

public class UserInsertDTO extends UserDTO {

    @NotBlank(message = "[!] -> Password is required")
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
