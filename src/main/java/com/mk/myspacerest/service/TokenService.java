package com.mk.myspacerest.service;

import com.mk.myspacerest.data.entity.RefreshToken;
import com.mk.myspacerest.data.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtEncoder encoder;
    private final JwtDecoder decoder;
    private final RefreshTokenRepository refreshTokenRepository;

    public String generateAccessToken(Authentication authentication) {

        var now = LocalDateTime.now();

        var scope = authentication.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .collect(Collectors.joining(" "));
        var claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now.toInstant(ZoneOffset.UTC))
                .expiresAt(now.plusHours(1).toInstant(ZoneOffset.UTC)) // Access token validity
                .subject(authentication.getName())
                .claim("scope", scope)
                .build();

        return this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public String generateRefreshToken(Authentication authentication) {

        var now = LocalDateTime.now();

        var claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now.toInstant(ZoneOffset.UTC))
                .expiresAt(now.plusDays(30).toInstant(ZoneOffset.UTC)) // Refresh token validity
                .subject(authentication.getName())
                .build();

        var refreshTokenValue = this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        refreshTokenRepository.save(RefreshToken.builder()
                .username(authentication.getName())
                .token(refreshTokenValue)
                .expiryDate(now.plusDays(30))
                .build());

        return refreshTokenValue;
    }

    public String refreshAccessToken(String refreshToken) {
        System.out.println("here " + refreshToken);
        var storedRefreshToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        var now = LocalDateTime.now();
        if (storedRefreshToken.getExpiryDate().isBefore(now))
            throw new RuntimeException("Refresh token expired");

        var receivedUsername = extractUsernameFromToken(refreshToken);
        if (!storedRefreshToken.getUsername().equals(receivedUsername))
            throw new RuntimeException("Invalid username in refresh token");

        var authentication = new UsernamePasswordAuthenticationToken(storedRefreshToken.getUsername(), null, Collections.emptyList());

        return generateAccessToken(authentication);
    }

    public String rotateRefreshToken(String refreshToken) {

        var storedRefreshToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));
        var now = LocalDateTime.now();
        if (storedRefreshToken.getExpiryDate().isBefore(now))
            throw new RuntimeException("Refresh token expired");


        var authentication = new UsernamePasswordAuthenticationToken(storedRefreshToken.getUsername(), null, Collections.emptyList());
        var newRefreshToken = generateRefreshToken(authentication);

        refreshTokenRepository.delete(storedRefreshToken);

        return newRefreshToken;
    }

    private String extractUsernameFromToken(String token) {
        Jwt jwt = decoder.decode(token);
        return jwt.getSubject();
    }
}
