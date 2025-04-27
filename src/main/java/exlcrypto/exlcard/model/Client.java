package exlcrypto.exlcard.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;

@Entity
@Data
@Table(name = "client", schema = "public")
public class Client {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long clientId;

    @NotBlank(message = "Имя обязательно")
    @Size(min = 2, max = 50, message = "Имя должно быть от 2 до 50 символов")
    private String firstName;

    @NotBlank(message = "Фамилия обязательна")
    @Size(min = 2, max = 50, message = "Фамилия должна быть от 2 до 50 символов")
    private String lastName;

    @Email(message = "Некорректный формат email")
    private String email;

    @Past(message = "Дата рождения должна быть в прошлом")
    @Temporal(TemporalType.DATE)
    private Date dateOfBirthday;
}

//    @OneToMany(mappedBy = "client",fetch = FetchType.LAZY)
//    @JsonIgnore
//    private List<CryptoCard> сryptoCards;


