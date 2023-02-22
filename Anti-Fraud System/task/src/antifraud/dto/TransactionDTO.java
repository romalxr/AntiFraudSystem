package antifraud.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class TransactionDTO {

    @NotNull
    @Min(value = 1)
    private Long amount;

    @NotBlank
    private String ip;

    @NotBlank
    private String number;
}
