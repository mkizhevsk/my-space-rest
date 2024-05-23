package com.mk.myspacerest.data.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardDTO {

    private String front;
    private String back;
    private String example;
    private int status;

}
