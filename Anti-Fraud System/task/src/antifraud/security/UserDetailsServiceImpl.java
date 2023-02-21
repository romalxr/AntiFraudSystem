package antifraud.security;

import antifraud.entity.User;
import antifraud.repository.UserRepository;
import antifraud.util.LogUtil;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LogUtil.increase();
        Optional<User> user = userRepository.findFirstByUsernameIgnoreCase(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("Not found: " + username);
        }
        System.out.println("Auth with role: " + user.get().getRole());
        return new UserDetailsImpl(user.get());
    }
}
