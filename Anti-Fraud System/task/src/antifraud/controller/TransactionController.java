package antifraud.controller;

import antifraud.dto.TransactionDTO;
import antifraud.entity.Validity;
import antifraud.service.TransactionService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("api/antifraud/transaction")
    public Object checkTransaction(@RequestBody TransactionDTO transaction) {
        String result = transactionService.checkTransaction(transaction);
        return Map.of("result", result);
    }
}
