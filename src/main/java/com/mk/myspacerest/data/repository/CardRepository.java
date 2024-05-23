package com.mk.myspacerest.data.repository;

import com.mk.myspacerest.data.entity.Card;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends CrudRepository<Card, Integer> {

    @Query(value = "SELECT * FROM card c",
            nativeQuery = true)
    List<Card> getCards();

    Optional<Card> findByInternalCode(String internalCode);
}
