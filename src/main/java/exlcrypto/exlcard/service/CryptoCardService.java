package exlcrypto.exlcard.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import exlcrypto.exlcard.exception.InvalidTokenException;
import exlcrypto.exlcard.exception.ResourceNotFoundException;
import exlcrypto.exlcard.model.Client;
import exlcrypto.exlcard.model.CryptoCard;
import exlcrypto.exlcard.repository.ClientRepository;
import exlcrypto.exlcard.repository.CryptoCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class CryptoCardService {

    private final CryptoCardRepository cryptoCardRepository;
    private final ClientRepository clientRepository;

    // Кэш для хранения CVV (токен -> CVV)
    private final Cache<String, String> cvvCache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .maximumSize(1000)
            .build();

    public CryptoCard createCardForClient(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));

        CryptoCard card = generateCard(client);

        if (card.getCvv() == null || card.getCvv().isBlank()) {
            throw new IllegalStateException("CVV generation failed");
        }

        storeCvvInCache(card);
        return saveMaskedCard(card);
    }

    public String getCvvByToken(String token) {
        String cvv = cvvCache.getIfPresent(token);
        if (cvv == null) {
            throw new InvalidTokenException("CVV token is invalid or expired");
        }
        cvvCache.invalidate(token);
        return cvv;
    }

    public List<CryptoCard> getClientCards(Long clientId) {
        return cryptoCardRepository.findByClient_ClientId(clientId)
                .stream()
                .map(this::maskCardData)
                .toList();
    }

    public void deleteCard(Long cardId) {
        cryptoCardRepository.deleteById(cardId);
    }

    private CryptoCard generateCard(Client client) {
        CryptoCard card = new CryptoCard();
        card.setCryptoCardNumber(generateCardNumber());
        card.setExpiryDate(generateExpiryDate());
        card.setCvv(generateCVV());
        card.setClient(client);
        return card;
    }

    private void storeCvvInCache(CryptoCard card) {
        String token = UUID.randomUUID().toString();
        cvvCache.put(token, card.getCvv());
        card.setCvvToken(token);
    }

    private CryptoCard saveMaskedCard(CryptoCard card) {
        CryptoCard maskedCard = maskCardData(card);
//        maskedCard.setCvv(null); // Не сохраняем CVV в БД
        return cryptoCardRepository.save(maskedCard);
    }

    private String generateCardNumber() {
        // Генерация 16 цифр без пробелов
        return String.format("%016d", ThreadLocalRandom.current().nextLong(1_0000_0000_0000_0000L, 9_9999_9999_9999_9999L));
    }

    private String generateExpiryDate() {
        LocalDate expiry = LocalDate.now().plusYears(3);
        return String.format("%02d/%02d", expiry.getMonthValue(), expiry.getYear() % 100);
    }

    private String generateCVV() {
        return String.format("%03d", ThreadLocalRandom.current().nextInt(0, 999));
    }

    private CryptoCard maskCardData(CryptoCard card) {
        String number = card.getCryptoCardNumber();
        if (number != null && number.length() == 16) {
            card.setCryptoCardNumber(number.substring(0, 4) + " **** **** " + number.substring(12));
        }
        return card;
    }
}