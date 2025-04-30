package exlcrypto.exlcard.controller;

import exlcrypto.exlcard.dto.CryptoCardRequest;
import exlcrypto.exlcard.model.CryptoCard;
import exlcrypto.exlcard.repository.ClientRepository;
import exlcrypto.exlcard.service.CryptoCardService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/api/cryptocards")
@Slf4j
public class CryptoCardController {

    private final CryptoCardService cryptoCardService;
    private final ClientRepository clientRepository;

    @Autowired
    public CryptoCardController(CryptoCardService cryptoCardService,
                                ClientRepository clientRepository) {
        this.cryptoCardService = cryptoCardService;
        this.clientRepository = clientRepository;
    }

    @PostMapping("/{clientId}")
    public ResponseEntity<CryptoCard> addCard(@PathVariable Long clientId) {
        return clientRepository.findById(clientId)
                .map(client -> {
                    CryptoCardRequest request = new CryptoCardRequest();
                    request.setCryptoCardNumber(generateCardNumber());
                    request.setExpiryDate(generateExpiryDate());
                    request.setCvv(generateCVV());
                    return ResponseEntity.ok(cryptoCardService.createCard(request, clientId));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{clientId}")
    public ResponseEntity<List<CryptoCard>> getCardsByClient(@PathVariable Long clientId) {
        return ResponseEntity.ok(cryptoCardService.getCardsByClient(clientId));
    }

    @GetMapping("/card/{cardId}")
    public ResponseEntity<CryptoCard> getCardById(@PathVariable Long cardId) {
        return cryptoCardService.getCardById(cardId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/{cardId}")
    public ResponseEntity<String> updateCard(
            @PathVariable Long cardId,
            @RequestBody @Valid CryptoCardRequest request
    ) {
        return cryptoCardService.updateCard(cardId, request)
                .map(updated -> ResponseEntity.ok("Card updated"))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{cardId}")
    public ResponseEntity<String> deleteCard(@PathVariable Long cardId) {
        return cryptoCardService.deleteCard(cardId)
                ? ResponseEntity.ok("Card deleted")
                : ResponseEntity.notFound().build();
    }

    private String generateCardNumber() {
        Random random = new Random();
        return "4440" +
                String.format("%04d", random.nextInt(10000)) +
                String.format("%04d", random.nextInt(10000)) +
                String.format("%04d", random.nextInt(10000));
    }


    private String generateExpiryDate() {
        LocalDate expiry = LocalDate.now().plusYears(3);
        return String.format("%02d/%02d", expiry.getMonthValue(), expiry.getYear() % 100);
    }

    private String generateCVV() {
        return String.format("%04d", new Random().nextInt(1000));
    }
}