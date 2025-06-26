package br.edu.ifmg.hotelbao.dtos;

import br.edu.ifmg.hotelbao.entities.Role;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;

@Schema(description = "Data transfer object that represents a user role or authority (e.g., ROLE_ADMIN, ROLE_EMPLOYEE)")
public class RoleDTO {

    @Schema(description = "Unique identifier of the role", example = "1")
    private Long id;

    @Schema(description = "Name of the authority or role assigned to the user", example = "ROLE_ADMIN")
    private String authority;

    public RoleDTO() {
    }

    public RoleDTO(Long id, String authority) {
        this.id = id;
        this.authority = authority;
    }

    public RoleDTO(Role entity) {
        this.id = entity.getId();
        this.authority = entity.getAuthority();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RoleDTO roleDTO)) return false;
        return Objects.equals(id, roleDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "RoleDTO{" +
                "id=" + id +
                ", authority='" + authority + '\'' +
                '}';
    }

}
