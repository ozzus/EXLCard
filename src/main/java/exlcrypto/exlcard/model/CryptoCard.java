package exlcrypto.exlcard.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "crypto_cards")
@Data
public class CryptoCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cryptoCardId;

    @Column(nullable = false, length = 19)
    private String cryptoCardNumber;

    @JsonIgnore
    @Column(nullable = false, length = 4)
    private String cvv;

    @Column(nullable = false, length = 5)
    private String expiryDate;

    @Column(unique = true,length = 36)
    private String cvvToken; // Для одноразового доступа

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;
}
