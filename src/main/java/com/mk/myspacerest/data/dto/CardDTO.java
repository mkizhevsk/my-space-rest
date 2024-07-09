package com.mk.myspacerest.data.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardDTO {

    private String internalCode;
    private String editDateTime;
    private String front;
    private String back;
    private String example;
    private int status;
    private boolean deleted;

    @Override
    public String toString() {
        return "CardDTO{" +
                "internalCode='" + internalCode + '\'' +
                ", editDateTime='" + editDateTime + '\'' +
                ", front='" + front + '\'' +
                ", back='" + back + '\'' +
                ", example='" + example + '\'' +
                ", status=" + status +
                ", deleted=" + deleted +
                '}';
    }
}
