package exlcrypto.exlcard.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "credit_card")
@Data
public class CreditCard {
    @GeneratedValue(strategy = GenerationType.UUID)
    @Id
    @Column(name = "credit_card_id")
    private Long CreditCardId;

    @Column(name = "creditCardnumber")
    private String CreditCardNumber;

    @Column(name = "cvv")
    @JsonIgnore
    private String cvv;

    @Column(name = "expiry_date")
    private String expiryDate;

}
