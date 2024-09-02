package com.mk.myspacerest.data.repository;

import com.mk.myspacerest.data.entity.Deck;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeckRepository extends CrudRepository<Deck, Integer> {

    @Query(value = "SELECT * FROM deck d WHERE d.username = ?1",
            nativeQuery = true)
    List<Deck> getDecksByUser(String username);
}
