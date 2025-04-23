package exlcrypto.exlcard.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "crypto_cards")
@Data
public class CryptoCard {
    @GeneratedValue(strategy = GenerationType.UUID)
    @Id
    @Column(name = "crypto_card_id")
    private Long cryptoCardId;

    @Column(name = "cryptoCardnumber")
    private String cryptoCardNumber;

    @Column(name = "cvv")
    @JsonIgnore
    private String cvv;

    @Column(name = "expiry_date")
    private String expiryDate;

    @ManyToOne
    @JoinColumn(name = "client_id") // Added to map the client relationship
    private Client client;
}
