package exlcrypto.exlcard.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;


@Entity
@Data
@Table(name = "client")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long clientId;

    @NotBlank(message = "{validation.client.firstName.required}")
    @Size(min = 2, max = 50, message = "{validation.client.firstName.size}")
    private String firstName;

    @NotBlank(message = "{validation.client.lastName.required}")
    @Size(min = 2, max = 50, message = "{validation.client.lastName.size}")
    private String lastName;

    @Email(message = "{validation.client.email.invalid}")
    private String email;

    @NotBlank(message = "{validation.client.phone.required}")
    @Pattern(
            regexp = "^\\+7\\d{10}$",
            message = "{validation.client.phone.pattern}"
    )
    @Column(unique = true)
    private String phoneNumber;

    @Past(message = "{validation.client.birthdate.past}")
    @JsonFormat(pattern = "dd.MM.yyyy")
    private LocalDate dateOfBirthday;
}
