package exlcrypto.exlcard.controller;

import exlcrypto.exlcard.dto.CryptoCardRequest;
import exlcrypto.exlcard.model.CryptoCard;
import exlcrypto.exlcard.model.Client;
import exlcrypto.exlcard.repository.ClientRepository;
import exlcrypto.exlcard.repository.CryptoCardRepository;
import exlcrypto.exlcard.service.CryptoCardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/api/cards")
@Slf4j
@RequiredArgsConstructor
public class CryptoCardController {
    private final CryptoCardService cryptoCardService;
    private final ClientRepository clientRepository;
    private final CryptoCardRepository cryptoCardRepository;

    // Create (Post)
    @PostMapping("/{clientId}")
    public ResponseEntity<CryptoCard> addCard(@PathVariable Long clientId) {
        Optional<Client> clientOptional = clientRepository.findById(clientId);
        if (clientOptional.isPresent()) {
            CryptoCard card = generateCryptoCard();
            CryptoCard savedCard = cryptoCardRepository.save(card);
            return ResponseEntity.ok(savedCard);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Read (Get)
    @GetMapping("/{clientId}")
    public ResponseEntity<List<CryptoCard>> getCards(@PathVariable Long clientId) {
        List<CryptoCard> cards = cryptoCardService.getCardsByClient(clientId);
        return ResponseEntity.ok(cards);
    }

    @GetMapping("/card/{cryptoCardId}")
    public ResponseEntity<CryptoCard> getCardById(@PathVariable Long cryptoCardId) {
        Optional<CryptoCard> card = cryptoCardRepository.findById(cryptoCardId);
        if (card.isPresent()) {
            return ResponseEntity.ok(card.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Update (Patch)
    @PatchMapping("/update/{cryptoCardId}")
    public ResponseEntity<String> updateCard(
            @PathVariable Long cryptoCardId,
            @RequestBody CryptoCardRequest request
    ) {
        Optional<CryptoCard> optionalCard = cryptoCardRepository.findById(cryptoCardId);
        if (optionalCard.isPresent()) {
            CryptoCard card = optionalCard.get();
            card.setCryptoCardNumber(request.getCryptoCardNumber());
            card.setExpiryDate(request.getExpiryDate());
            card.setCvv(request.getCvv());
            cryptoCardRepository.save(card);
            return ResponseEntity.ok("Card updated");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete
    @DeleteMapping("/delete/{cryptoCardId}")
    public ResponseEntity<String> deleteCard(@PathVariable Long cryptoCardId) {
        if (cryptoCardRepository.existsById(cryptoCardId)) {
            cryptoCardRepository.deleteById(cryptoCardId);
            return ResponseEntity.ok("Card deleted");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Generate Crypto Card
    private CryptoCard generateCryptoCard() {
        CryptoCard card = new CryptoCard();
        card.setCryptoCardNumber(generateCardNumber());
        card.setExpiryDate(generateExpiryDate());
        card.setCvv(generateCVV());
        return card;
    }

    private String generateCardNumber() {
        Random random = new Random();
        StringBuilder cryptoCardNumber = new StringBuilder("4440"); // Prefix for crypto card
        for (int i = 0; i < 12; i++) {
            cryptoCardNumber.append(random.nextInt(10));
        }
        return cryptoCardNumber.toString();
    }

    private String generateExpiryDate() {
        LocalDate date = LocalDate.now().plusYears(3);
        return String.format("%02d/%02d", date.getMonthValue(), date.getYear() % 100);
    }

    private String generateCVV() {
        Random random = new Random();
        int cvv = 100 + random.nextInt(900); // Generate a 3-digit CVV
        return String.valueOf(cvv);
    }
}
