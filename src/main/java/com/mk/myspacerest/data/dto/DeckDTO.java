package com.mk.myspacerest.data.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeckDTO {

    private String name;
    private String internalCode;
    private String editDateTime;
    private List<CardDTO> cards;
    private boolean deleted;

    @Override
    public String toString() {
        return "DeckDTO{" +
                "name='" + name + '\'' +
                ", internalCode='" + internalCode + '\'' +
                ", editDateTime='" + editDateTime + '\'' +
                ", cards=" + cards.size() +
                ", deleted=" + deleted +
                '}';
    }
}
