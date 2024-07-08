package com.mk.myspacerest.mapper;

import com.mk.myspacerest.data.dto.CardDTO;
import com.mk.myspacerest.data.entity.Card;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CardMapper {

    Card toCard(CardDTO cardDTO);

    CardDTO toCardDTO(Card card);

    List<CardDTO> toCardDTOs(List<Card> cards);

    @Mapping(target = "editDateTime", ignore = true)
    @Mapping(target = "internalCode", ignore = true)
    void updateCard(CardDTO cardDTO, @MappingTarget Card card);

}