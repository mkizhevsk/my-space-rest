package com.mk.myspacerest.service;

import com.mk.myspacerest.data.dto.CardDTO;
import com.mk.myspacerest.data.dto.DeckDTO;
import com.mk.myspacerest.data.entity.Card;
import com.mk.myspacerest.data.entity.Deck;
import com.mk.myspacerest.data.repository.CardRepository;
import com.mk.myspacerest.data.repository.DeckRepository;
import com.mk.myspacerest.data.repository.UserRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final CardMapper cardMapper;
    private final DeckRepository deckRepository;
    private final DeckMapper deckMapper;
    private final UserRepository userRepository;

    private final Logger logger = LoggerFactory.getLogger(CardService.class);

    public List<DeckDTO> getDeckDTOsByUser(String username) {
        var decks = deckRepository.getDecksByUser(username);
        return deckMapper.toDeckDTOs(decks);
    }

    public List<Deck> getDeckByUser(String username) {
        return deckRepository.getDecksByUser(username);
    }

    public Deck getDeckByInternalCode(String internalCode) {
        return deckRepository.getByInternalCode(internalCode);
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

    public List<DeckDTO> syncCards(List<DeckDTO> mobileDeckDTOs, String username) {
        logger.info("syncCards - start: mobileDeckDTOs = {}, userName = {}", mobileDeckDTOs.size(), username);
        var deckDTOsForMobile = getDecksForMobile(mobileDeckDTOs, username);
        logger.info("syncCards - end: deckDTOsForMobile = {}", deckDTOsForMobile.size());
        return deckDTOsForMobile;
    }

    private List<DeckDTO> getDecksForMobile(List<DeckDTO> mobileDeckDTOs, String username) {
        var deckDTOsForMobile = new ArrayList<DeckDTO>();

        for (var mobileDeckDTO : mobileDeckDTOs) {
            processMobileDeckDTO(mobileDeckDTO, username);

            var cards = getCardsForMobile(mobileDeckDTO.getCards());

            var deckForMobile = getDeckByInternalCode(mobileDeckDTO.getInternalCode());
            deckForMobile.setCards(cards);

            var deckDTOForMobile = deckMapper.toDeckDTO(deckForMobile);
            deckDTOsForMobile.add(deckDTOForMobile);
        }

        addNewWebDecks(mobileDeckDTOs, deckDTOsForMobile, username);

        return deckDTOsForMobile;
    }

    private void processMobileDeckDTO(DeckDTO mobileDeckDTO, String username) {
        var webDeck = deckRepository.findByInternalCode(mobileDeckDTO.getInternalCode());
        if (webDeck.isPresent()) {
            var deck = webDeck.get();
            var deckDTOEditDateTime = DateUtils.parseStringToLocalDateTime(mobileDeckDTO.getEditDateTime());

            if (deckDTOEditDateTime.isAfter(deck.getEditDateTime())) {
                deckMapper.updateDeck(mobileDeckDTO, deck);
                deckRepository.save(deck);
            }
        } else {
            var newDeck = deckMapper.toDeck(mobileDeckDTO);
            var user = userRepository.getUserByUsername(username);
            newDeck.setUser(user);
            deckRepository.save(newDeck);
        }
    }

    private List<Card> getCardsForMobile(List<CardDTO> mobileCardDTOS) {
        var cardsForMobile = new ArrayList<Card>();

        for (var mobileCardDTO : mobileCardDTOS) {
            var webCardOptional = cardRepository.findByInternalCode(mobileCardDTO.getInternalCode());
            if (webCardOptional.isPresent()) {
                var webCard = webCardOptional.get();
                var mobileCardDTOEditDateTime = DateUtils.parseStringToLocalDateTime(mobileCardDTO.getEditDateTime());

                if (mobileCardDTOEditDateTime.isAfter(webCard.getEditDateTime())) {
                    cardMapper.updateCard(mobileCardDTO, webCard);
                    webCard.setEditDateTime(mobileCardDTOEditDateTime);
                    webCard.setDeleted(false);
                    cardRepository.save(webCard);
                } else {
                    cardsForMobile.add(webCard);
                }
            } else {
                var newCard = cardMapper.toCard(mobileCardDTO);
                cardRepository.save(newCard);
            }
        }

        return cardsForMobile;
    }

    private void addNewWebDecks(List<DeckDTO> mobileDeckDTOs, List<DeckDTO> deckDTOsForMobile, String username) {
        // Get all web decks for the user
        var webDeckDTOs = getDeckDTOsByUser(username);

        // Find decks not present in mobileDeckDTOs
        var mobileDeckInternalCodes = mobileDeckDTOs.stream()
                .map(DeckDTO::getInternalCode)
                .collect(Collectors.toSet());

        for (var webDeckDTO : webDeckDTOs) {
            if (!mobileDeckInternalCodes.contains(webDeckDTO.getInternalCode())) {
                deckDTOsForMobile.add(webDeckDTO);
            }
        }
    }
}
