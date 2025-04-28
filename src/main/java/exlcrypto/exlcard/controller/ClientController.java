package exlcrypto.exlcard.controller;

import exlcrypto.exlcard.model.Client;
import exlcrypto.exlcard.repository.ClientRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/clients")
@Slf4j
public class ClientController {

    private final ClientRepository clientRepository;

    @Autowired
    public ClientController(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @PostMapping
    public ResponseEntity<?> createClient(@Valid @RequestBody Client client, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> {
                errors.put(error.getField(), error.getDefaultMessage());
            });
            return ResponseEntity.badRequest().body(errors);
        }

        if (clientRepository.existsByPhoneNumber(client.getPhoneNumber())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("phoneNumber", "Номер уже используется"));
        }

        try {
            Client savedClient = clientRepository.save(client);
            return ResponseEntity.ok(savedClient);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.internalServerError()
                    .body("Ошибка сохранения данных");
        }
    }

    @GetMapping("/{clientId}")
    public ResponseEntity<Client> getClient(@PathVariable Long clientId) {
        return clientRepository.findById(clientId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{clientId}")
    public ResponseEntity<?> updateClient(
            @PathVariable Long clientId,
            @Valid @RequestBody Client updatedClient,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return getValidationErrors(bindingResult);
        }

        return clientRepository.findById(clientId)
                .map(client -> {
                    // Проверка уникальности номера
                    if (!client.getPhoneNumber().equals(updatedClient.getPhoneNumber())) {
                        if (clientRepository.existsByPhoneNumber(updatedClient.getPhoneNumber())) {
                            return ResponseEntity.status(HttpStatus.CONFLICT)
                                    .body("Номер телефона уже используется другим клиентом");
                        }
                    }

                    updateClientFields(client, updatedClient);
                    Client savedClient = clientRepository.save(client);
                    return ResponseEntity.ok(savedClient);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{clientId}")
    public ResponseEntity<String> deleteClient(@PathVariable Long clientId) {
        if (clientRepository.existsById(clientId)) {
            clientRepository.deleteById(clientId);
            return ResponseEntity.ok("Клиент успешно удален");
        }
        return ResponseEntity.notFound().build();
    }

    private void updateClientFields(Client existing, Client updated) {
        existing.setFirstName(updated.getFirstName());
        existing.setLastName(updated.getLastName());
        existing.setEmail(updated.getEmail());
        existing.setDateOfBirthday(updated.getDateOfBirthday());
        existing.setPhoneNumber(updated.getPhoneNumber());
    }

    private ResponseEntity<Map<String, String>> getValidationErrors(BindingResult bindingResult) {
        Map<String, String> errors = new HashMap<>();
        bindingResult.getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(errors);
    }
}