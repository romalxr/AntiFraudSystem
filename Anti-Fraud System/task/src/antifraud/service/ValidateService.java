package antifraud.service;

import antifraud.entity.Operation;
import antifraud.entity.Role;
import antifraud.entity.User;
import antifraud.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ValidateService {

    private final UserRepository userRepository;

    public ValidateService(UserRepository userRepository) {
        this.userRepository = userRepository;
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

    public void checkChangeAdmin(User user) {
        if (user.getRole() == Role.ADMINISTRATOR) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Can't change Administrator!");
        }
    }

    public void checkUnaccessibleRole(Role role) {
        if (role == Role.ADMINISTRATOR) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't add Administrator role!");
        }
    }

    public void checkAlreadyHaveRole(User user, Role role) {
        if (user.getRole() == role) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already have a role!");
        }
    }
}
