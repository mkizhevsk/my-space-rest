package com.mk.myspacerest.data.repository;

import com.mk.myspacerest.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {

    User getUserByUsername(String username);
}
