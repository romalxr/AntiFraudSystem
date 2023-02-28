package antifraud.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class FeedbackDTO {
    @Min(0)
    @NotNull
    Long transactionId;
    @NotBlank
    String feedback;

    public void setFeedback(String feedback) {
        this.feedback = feedback.toUpperCase();
    }
}