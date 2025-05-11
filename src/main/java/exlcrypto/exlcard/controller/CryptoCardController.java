package exlcrypto.exlcard.controller;

import exlcrypto.exlcard.model.CryptoCard;
import exlcrypto.exlcard.service.CryptoCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cryptocards")
@RequiredArgsConstructor
public class CryptoCardController {

    private final CryptoCardService cryptoCardService;

    @PostMapping("/{clientId}")
    public ResponseEntity<CryptoCard> createCard(@PathVariable Long clientId) {
        CryptoCard card = cryptoCardService.createCardForClient(clientId);
        return ResponseEntity.ok()
                .header("X-CVV-Token", card.getCvvToken())
                .body(card);
    }

    @GetMapping("/cvv/{token}")
    public ResponseEntity<String> getCvv(@PathVariable String token) {
        return ResponseEntity.ok(cryptoCardService.getCvvByToken(token));
    }

    @GetMapping("/{clientId}")
    public ResponseEntity<List<CryptoCard>> getClientCards(@PathVariable Long clientId) {
        return ResponseEntity.ok(cryptoCardService.getClientCards(clientId));
    }

    @DeleteMapping("/{cardId}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long cardId) {
        cryptoCardService.deleteCard(cardId);
        return ResponseEntity.noContent().build();
    }

}