package exlcrypto.exlcard.controller;

import exlcrypto.exlcard.model.CryptoCard;
import exlcrypto.exlcard.service.CryptoCardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/cryptocards")
@RequiredArgsConstructor
@Slf4j
public class CryptoCardController {

    private final CryptoCardService cryptoCardService;

    @PostMapping("/{clientId}")
    public ResponseEntity<CryptoCard> addCard(@PathVariable Long clientId) {
        try {
            CryptoCard card = cryptoCardService.createCardForClient(clientId);
            log.info("Created new card for client ID: {}", clientId);
            return ResponseEntity.ok(card);
        } catch (ResponseStatusException ex) {
            log.error("Error creating card for client {}: {}", clientId, ex.getReason());
            throw ex;
        }
    }

    @GetMapping("/{clientId}")
    public ResponseEntity<List<CryptoCard>> getCardsByClient(@PathVariable Long clientId) {
        List<CryptoCard> cards = cryptoCardService.getAllClientCards(clientId);
        return ResponseEntity.ok(cards);
    }

    @GetMapping("/card/{cardId}")
    public ResponseEntity<CryptoCard> getCardById(@PathVariable Long cardId) {
        return cryptoCardService.getCardDetails(cardId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/{cardId}")
    public ResponseEntity<String> updateCard(
            @PathVariable Long cardId,
            @RequestParam(required = false) String expiryDate,
            @RequestParam(required = false) String cvv) {
        return cryptoCardService.updateCardDetails(cardId, expiryDate, cvv)
                .map(updated -> ResponseEntity.ok("Card updated"))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{cardId}")
    public ResponseEntity<String> deleteCard(@PathVariable Long cardId) {
        return cryptoCardService.removeCard(cardId)
                ? ResponseEntity.ok("Card deleted")
                : ResponseEntity.notFound().build();
    }
}

//    private String generateCardNumber() {
//        Random random = new Random();
//        return "4440" +
//                String.format("%04d", random.nextInt(10000)) +
//                String.format("%04d", random.nextInt(10000)) +
//                String.format("%04d", random.nextInt(10000));
//    }
//
//
//    private String generateExpiryDate() {
//        LocalDate expiry = LocalDate.now().plusYears(3);
//        return String.format("%02d/%02d", expiry.getMonthValue(), expiry.getYear() % 100);
//    }
//
//    private String generateCVV() {
//        return String.format("%04d", new Random().nextInt(1000));
//    }
