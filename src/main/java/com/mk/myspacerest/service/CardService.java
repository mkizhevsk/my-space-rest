package com.mk.myspacerest.service;

import com.mk.myspacerest.data.dto.CardDTO;
import com.mk.myspacerest.data.dto.DeckDTO;
import com.mk.myspacerest.data.entity.Card;
import com.mk.myspacerest.data.repository.CardRepository;
import com.mk.myspacerest.data.repository.DeckRepository;
import com.mk.myspacerest.mapper.CardMapper;
import com.mk.myspacerest.mapper.DeckMapper;
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
    private final DeckRepository deckRepository;
    private final DeckMapper deckMapper;

    private final Logger logger = LoggerFactory.getLogger(CardService.class);

    public List<DeckDTO> getDecksByUser(String username) {
        var decks = deckRepository.getDecksByUser(username);
        return deckMapper.toDeckDTOs(decks);
    }

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

    private List<Card> getAllCards() {
        var cards = (List<Card>) cardRepository.findAll();
        return cards.stream()
                .filter(card -> card.getInternalCode() != null && !card.getInternalCode().isEmpty())
                .collect(Collectors.toList());
    }

    public List<CardDTO> syncCards(List<CardDTO> cardDTOs) {

        var resultFromMobileCards = processMobileCards(cardDTOs);
        logger.info("updated: " + resultFromMobileCards[0] + " | created: " + resultFromMobileCards[1]);

        var cardsForMobile = getCardsForMobile(cardDTOs);
        logger.info("cards for mobile: " + cardsForMobile);

        return cardsForMobile;
    }

    private int[] processMobileCards(List<CardDTO> cardDTOs) {
        var result = new int[] { 0, 0};
        for (var cardDTO : cardDTOs) {
            var existingCard = cardRepository.findByInternalCode(cardDTO.getInternalCode());
            if (existingCard.isPresent()) {
                var card = existingCard.get();
                var cardDTOEditDateTime = DateUtils.parseStringToLocalDateTime(cardDTO.getEditDateTime());

                if (cardDTOEditDateTime.isAfter(card.getEditDateTime())) {
                    cardMapper.updateCard(cardDTO, card);
                    card.setEditDateTime(cardDTOEditDateTime);
                    card.setDeleted(false);
                    cardRepository.save(card);
                    result[0]++;
                }
            } else {
                var newCard = cardMapper.toCard(cardDTO);
                cardRepository.save(newCard);
                result[1]++;
            }
        }
        return result;
    }

    private List<CardDTO> getCardsForMobile(List<CardDTO> cardDTOs) {

        var receivedInternalCodes = cardDTOs.stream()
                .map(CardDTO::getInternalCode)
                .collect(Collectors.toSet());

        var allCards = getAllCards();
        var allCardDTOs = cardMapper.toCardDTOs(allCards);

        return allCardDTOs.stream()
                .filter(cardDTO -> !receivedInternalCodes.contains(cardDTO.getInternalCode()) || cardDTO.isDeleted())
                .collect(Collectors.toList());
    }

}
