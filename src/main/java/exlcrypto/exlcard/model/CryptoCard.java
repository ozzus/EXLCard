package exlcrypto.exlcard.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "crypto_cards")
@Data
public class CryptoCard {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "crypto_card_id")
    private Long cryptoCardId;

    @Column(name = "crypto_card_number",nullable = false,length = 16)
    private String cryptoCardNumber;

    @JsonIgnore
    @Column(name = "cvv", nullable = false, length = 4)
    private String cvv;

    @Column(name = "expiry_date",nullable = false,length = 5)
    private String expiryDate;

    @JsonIgnore // Исключаем клиента из JSON
    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;
}
