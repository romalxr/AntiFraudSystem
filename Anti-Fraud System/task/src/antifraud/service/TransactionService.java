package antifraud.service;

import antifraud.dto.TransactionDTO;
import antifraud.entity.Validity;
import antifraud.security.UserDetailsImpl;
import antifraud.util.LogUtil;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

@Service
@Validated
public class TransactionService {

    public String checkTransaction(@Valid TransactionDTO transaction) {
        if (transaction.getAmount() > 1500L) {
            return Validity.PROHIBITED.name();
        } else if (transaction.getAmount() > 200L) {
            return Validity.MANUAL_PROCESSING.name();
        } else {
            return Validity.ALLOWED.name();
        }
    }
}
