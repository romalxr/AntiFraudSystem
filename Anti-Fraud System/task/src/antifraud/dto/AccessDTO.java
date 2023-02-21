package antifraud.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AccessDTO {
    @NotBlank
    String username;
    @NotBlank
    String operation;
    public void setOperation(String operation) {
        this.operation = operation.toUpperCase();
    }
}
