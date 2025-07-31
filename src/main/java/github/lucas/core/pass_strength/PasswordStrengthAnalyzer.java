package github.lucas.core.pass_strength;

import github.lucas.core.common.PasswordRequirements;
import java.util.ArrayList;
import java.util.List;

public class PasswordStrengthAnalyzer {

    private static final int DEFAULT_MIN_SIZE_THRESHOLD = 6;

    public static PasswordFeedback analyzePassword(String password) {
        List<PasswordRequirements> missing = new ArrayList<>();

        if (password.length() < DEFAULT_MIN_SIZE_THRESHOLD) {
            missing.add(PasswordRequirements.SIZE);
        }

        boolean hasLower = false, hasUpper = false, hasDigit = false, hasSpecial = false;

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
