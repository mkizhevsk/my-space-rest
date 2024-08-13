package com.mk.myspacerest.controller;

import com.mk.myspacerest.constants.ErrorCodes;
import com.mk.myspacerest.data.security.verification.VerificationService;
import com.mk.myspacerest.exception.ErrorResponse;
import com.mk.myspacerest.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?>  processCode(@RequestParam String username, @RequestParam String code) {
        LOG.info("Start processCode username: {}, code: {}", username, code);

        var verificationResult = verificationService.verifyCode(username, code);

        if (verificationResult.isSuccessful()) {
            Authentication authentication = new UsernamePasswordAuthenticationToken(username, null);
            String accessToken = tokenService.generateAccessToken(authentication);
            String refreshToken = tokenService.generateRefreshToken(authentication);

            var tokens = new HashMap<String, String>();
            tokens.put("accessToken", accessToken);
            tokens.put("refreshToken", refreshToken);
            LOG.info("Tokens created {}", tokens);
            return ResponseEntity.ok(tokens);
        } else {
            ErrorResponse errorResponse;

            if (verificationResult.isCodeInvalid()) {
                errorResponse = new ErrorResponse(ErrorCodes.INVALID_CODE, "The verification code you entered is invalid.");
                return ResponseEntity.badRequest().body(errorResponse);
            } else if (verificationResult.isCodeExpired()) {
                errorResponse = new ErrorResponse(ErrorCodes.EXPIRED_CODE, "The verification code has expired.");
                return ResponseEntity.badRequest().body(errorResponse);
            } else {
                errorResponse = new ErrorResponse(ErrorCodes.UNKNOWN_ERROR, "An unknown error occurred.");
                return ResponseEntity.badRequest().body(errorResponse);
            }
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
