package com.mk.myspacerest.data.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "authorities")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Authority {

    @EmbeddedId
    private AuthorityId id;

    @ManyToOne
    @MapsId("username")
    @JoinColumn(name = "username", insertable = false, updatable = false)
    private User user;

}
