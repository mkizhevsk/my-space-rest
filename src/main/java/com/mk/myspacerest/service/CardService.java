package com.mk.myspacerest.service;

import com.mk.myspacerest.data.dto.CardDTO;
import com.mk.myspacerest.data.entity.Card;
import com.mk.myspacerest.data.repository.CardRepository;
import com.mk.myspacerest.mapper.CardMapper;
import com.mk.myspacerest.utils.StringRandomGenerator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final CardMapper cardMapper;

    private final Logger logger = LoggerFactory.getLogger(CardService.class);

    public List<Card> getCards() {
        var customers = new ArrayList<Card>();
        cardRepository.findAll().forEach(customers::add);
        return customers;
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
    public Card updateCard(String internalCode, CardDTO updatedCardDTO) {
        var existingCardOptional = cardRepository.findByInternalCode(internalCode);
        if (existingCardOptional.isPresent()) {
            var existingCard = existingCardOptional.get();
            var updatedCard = cardMapper.toCard(updatedCardDTO);
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
}
