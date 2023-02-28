package antifraud.service;

import antifraud.dto.FeedbackDTO;
import antifraud.dto.TransactionDTO;
import antifraud.entity.*;
import antifraud.mapper.TransactionMapper;
import antifraud.repository.BannedCardRepository;
import antifraud.repository.BannedIPRepository;
import antifraud.repository.TransactionRepository;
import antifraud.util.LimitsUtil;
import antifraud.util.LogUtil;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.*;

@Service
@Validated
public class AntifraudService {

    private final BannedIPRepository bannedIPRepository;
    private final BannedCardRepository bannedCardRepository;
    private final ValidateService validateService;
    private final TransactionRepository transactionRepository;
    private final TransactionValidateService transactionValidateService;

    public AntifraudService(BannedIPRepository bannedIPRepository,
                            BannedCardRepository bannedCardRepository,
                            ValidateService validateService,
                            TransactionRepository transactionRepository,
                            TransactionValidateService transactionValidateService) {
        this.bannedIPRepository = bannedIPRepository;
        this.bannedCardRepository = bannedCardRepository;
        this.validateService = validateService;
        this.transactionRepository = transactionRepository;
        this.transactionValidateService = transactionValidateService;
    }

    public Map<String, String> addTransaction(@Valid TransactionDTO transaction) {
        validateService.checkIpFormat(transaction.getIp());
        validateService.checkCardNumberFormat(transaction.getNumber());
        validateService.checkRegion(transaction.getRegion());
        SortedMap<Validity, Set<String>> result = transactionValidateService.checkTransaction(transaction);
        Transaction transactionEntity = TransactionMapper.toEntity(transaction);
        transactionEntity.setResult(result.firstKey());
        transactionRepository.save(transactionEntity);
        return Map.of("result", result.firstKey().name(),
                "info", String.join(", ", result.get(result.firstKey())));
    }

    public TransactionDTO addFeedback(FeedbackDTO feedback) {
        Transaction transaction = getTransaction(feedback.getTransactionId());
        Validity validity = validateService.checkAndParseValidity(feedback.getFeedback());
        validateService.checkTransactionFeedbackExist(transaction);
        validateService.checkFeedbackSameAsResult(validity, transaction.getResult());
        transaction.setFeedback(validity);
        transactionRepository.save(transaction);
        LimitsUtil.updateLimits(transaction.getResult(), validity, transaction.getAmount());
        return TransactionMapper.toDTO(transaction);
    }

    public BannedIP addBannedIP(@Valid BannedIP bannedIP) {
        validateService.checkIpFormat(bannedIP.getIp());
        validateService.checkBannedIPExist(bannedIP.getIp());
        bannedIPRepository.save(bannedIP);
        return bannedIP;
    }

    public void deleteBannedIP(String ip) {
        validateService.checkIpFormat(ip);
        validateService.checkBannedIPNotFound(ip);
        BannedIP bannedIP = bannedIPRepository.findFirstByIp(ip).get();
        bannedIPRepository.delete(bannedIP);
    }

    public List<BannedIP> findAllBannedIP() {
        return (List<BannedIP>) bannedIPRepository.findAll();
    }

    public BannedCard addBannedCard(BannedCard bannedCard) {
        validateService.checkCardNumberFormat(bannedCard.getNumber());
        validateService.checkBannedCardExist(bannedCard.getNumber());
        bannedCardRepository.save(bannedCard);
        return bannedCard;
    }

    public void deleteBannedCard(String number) {
        validateService.checkCardNumberFormat(number);
        validateService.checkBannedCardNotFound(number);
        BannedCard bannedCard = bannedCardRepository.findFirstByNumber(number).get();
        bannedCardRepository.delete(bannedCard);
    }

    public List<BannedCard> findAllBannedCard() {
        return (List<BannedCard>) bannedCardRepository.findAll();
    }

    private Transaction getTransaction(Long id) {
        validateService.checkTransactionNotFound(id);
        return transactionRepository.findById(id).get();
    }

    public List<TransactionDTO> getTransactionHistory() {
        List<Transaction> transactions = (List<Transaction>) transactionRepository.findAllByOrderById();
        return transactions.stream()
                .map(TransactionMapper::toDTO)
                .toList();
    }

    public List<TransactionDTO> getTransactionsByNumber(String number) {
        validateService.checkCardNumberFormat(number);
        List<Transaction> transactions = (List<Transaction>) transactionRepository.findAllByNumberOrderById(number);
        validateService.checkTransactionsByNumberNotFound(transactions);
        return transactions.stream()
                .map(TransactionMapper::toDTO)
                .toList();
    }
}