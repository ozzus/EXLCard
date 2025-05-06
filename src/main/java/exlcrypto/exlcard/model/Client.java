package exlcrypto.exlcard.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;


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

    @NotBlank(message = "Номер телефона обязателен")
    @Pattern(
            regexp = "^(\\+7\\d{10,11}|8\\d{10})$",
            message = "Форматы: +7XXXXXXXXXX (12-13 символов) или 8XXXXXXXXXX (11 символов)"
    )
    @Column(name = "phone_number", length = 20,unique = true)
    private String phoneNumber;

    @Past(message = "Дата рождения должна быть в прошлом")
    @JsonFormat(pattern = "dd.MM.yyyy")
    @Column(name = "date_of_birthday")
    private LocalDate dateOfBirthday;
}

//    @OneToMany(mappedBy = "client",fetch = FetchType.LAZY)
//    @JsonIgnore
//    private List<CryptoCard> сryptoCards;


