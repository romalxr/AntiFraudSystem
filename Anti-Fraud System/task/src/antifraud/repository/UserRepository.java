package antifraud.repository;

import antifraud.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, String> {
    Optional<User> findFirstByUsernameIgnoreCase(String username);
    boolean existsByUsernameIgnoreCase(String username);
}
