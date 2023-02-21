package antifraud.dto;


import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class TransactionDTO {

    @NotNull
    @Min(value = 1)
    private Long amount;
}
