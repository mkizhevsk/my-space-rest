package com.mk.myspacerest.mapper;

import com.mk.myspacerest.data.dto.DeckDTO;
import com.mk.myspacerest.data.entity.Deck;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DeckMapper {

    Deck toDeck(DeckDTO deckDTO);

    DeckDTO toDeckDTO(Deck deck);

    List<DeckDTO> toDeckDTOs(List<Deck> decks);

    @Mapping(target = "name", source = "name")
    @Mapping(target = "editDateTime", source = "editDateTime")
    @Mapping(target = "internalCode", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "cards", ignore = true)
    void updateDeck(DeckDTO deckDTO, @MappingTarget Deck deck);
}
