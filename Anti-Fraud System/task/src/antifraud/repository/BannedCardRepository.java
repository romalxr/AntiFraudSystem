package antifraud.repository;

import antifraud.entity.BannedCard;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface BannedCardRepository extends CrudRepository<BannedCard, Long> {
    boolean existsByNumber(String number);
    Optional<BannedCard> findFirstByNumber(String number);
}
