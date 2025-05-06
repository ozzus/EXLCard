package exlcrypto.exlcard.service;

import exlcrypto.exlcard.model.Client;
import exlcrypto.exlcard.model.CryptoCard;
import exlcrypto.exlcard.repository.ClientRepository;
import exlcrypto.exlcard.repository.CryptoCardRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class CryptoCardService {

    private static final String CARD_PREFIX = "4440";
    private static final int CARD_NUMBER_LENGTH = 16;
    private static final int CVV_LENGTH = 3;
    private static final int CARD_VALID_YEARS = 3;

    private final CryptoCardRepository cryptoCardRepository;
    private final ClientRepository clientRepository;

    public CryptoCard createCardForClient(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Клиент с ID " + clientId + " не найден"
                ));

        CryptoCard card = new CryptoCard();
        card.setCryptoCardNumber(generateCardNumber());
        card.setExpiryDate(generateExpiryDate());
        card.setCvv(generateCVV());
        card.setClient(client);

        return maskCardData(cryptoCardRepository.save(card));
    }

    public Optional<CryptoCard> updateCardDetails(Long cardId, String expiryDate, String cvv) {
        return cryptoCardRepository.findById(cardId)
                .map(card -> {
                    if (expiryDate != null) {
                        card.setExpiryDate(expiryDate);
                    }
                    if (cvv != null) {
                        card.setCvv(cvv);
                    }
                    return maskCardData(cryptoCardRepository.save(card));
                });
    }

    public Optional<CryptoCard> getCardDetails(Long cardId) {
        return cryptoCardRepository.findById(cardId)
                .map(this::maskCardData);
    }

    public boolean removeCard(Long cardId) {
        if (cryptoCardRepository.existsById(cardId)) {
            cryptoCardRepository.deleteById(cardId);
            return true;
        }
        return false;
    }

    public List<CryptoCard> getAllClientCards(Long clientId) {
        return cryptoCardRepository.findByClient_ClientId(clientId)
                .stream()
                .map(this::maskCardData)
                .toList();
    }

    private String generateCardNumber() {
        return CARD_PREFIX +
                generateRandomDigits(4) +
                generateRandomDigits(4) +
                generateRandomDigits(4);
    }

    private String generateExpiryDate() {
        LocalDate expiry = LocalDate.now().plusYears(CARD_VALID_YEARS);
        return String.format("%02d/%02d", expiry.getMonthValue(), expiry.getYear() % 100);
    }

    private String generateCVV() {
        return generateRandomDigits(CVV_LENGTH);
    }

    private String generateRandomDigits(int length) {
        int min = (int) Math.pow(10, length - 1);
        int max = (int) Math.pow(10, length);
        return String.valueOf(ThreadLocalRandom.current().nextInt(min, max));
    }

    private CryptoCard maskCardData(CryptoCard card) {
        CryptoCard masked = new CryptoCard();
        masked.setCryptoCardId(card.getCryptoCardId());
        masked.setExpiryDate(card.getExpiryDate());
        String fullNumber = card.getCryptoCardNumber();
        if (fullNumber != null && fullNumber.length() == 16) {
            masked.setCryptoCardNumber("**** **** **** " + fullNumber.substring(12));
        }
        masked.setCvv("***");
//        masked.setClient(null); // Убираем связь с клиентом
        return masked;
    }
}