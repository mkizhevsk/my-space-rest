package com.mk.myspacerest.controller;

import com.mk.myspacerest.data.dto.CardDTO;
import com.mk.myspacerest.data.entity.Card;
import com.mk.myspacerest.service.CardService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    private final Logger logger = LoggerFactory.getLogger(CardController.class);

    @GetMapping("/cards")
    public ResponseEntity<List<CardDTO>> getCards() {
        var cards = cardService.getCards();
        return ResponseEntity.ok(cards);
    }

    @GetMapping("/cards/{cardId}")
    public Card getCard(@PathVariable int cardId, Principal principal) {
        logger.debug("Start - getCard: cardId = {}, principal = {} ", cardId, principal);
        var card = cardService.getCard(cardId);
        logger.debug("End - getCard {}", card);
        return card;
    }

    @PostMapping("/cards")
    public ResponseEntity<Card> createCard(@RequestBody Card card, Principal principal) {
        logger.debug("Start - createCard: principal = {} ", principal);
        var createdCard = cardService.createCard(card);
        logger.debug("End - createCard {}", createdCard);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCard);
    }

    @PutMapping("/cards/{internalCode}")
    public ResponseEntity<Card> updateCard(@PathVariable String internalCode, @RequestBody CardDTO cardDTO) {
        var updatedCard = cardService.updateCard(internalCode, cardDTO);
        if (updatedCard != null) {
            return ResponseEntity.ok(updatedCard);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
