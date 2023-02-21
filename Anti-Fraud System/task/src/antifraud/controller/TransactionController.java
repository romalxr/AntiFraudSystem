package antifraud.controller;

import antifraud.dto.TransactionDTO;
import antifraud.entity.Validity;
import antifraud.service.TransactionService;
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
        Validity result = transactionService.checkTransaction(transaction);
        return Map.of("result", result);
    }
}
