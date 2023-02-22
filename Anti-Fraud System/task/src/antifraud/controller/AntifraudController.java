package antifraud.controller;

import antifraud.dto.TransactionDTO;
import antifraud.entity.BannedCard;
import antifraud.entity.BannedIP;
import antifraud.service.AntifraudService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class AntifraudController {

    private final AntifraudService antifraudService;

    public AntifraudController(AntifraudService antifraudService) {
        this.antifraudService = antifraudService;
    }

    @PostMapping("api/antifraud/transaction")
    public Object checkTransaction(@RequestBody TransactionDTO transaction) {
        return antifraudService.checkTransaction(transaction);
    }

    @PostMapping("/api/antifraud/suspicious-ip")
    public BannedIP addBannedIP(@RequestBody BannedIP bannedIP) {
        return antifraudService.addBannedIP(bannedIP);
    }

    @DeleteMapping("/api/antifraud/suspicious-ip/{ip}")
    public Object deleteBannedIP(@PathVariable String ip) {
        antifraudService.deleteBannedIP(ip);
        return Map.of("status", "IP " + ip + " successfully removed!");
    }

    @GetMapping("/api/antifraud/suspicious-ip")
    public List<BannedIP> getBannedIP() {
        return antifraudService.findAllBannedIP();
    }

    @PostMapping("/api/antifraud/stolencard")
    public BannedCard addBannedCard(@RequestBody BannedCard bannedCard) {
        return antifraudService.addBannedCard(bannedCard);
    }

    @DeleteMapping("/api/antifraud/stolencard/{number}")
    public Object deleteBannedCard(@PathVariable String number) {
        antifraudService.deleteBannedCard(number);
        return Map.of("status", "Card " + number + " successfully removed!");
    }

    @GetMapping("/api/antifraud/stolencard")
    public List<BannedCard> getBannedCard() {
        return antifraudService.findAllBannedCard();
    }
}
