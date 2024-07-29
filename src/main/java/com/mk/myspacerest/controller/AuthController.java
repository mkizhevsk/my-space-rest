package com.mk.myspacerest.controller;

import com.mk.myspacerest.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private static final Logger LOG = LoggerFactory.getLogger(AuthController.class);

    private final TokenService tokenService;

    @PostMapping("/token")
    public String token(Authentication authentication) {
        LOG.info("Token requested for user: '{}'", authentication.getName());
        String token = tokenService.generateToken(authentication);
        LOG.info("Token granted {}", token);
        return token;
    }

    @PostMapping("/refresh-token")
    public String refreshToken(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        String newToken = tokenService.refreshToken(token);
        LOG.info("Token refreshed {}", newToken);
        return newToken;
    }
}
