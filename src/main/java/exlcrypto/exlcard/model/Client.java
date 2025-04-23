package exlcrypto.exlcard.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.List;


@Entity
@Data
@Table(name = "client", schema = "public")
public class Client {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "client_id")
    private Long clientId;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "email")
    private String email;
    @Column(name = "date_of_birthday")
    private Date dateOfBirthday;

    @OneToMany(mappedBy = "client",fetch = FetchType.LAZY)
    @JsonIgnore
    private List<CryptoCard> сryptoCards;

}
