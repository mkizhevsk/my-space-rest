package com.mk.myspacerest.data.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeckDTO {

    private String internalCode;
    private String editDateTime;
    private List<CardDTO> cards;
    private boolean deleted;
}
