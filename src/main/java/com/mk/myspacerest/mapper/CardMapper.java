package com.mk.myspacerest.mapper;

import com.mk.myspacerest.data.dto.CardDTO;
import com.mk.myspacerest.data.entity.Card;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CardMapper {

    @Mapping(target = "id", ignore = true)
    Card toCard(CardDTO cardDTO);

    List<CardDTO> toCardDTOs(List<Card> cards);
}