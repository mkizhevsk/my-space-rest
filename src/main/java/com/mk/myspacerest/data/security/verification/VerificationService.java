package com.mk.myspacerest.data.security.verification;

import com.mk.myspacerest.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class VerificationService {

    private final EmailService emailService;

    private final Map<String, CodeEntry> verificationCodes = new HashMap<>();

    public void sendVerificationCode(String email) {

        String verificationCode = generateVerificationCode();

        String subject = "Your Verification Code";
        String text = "Your verification code is: " + verificationCode;
        emailService.sendSimpleMessage(email, subject, text);

        // Store the verification code in the map with an expiry date
        verificationCodes.put(email, new CodeEntry(verificationCode, LocalDateTime.now()));
    }

    private String generateVerificationCode() {
        var random = new Random();
        int code = 1000 + random.nextInt(9000); // Generate a 4-digit code
        return String.valueOf(code);
    }

    public VerificationResult verifyCode(String username, String code) {
        verificationCodes.forEach((key, value) -> {
            System.out.println("Key: " + key + ", Value: " + value);
        });

        var entry = verificationCodes.get(username);

        if (entry == null) {
            return VerificationResult.invalidCode();
        } else if (entry.getCode().equals(code)) {
            if (!entry.getTimestamp().isBefore(LocalDateTime.now().minusMinutes(10))) {
                return VerificationResult.success();
            } else {
                return VerificationResult.expiredCode();
            }
        }

        return VerificationResult.invalidCode(); // No matching code found
    }

    // Scheduled task to clean up old codes
    @Scheduled(fixedRate = 3600000) // every hour
    public void cleanUpExpiredCodes() {
        LocalDateTime now = LocalDateTime.now();
        verificationCodes.entrySet().removeIf(entry -> entry.getValue().getTimestamp().isBefore(now.minusMinutes(10)));
    }

    private static class CodeEntry {
        private final String code;
        private final LocalDateTime timestamp;

        public CodeEntry(String code, LocalDateTime timestamp) {
            this.code = code;
            this.timestamp = timestamp;
        }

        public String getCode() {
            return code;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }
    }
}

