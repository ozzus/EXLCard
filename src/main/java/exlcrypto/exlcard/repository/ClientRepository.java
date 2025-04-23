package exlcrypto.exlcard.repository;

import exlcrypto.exlcard.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ClientRepository  extends JpaRepository<Client, Long> {

}
