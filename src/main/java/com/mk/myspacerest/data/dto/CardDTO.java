package com.mk.myspacerest.data.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardDTO {

    private String internalCode;
    private String front;
    private String back;
    private String example;
    private int status;

    @Override
    public String toString() {
        return "CardDTO{" +
                "internalCode='" + internalCode + '\'' +
                ", front='" + front + '\'' +
                ", back='" + back + '\'' +
                ", example='" + example + '\'' +
                ", status=" + status +
                '}';
    }
}
