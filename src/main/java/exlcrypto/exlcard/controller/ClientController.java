package exlcrypto.exlcard.controller;

import exlcrypto.exlcard.model.Client;
import exlcrypto.exlcard.repository.ClientRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

        Client savedClient = clientRepository.save(client);
        return ResponseEntity.ok(savedClient);
    }

    @GetMapping("/{clientId}")
    public ResponseEntity<Client> getClient(@PathVariable Long clientId) {
        Optional<Client> client = clientRepository.findById(clientId);
        return client.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{clientId}")
    public ResponseEntity<Client> updateClient(
            @PathVariable Long clientId,
            @RequestBody Client updatedClient
    ) {
        return clientRepository.findById(clientId)
                .map(client -> {
                    client.setFirstName(updatedClient.getFirstName());
                    client.setEmail(updatedClient.getEmail());
                    client.setLastName(updatedClient.getLastName());
                    client.setDateOfBirthday(updatedClient.getDateOfBirthday());
                    clientRepository.save(client);
                    return ResponseEntity.ok(client);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{clientId}")
    public ResponseEntity<String> deleteClient(@PathVariable Long clientId) {
        if (clientRepository.existsById(clientId)) {
            clientRepository.deleteById(clientId);
            return ResponseEntity.ok("Client deleted");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}