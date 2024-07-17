package com.mk.myspacerest.data.repository;

import com.mk.myspacerest.data.entity.Authority;
import com.mk.myspacerest.data.entity.AuthorityId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, AuthorityId> {
}
