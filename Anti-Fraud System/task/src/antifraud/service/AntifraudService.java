package antifraud.service;

import antifraud.dto.TransactionDTO;
import antifraud.entity.BannedCard;
import antifraud.entity.BannedIP;
import antifraud.entity.Validity;
import antifraud.repository.BannedCardRepository;
import antifraud.repository.BannedIPRepository;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Validated
public class AntifraudService {

    private final BannedIPRepository bannedIPRepository;
    private final BannedCardRepository bannedCardRepository;
    private final ValidateService validateService;

    public AntifraudService(BannedIPRepository bannedIPRepository, BannedCardRepository bannedCardRepository, ValidateService validateService) {
        this.bannedIPRepository = bannedIPRepository;
        this.bannedCardRepository = bannedCardRepository;
        this.validateService = validateService;
    }

    public Map<String, String> checkTransaction(@Valid TransactionDTO transaction) {
        validateService.checkIpFormat(transaction.getIp());
        validateService.checkCardNumberFormat(transaction.getNumber());

        Validity result;
        List<String> reasons = new ArrayList<>();
        if (transaction.getAmount() > 1500L) {
            result = Validity.PROHIBITED;
            reasons.add("amount");
        } else if (transaction.getAmount() > 200L) {
            result = Validity.MANUAL_PROCESSING;
            reasons.add("amount");
        } else {
            result = Validity.ALLOWED;
            reasons.add("none");
        }

        if (bannedCardRepository.existsByNumber(transaction.getNumber())) {
            if (result != Validity.PROHIBITED) {
                result = Validity.PROHIBITED;
                reasons.clear();
            }
            reasons.add("card-number");
        }

        if (bannedIPRepository.existsByIp(transaction.getIp())) {
            if (result != Validity.PROHIBITED) {
                result = Validity.PROHIBITED;
                reasons.clear();
            }
            reasons.add("ip");
        }

        return Map.of("result", result.name(), "info", String.join(", ", reasons));
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
}
