package exlcrypto.exlcard.repository;

import exlcrypto.exlcard.model.CryptoCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CryptoCardRepository extends JpaRepository<CryptoCard, Long> {
    List<CryptoCard> findByClientId(Long clientId);
}
