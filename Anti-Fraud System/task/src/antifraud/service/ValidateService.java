package antifraud.service;

import antifraud.entity.*;
import antifraud.repository.BannedCardRepository;
import antifraud.repository.BannedIPRepository;
import antifraud.repository.TransactionRepository;
import antifraud.repository.UserRepository;
import antifraud.util.LuhnFormula;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ValidateService {

    private final UserRepository userRepository;
    private final BannedIPRepository bannedIPRepository;
    private final BannedCardRepository bannedCardRepository;
    private final TransactionRepository transactionRepository;

    public ValidateService(UserRepository userRepository,
                           BannedIPRepository bannedIPRepository,
                           BannedCardRepository bannedCardRepository,
                           TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.bannedIPRepository = bannedIPRepository;
        this.bannedCardRepository = bannedCardRepository;
        this.transactionRepository = transactionRepository;
    }

    void checkUserExist(String username) {
        if (userRepository.existsByUsernameIgnoreCase(username)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User exist!");
        }
    }

    public void checkUserNotFound(String username) {
        if (!userRepository.existsByUsernameIgnoreCase(username)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
        }
    }

    public Operation checkAndParseOperation(String operation) {
        try {
            return Operation.valueOf(operation);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Operation not found!");
        }
    }

    public Role checkAndParseRole(String role) {
        try {
            return Role.valueOf(role);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role not found!");
        }
    }

    public Validity checkAndParseValidity(String validity) {
        try {
            return Validity.valueOf(validity);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Validity not found!");
        }
    }

    public void checkChangeAdmin(User user) {
        if (user.getRole() == Role.ADMINISTRATOR) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Can't change Administrator!");
        }
    }

    public void checkInaccessibleRole(Role role) {
        if (role == Role.ADMINISTRATOR) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't add Administrator role!");
        }
    }

    public void checkAlreadyHaveRole(User user, Role role) {
        if (user.getRole() == role) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already have a role!");
        }
    }

    public void checkIpFormat(String ip) {
        String pattern = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
        if (!ip.matches(pattern)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong IP format!");
        }
    }

    public void checkBannedIPExist(String ip) {
        if (bannedIPRepository.existsByIp(ip)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Suspicious IP exist!");
        }
    }

    public void checkBannedIPNotFound(String ip) {
        if (!bannedIPRepository.existsByIp(ip)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Suspicious IP not found!");
        }
    }

    public void checkCardNumberFormat(String number) {
        if(!LuhnFormula.validate(number)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong card number format!");
        }
    }

    public void checkBannedCardExist(String number) {
        if (bannedCardRepository.existsByNumber(number)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Card number exist!");
        }
    }

    public void checkBannedCardNotFound(String number) {
        if (!bannedCardRepository.existsByNumber(number)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Card number not found!");
        }
    }

    public Region checkRegion(String region) {
        try {
            return Region.valueOf(region);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Region not found!");
        }
    }

    public void checkTransactionNotFound(Long id) {
        if (!transactionRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found!");
        }
    }

    public void checkTransactionFeedbackExist(Transaction transaction) {
        if(transaction.getFeedback() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Transaction feedback exist!");
        }
    }

    public void checkTransactionsByNumberNotFound(List<Transaction> transactions) {
        if (transactions.size() == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transactions not found!");
        }
    }

    public void checkFeedbackSameAsResult(Validity feedback, Validity result) {
        if (feedback.equals(result)) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Feedback is unprocessable!");
        }
    }
}
