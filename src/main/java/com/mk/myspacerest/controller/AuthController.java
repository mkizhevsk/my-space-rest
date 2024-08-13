package com.mk.myspacerest.controller;

import com.mk.myspacerest.service.TokenService;
import com.mk.myspacerest.service.VerificationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final TokenService tokenService;
    private final VerificationService verificationService;

    private static final Logger LOG = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/request-code")
    public Map<String, String> requestCode(@RequestParam String username) {
        LOG.info("Start requestCode username: {}", username);
        verificationService.sendVerificationCode(username);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Verification code sent to the provided email address.");
        return response;
    }

    @PostMapping("/process-code")
    public Map<String, String> processCode(@RequestParam String username, @RequestParam String code) {
        LOG.info("Start processCode username: {}, code: {}", username, code);
        if (verificationService.verifyCode(username, code)) {
            Authentication authentication = new UsernamePasswordAuthenticationToken(username, null);
            String accessToken = tokenService.generateAccessToken(authentication);
            String refreshToken = tokenService.generateRefreshToken(authentication);

            var tokens = new HashMap<String, String>();
            tokens.put("accessToken", accessToken);
            tokens.put("refreshToken", refreshToken);
            LOG.info("Tokens created {}", tokens);
            return tokens;
        } else {
            throw new RuntimeException("Invalid or expired verification code.");
        }
    }

    @PostMapping("/refresh-token")
    public Map<String, String> refreshToken(@RequestBody Map<String, String> request) {
        LOG.info("Refresh token requested");
        String refreshToken = request.get("refreshToken");
        var newAccessToken = tokenService.refreshAccessToken(refreshToken);
        var newRefreshToken = tokenService.rotateRefreshToken(refreshToken);

        var tokens = new HashMap<String, String>();
        tokens.put("accessToken", newAccessToken);
        tokens.put("refreshToken", newRefreshToken);
        LOG.info("Tokens refreshed {}", tokens);
        return tokens;
    }
}
