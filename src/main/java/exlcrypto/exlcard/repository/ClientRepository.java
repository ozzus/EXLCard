package exlcrypto.exlcard.repository;

import exlcrypto.exlcard.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository  extends JpaRepository<Client, Long> {
    boolean existsByPhoneNumber(String phoneNumber);
}