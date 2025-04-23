package exlcrypto.exlcard.service;

import exlcrypto.exlcard.dto.CryptoCardRequest;
import exlcrypto.exlcard.model.Client;
import exlcrypto.exlcard.model.CryptoCard;
import exlcrypto.exlcard.repository.ClientRepository;
import exlcrypto.exlcard.repository.CryptoCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CryptoCardService {
    private final CryptoCardRepository cryptoCardRepository;
    private final ClientRepository clientRepository;

    public CryptoCard createCard(CryptoCardRequest request, Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        CryptoCard card = new CryptoCard();
        card.setCryptoCardNumber(request.getCryptoCardNumber());
        card.setExpiryDate(request.getExpiryDate());
        card.setCvv(request.getCvv());
        card.setClient(client);

        CryptoCard savedCard = cryptoCardRepository.save(card);
        return maskCardData(savedCard);
    }

    public List<CryptoCard> getCardsByClient(Long clientId) {
        return cryptoCardRepository.findByClientId(clientId)
                .stream()
                .map(this::maskCardData)
                .toList();
    }

    private CryptoCard maskCardData(CryptoCard card) {
        String maskedNumber = "**** **** **** " + card.getCryptoCardNumber().substring(12);
        card.setCryptoCardNumber(maskedNumber);
        card.setCvv(null); // Удаляем CVV из ответа
        return card;
    }
}
