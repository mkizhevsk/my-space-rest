package com.mk.myspacerest.service;

import com.mk.myspacerest.data.dto.CardDTO;
import com.mk.myspacerest.data.entity.Card;
import com.mk.myspacerest.data.repository.CardRepository;
import com.mk.myspacerest.mapper.CardMapper;
import com.mk.myspacerest.utils.DateUtils;
import com.mk.myspacerest.utils.StringRandomGenerator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final CardMapper cardMapper;

    private final Logger logger = LoggerFactory.getLogger(CardService.class);

    public List<CardDTO> getCards() {
        var cards = (List<Card>) cardRepository.findAll();
        return cardMapper.toCardDTOs(cards);
    }

    @Transactional
    public Card createCard(Card card) {
        if (card.getInternalCode() == null) {
            card.setInternalCode(StringRandomGenerator.getInstance().getValue());
        }
        card.setEditDateTime(LocalDateTime.now());
        return cardRepository.save(card);
    }

    @Transactional
    public Card updateCard(String internalCode, CardDTO cardDTO) {
        var existingCardOptional = cardRepository.findByInternalCode(internalCode);
        if (existingCardOptional.isPresent()) {
            var existingCard = existingCardOptional.get();
            var updatedCard = cardMapper.toCard(cardDTO);
            updatedCard.setId(existingCard.getId());
            updatedCard.setEditDateTime(LocalDateTime.now());
            return cardRepository.save(updatedCard);
        }
        return null;
    }


    public Card getCard(int cardId) {
        return cardRepository.findById(cardId).orElse(null);
    }

    @Transactional
    public void deleteCard(int cardId) {
        cardRepository.deleteById(cardId);
    }

    private List<Card> getAllCards() {
        var cards = (List<Card>) cardRepository.findAll();
        return cards.stream()
                .filter(card -> card.getInternalCode() != null && !card.getInternalCode().isEmpty())
                .collect(Collectors.toList());
    }

    public List<CardDTO> syncCards(List<CardDTO> cardDTOs) {
        // Create a set of received internal codes for fast lookup
        var receivedInternalCodes = cardDTOs.stream()
                .map(CardDTO::getInternalCode)
                .collect(Collectors.toSet());

        // Get all cards from the database
        var allCards = getAllCards();
        var allCardDTOs = cardMapper.toCardDTOs(allCards);

        // Find cards that are in the database but not in the received list
        var missingOnMobile = allCardDTOs.stream()
                .filter(cardDTO -> !receivedInternalCodes.contains(cardDTO.getInternalCode()))
                .collect(Collectors.toList());

        // Sync the received cards (update existing or add new ones)
        for (var cardDTO : cardDTOs) {
            var existingCard = cardRepository.findByInternalCode(cardDTO.getInternalCode());
            if (existingCard.isPresent()) {
                var card = existingCard.get();
                var cardDTOEditDateTime = DateUtils.parseStringToLocalDateTime(cardDTO.getEditDateTime());

                // Check if the mobile card's editDateTime is newer than the web card's editDateTime
                if (cardDTOEditDateTime.isAfter(card.getEditDateTime())) {
                    cardMapper.updateCard(cardDTO, card);
                    card.setEditDateTime(cardDTOEditDateTime);
                    cardRepository.save(card);
                }
            } else {
                var newCard = cardMapper.toCard(cardDTO);
                newCard.setEditDateTime(LocalDateTime.now());
                cardRepository.save(newCard);
            }
        }

        return missingOnMobile;
    }

}
