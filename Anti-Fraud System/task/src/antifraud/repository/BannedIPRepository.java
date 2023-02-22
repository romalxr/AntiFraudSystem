package antifraud.repository;

import antifraud.entity.BannedIP;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface BannedIPRepository extends CrudRepository<BannedIP, Long> {
    boolean existsByIp(String ip);
    Optional<BannedIP> findFirstByIp(String ip);
}
