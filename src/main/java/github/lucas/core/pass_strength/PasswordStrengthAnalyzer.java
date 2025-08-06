package github.lucas.core.pass_strength;

import github.lucas.core.common.PasswordRequirements;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for analyzing password strength.
 * Checks for minimum length and required character types (lowercase, uppercase, digit, special).
 * Returns feedback with missing requirements and strength level.
 */
public class PasswordStrengthAnalyzer {

    // Minimum password length threshold
    private static final int DEFAULT_MIN_SIZE_THRESHOLD = 6;

    /**
     * Analyzes the given password and returns feedback about its strength and missing requirements.
     *
     * @param password The password to analyze.
     * @return PasswordFeedback containing strength level and missing requirements.
     */
    public static PasswordFeedback analyzePassword(String password) {
        List<PasswordRequirements> missing = new ArrayList<>();

        // Check for minimum length
        if (password.length() < DEFAULT_MIN_SIZE_THRESHOLD) {
            missing.add(PasswordRequirements.SIZE);
        }

        boolean hasLower = false, hasUpper = false, hasDigit = false, hasSpecial = false;

        // Check for required character types
        for (char c : password.toCharArray()) {
            if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else if (!Character.isLetterOrDigit(c)) hasSpecial = true;
        }

        if (!hasLower) missing.add(PasswordRequirements.LOWER);
        if (!hasUpper) missing.add(PasswordRequirements.UPPER);
        if (!hasDigit) missing.add(PasswordRequirements.DIGIT);
        if (!hasSpecial) missing.add(PasswordRequirements.SPECIAL);

        PasswordStrength strength;

        // Count fulfilled requirements
        int fulfilled = 0;
        if (hasLower) fulfilled++;
        if (hasUpper) fulfilled++;
        if (hasDigit) fulfilled++;
        if (hasSpecial) fulfilled++;

        // Determine password strength
        if (password.length() < DEFAULT_MIN_SIZE_THRESHOLD || fulfilled <= 2) {
            strength = PasswordStrength.LOW;
        } else if (fulfilled == 3) {
            strength = PasswordStrength.MEDIUM;
        } else {
            strength = PasswordStrength.HIGH;
        }

        return new PasswordFeedback(strength, missing);
    }
}