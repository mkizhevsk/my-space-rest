package com.mk.myspacerest.mapper;

import com.mk.myspacerest.data.dto.CardDTO;
import com.mk.myspacerest.data.entity.Card;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CardMapper {

    Card toCard(CardDTO cardDTO);

    CardDTO toCardDTO(Card card);

    List<CardDTO> toCardDTOs(List<Card> cards);
}