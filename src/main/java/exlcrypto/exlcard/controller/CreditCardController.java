package exlcrypto.exlcard.controller;

import exlcrypto.exlcard.dto.CreditCardRequest;
import exlcrypto.exlcard.model.CreditCard;
import exlcrypto.exlcard.service.CreditCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CreditCardController {
    private final CreditCardService creditCardService;

    @PostMapping("/{clientId}")
    public CreditCard addCard(
            @PathVariable Long clientId,
            @RequestBody CreditCardRequest request
    ) {
        return creditCardService.createCard(request, clientId);
    }

    @GetMapping("/{clientId}")
    public List<CreditCard> getCards(@PathVariable Long clientId) {
        return creditCardService.getCardsByClient(clientId);
    }
}