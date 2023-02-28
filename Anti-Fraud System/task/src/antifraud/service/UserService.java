package antifraud.service;

import antifraud.dto.AccessDTO;
import antifraud.dto.RoleDTO;
import antifraud.dto.UserDTO;
import antifraud.entity.Operation;
import antifraud.entity.Role;
import antifraud.entity.User;
import antifraud.mapper.UserMapper;
import antifraud.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Validated
public class UserService {

    private final PasswordEncoder encoder;
    private final UserRepository userRepository;
    private final ValidateService validateService;

    public UserService(PasswordEncoder encoder, UserRepository userRepository, ValidateService validateService) {
        this.encoder = encoder;
        this.userRepository = userRepository;
        this.validateService = validateService;
    }

    public List<UserDTO> findAll() {
        List<User> users = (List<User>) userRepository.findAll();
        return users.stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
    }

    public UserDTO saveUser(@Valid UserDTO userDTO) {
        validateService.checkUserExist(userDTO.getUsername());
        User user = UserMapper.toEntity(userDTO);
        user.setPassword(encoder.encode(user.getPassword()));
        user.setRole(newUserRole());
        user.setEnabled(user.getRole() == Role.ADMINISTRATOR ? true : false);
        userRepository.save(user);
        return UserMapper.toDTO(user);
    }

    private Role newUserRole() {
        if (findAll().isEmpty()) {
            return Role.ADMINISTRATOR;
        } else {
            return Role.MERCHANT;
        }
    }

    public void deleteUser(String username) {
        userRepository.delete(getUser(username));
    }

    private User getUser(String username) {
        validateService.checkUserNotFound(username);
        return userRepository.findFirstByUsernameIgnoreCase(username).get();
    }

    public void changeAccess(@Valid AccessDTO access) {
        User user = getUser(access.getUsername());
        Operation operation = validateService.checkAndParseOperation(access.getOperation());
        validateService.checkChangeAdmin(user);
        user.setEnabled(operation == Operation.LOCK ? false : true);
        userRepository.save(user);
    }

    public UserDTO changeRole(@Valid RoleDTO roleDTO) {
        User user = getUser(roleDTO.getUsername());
        Role role = validateService.checkAndParseRole(roleDTO.getRole());
        validateService.checkInaccessibleRole(role);
        validateService.checkAlreadyHaveRole(user, role);
        validateService.checkChangeAdmin(user);
        user.setRole(role);
        userRepository.save(user);
        return UserMapper.toDTO(user);
    }
}
