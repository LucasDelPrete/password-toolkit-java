package github.lucas.core.pass_strength.domain;

import java.util.ArrayList;
import java.util.List;

public class PasswordStrengthAnalyzer {

    private static final int DEFAULT_MIN_SIZE_THRESHOLD = 6;

    public static PasswordFeedback analyzePassword(String password) {
        List<String> missing = new ArrayList<>();

        if (password.length() < DEFAULT_MIN_SIZE_THRESHOLD) {
            missing.add("Minimum length of " + DEFAULT_MIN_SIZE_THRESHOLD + " characters");
        }

        boolean hasLower = false, hasUpper = false, hasDigit = false, hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else if (!Character.isLetterOrDigit(c)) hasSpecial = true;
        }

        if (!hasLower) missing.add("Lowercase letter");
        if (!hasUpper) missing.add("Uppercase letter");
        if (!hasDigit)  missing.add("Digit");
        if (!hasSpecial) missing.add("Special character");

        PasswordStrength strength;

        int fulfilled = 0;
        if (hasLower) fulfilled++;
        if (hasUpper) fulfilled++;
        if (hasDigit) fulfilled++;
        if (hasSpecial) fulfilled++;

        if (password.length() < DEFAULT_MIN_SIZE_THRESHOLD || fulfilled < 2) {
            strength = PasswordStrength.LOW;
        } else if (fulfilled == 3) {
            strength = PasswordStrength.MEDIUM;
        } else {
            strength = PasswordStrength.HIGH;
        }

        return new PasswordFeedback(strength, missing);
    }
}
