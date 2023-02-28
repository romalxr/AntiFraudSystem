package antifraud.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TransactionDTO {
    private Long transactionId;
    @NotNull
    @Min(value = 1)
    private Long amount;
    @NotBlank
    private String ip;
    @NotBlank
    private String number;
    @NotBlank
    private String region;
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime date;
    private String result;
    private String feedback;
    public void setRegion(String region) {
        this.region = region.toUpperCase();
    }
}
