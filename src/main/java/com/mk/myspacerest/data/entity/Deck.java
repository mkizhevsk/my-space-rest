package com.mk.myspacerest.data.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "deck")
public class Deck {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private  int id;

    /**
     * Название колоды кард
     */
    private String name;

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
     * Потребитель
     */
    @OneToOne(fetch= FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE,CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name="username")
    private User user;

    /**
     * Признак удаления
     */
    private boolean deleted;

    /**
     * Карты
     */
    @OneToMany(fetch= FetchType.LAZY, cascade= CascadeType.ALL)
    @JoinColumn(name="deck_id")
    private List<Card> cards;

    @Override
    public String toString() {
        return "Deck{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", internalCode='" + internalCode + '\'' +
                ", editDateTime=" + editDateTime +
                ", user=" + user +
                ", deleted=" + deleted +
                ", cards=" + cards.size() +
                '}';
    }
}
