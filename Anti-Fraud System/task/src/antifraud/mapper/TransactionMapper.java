package antifraud.mapper;

import antifraud.dto.TransactionDTO;
import antifraud.entity.Region;
import antifraud.entity.Transaction;
import antifraud.entity.Validity;

public class TransactionMapper {
    public static Transaction toEntity(TransactionDTO transactionDTO){
        return Transaction.builder()
                .amount(transactionDTO.getAmount())
                .ip(transactionDTO.getIp())
                .number(transactionDTO.getNumber())
                .region(Region.valueOf(transactionDTO.getRegion()))
                .date(transactionDTO.getDate())
                .build();
    }

    public static TransactionDTO toDTO(Transaction transaction){
        return TransactionDTO.builder()
                .transactionId(transaction.getId())
                .amount(transaction.getAmount())
                .ip(transaction.getIp())
                .number(transaction.getNumber())
                .region(transaction.getRegion().name())
                .date(transaction.getDate())
                .result(transaction.getResult().name())
                .feedback(parseFeedback(transaction.getFeedback()))
                .build();
    }

    private static String parseFeedback(Validity feedback) {
        if (feedback == null) {
            return "";
        }
        return feedback.name();
    }
}
