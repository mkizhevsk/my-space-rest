package com.mk.myspacerest.data.security.verification;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VerificationResult {

    private boolean successful;
    private boolean codeInvalid;
    private boolean codeExpired;

    public static VerificationResult success() {
        return new VerificationResult(true, false, false);
    }

    public static VerificationResult invalidCode() {
        return new VerificationResult(false, true, false);
    }

    public static VerificationResult expiredCode() {
        return new VerificationResult(false, false, true);
    }
}
