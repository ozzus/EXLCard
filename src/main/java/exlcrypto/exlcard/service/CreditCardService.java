package exlcrypto.exlcard.service;

import exlcrypto.exlcard.dto.CreditCardRequest;
import exlcrypto.exlcard.model.Client;
import exlcrypto.exlcard.model.CreditCard;
import exlcrypto.exlcard.repository.ClientRepository;
import exlcrypto.exlcard.repository.CreditCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CreditCardService {
    private final CreditCardRepository creditCardRepository;
    private final ClientRepository clientRepository;

    public CreditCard createCard(CreditCardRequest request, Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        CreditCard card = new CreditCard();
        card.setCreditCardNumber(request.getCreditCardNumber());
        card.setExpiryDate(request.getExpiryDate());
        card.setCvv(request.getCvv());

        CreditCard savedCard = creditCardRepository.save(card);
        return maskCardData(savedCard);
    }

    public List<CreditCard> getCardsByClient(Long clientId) {
        return creditCardRepository.findByClientId(clientId)
                .stream()
                .map(this::maskCardData)
                .toList();
    }

    private CreditCard maskCardData(CreditCard card) {
        String maskedNumber = "**** **** **** " + card.getCreditCardNumber().substring(12);
        card.setCreditCardNumber(maskedNumber);
        card.setCvv(null); // Удаляем CVV из ответа
        return card;
    }
}