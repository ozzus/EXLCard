package exlcrypto.exlcard.repository;

import exlcrypto.exlcard.model.CreditCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CreditCardRepository extends JpaRepository<CreditCard, Long> {
    List<CreditCard> findByClientId(Long clientId);
}
