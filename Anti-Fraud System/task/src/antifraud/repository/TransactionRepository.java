package antifraud.repository;

import antifraud.entity.Transaction;
import org.springframework.data.repository.CrudRepository;
import java.time.LocalDateTime;

public interface TransactionRepository extends CrudRepository<Transaction, Long> {
    Iterable<Transaction> findAllByNumberAndDateBetween(String number, LocalDateTime dateStart, LocalDateTime dateEnd);
    Iterable<Transaction> findAllByOrderById();
    Iterable<Transaction> findAllByNumberOrderById(String number);
}
