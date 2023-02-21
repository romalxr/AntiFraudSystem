package antifraud.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class RoleDTO {
    @NotBlank
    String username;
    @NotBlank
    String role;
    public void setRole(String role) {
        this.role = role.toUpperCase();
    }
}
