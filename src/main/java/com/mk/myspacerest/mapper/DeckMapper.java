package com.mk.myspacerest.mapper;

import com.mk.myspacerest.data.dto.DeckDTO;
import com.mk.myspacerest.data.entity.Deck;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DeckMapper {

    Deck toDeck(DeckDTO deckDTO);

    DeckDTO toDeckDTO(Deck deck);

    List<DeckDTO> toDeckDTOs(List<Deck> decks);
}
