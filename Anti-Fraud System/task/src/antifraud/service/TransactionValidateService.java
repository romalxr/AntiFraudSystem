package antifraud.service;

import antifraud.dto.TransactionDTO;
import antifraud.entity.Transaction;
import antifraud.entity.Validity;
import antifraud.repository.BannedCardRepository;
import antifraud.repository.BannedIPRepository;
import antifraud.repository.TransactionRepository;
import antifraud.util.LimitsUtil;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TransactionValidateService {
    private final TransactionRepository transactionRepository;
    private final BannedIPRepository bannedIPRepository;
    private final BannedCardRepository bannedCardRepository;

    public TransactionValidateService(TransactionRepository transactionRepository, BannedIPRepository bannedIPRepository, BannedCardRepository bannedCardRepository) {
        this.transactionRepository = transactionRepository;
        this.bannedIPRepository = bannedIPRepository;
        this.bannedCardRepository = bannedCardRepository;
    }

    public SortedMap<Validity, Set<String>> checkTransaction(TransactionDTO transaction) {
        SortedMap<Validity, Set<String>> results = new TreeMap();
        List<Transaction> latestTransactions = getLatestTransactions(transaction, 1);
        checkIpCorrelation(transaction, results, latestTransactions);
        checkRegionCorrelation(transaction, results, latestTransactions);
        checkTransactionAmount(transaction, results);
        checkBannedIp(transaction, results);
        checkBannedCard(transaction, results);
        return results;
    }

    private void checkBannedCard(TransactionDTO transaction, SortedMap<Validity, Set<String>> results) {
        if (bannedCardRepository.existsByNumber(transaction.getNumber())) {
            putOrUpdate(results, Validity.PROHIBITED, "card-number");
        }
    }

    private void checkBannedIp(TransactionDTO transaction, SortedMap<Validity, Set<String>> results) {
        if (bannedIPRepository.existsByIp(transaction.getIp())) {
            putOrUpdate(results, Validity.PROHIBITED, "ip");
        }
    }

    private void checkRegionCorrelation(TransactionDTO transaction, SortedMap<Validity, Set<String>> results, List<Transaction> transactions) {
        long countRegion = transactions.stream()
                .map(t -> t.getRegion().name())
                .filter(r -> !r.equals(transaction.getRegion()))
                .distinct()
                .count();

        if (countRegion == 2) {
            putOrUpdate(results, Validity.MANUAL_PROCESSING, "region-correlation");
        } else if (countRegion > 2) {
            putOrUpdate(results, Validity.PROHIBITED, "region-correlation");
        }
    }

    private void checkIpCorrelation(TransactionDTO transaction, SortedMap<Validity, Set<String>> results, List<Transaction> transactions) {
        long countIp = transactions.stream()
                .map(t -> t.getIp())
                .filter(i -> !i.equals(transaction.getIp()))
                .distinct()
                .count();
        if (countIp == 2) {
            putOrUpdate(results, Validity.MANUAL_PROCESSING, "ip-correlation");
        } else if (countIp > 2) {
            putOrUpdate(results, Validity.PROHIBITED, "ip-correlation");
        }
    }

    private List<Transaction> getLatestTransactions(TransactionDTO transaction, int hours) {
        List<Transaction> transactions = (List<Transaction>) transactionRepository
                .findAllByNumberAndDateBetween(transaction.getNumber(),
                        transaction.getDate().plusHours(-hours),
                        transaction.getDate());
        return transactions;
    }

    private void checkTransactionAmount(TransactionDTO transaction, SortedMap<Validity, Set<String>> results) {
        if (transaction.getAmount() > LimitsUtil.MAX_MANUAL.get()) {
            putOrUpdate(results, Validity.PROHIBITED, "amount");
        } else if (transaction.getAmount() > LimitsUtil.MAX_ALLOWED.get()) {
            putOrUpdate(results, Validity.MANUAL_PROCESSING, "amount");
        } else {
            putOrUpdate(results, Validity.ALLOWED, "none");
        }
    }

    private void putOrUpdate(Map<Validity, Set<String>> map, Validity key, String value) {
        if(!map.containsKey(key)){
            map.put(key, new TreeSet<String>());
        }
        map.get(key).add(value);
    }
}
