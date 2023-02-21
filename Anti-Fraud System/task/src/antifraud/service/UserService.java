package antifraud.service;

import antifraud.dto.UserDTO;
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

    public List<UserDTO> getAll() {
        List<User> users = (List<User>) userRepository.findAll();
        return users.stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
    }

    public UserDTO saveUser(@Valid UserDTO userDTO) {
        validateService.checkUserExist(userDTO.getUsername());
        User user = UserMapper.toEntity(userDTO);
        user.setPassword(encoder.encode(user.getPassword()));
        userRepository.save(user);
        return UserMapper.toDTO(user);
    }

    public void deleteUser(String username) {
        userRepository.delete(getUser(username));
    }

    private User getUser(String username) {
        validateService.checkUserNotFound(username);
        return userRepository.findFirstByUsernameIgnoreCase(username).get();
    }
}
