package antifraud.service;

import antifraud.dto.TransactionDTO;
import antifraud.entity.Validity;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

@Service
@Validated
public class TransactionService {

    public Validity checkTransaction(@Valid TransactionDTO transaction) {
        if (transaction.getAmount() > 1500L) {
            return Validity.PROHIBITED;
        } else if (transaction.getAmount() > 200L) {
            return Validity.MANUAL_PROCESSING;
        } else {
            return Validity.ALLOWED;
        }
    }
}
