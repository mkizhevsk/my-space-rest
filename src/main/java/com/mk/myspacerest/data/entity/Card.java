package com.mk.myspacerest.data.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "card")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * Уникальный код для синхронизации
     */
    @Column(name = "internal_code")
    private String internalCode;

    /**
     * Дата и время редактирования
     */
    @Column(name = "edit_date_time")
    private LocalDateTime editDateTime;

    /**
     * Лицевая сторона карты
     */
    private String front;

    /**
     * Обратная сторона карты
     */
    private String back;

    /**
     * Пример
     */
    private String example;

    /**
     * Состояние: 0 - не выучено, 1 - выучено
     */
    private int status;

    @Override
    public String toString() {
        return "Card{" +
                "id=" + id +
                ", internalCode='" + internalCode + '\'' +
                ", editDateTime=" + editDateTime +
                ", front='" + front + '\'' +
                ", back='" + back + '\'' +
                ", example='" + example + '\'' +
                ", status=" + status +
                '}';
    }
}
