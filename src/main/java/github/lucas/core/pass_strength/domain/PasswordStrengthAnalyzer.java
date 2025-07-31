package github.lucas.core.pass_strength.domain;

import static github.lucas.core.pass_strength.domain.PasswordStrength.*;

public class PasswordStrengthAnalyzer {

    private static final int defaultMinSizeThreshold = 6;
    private static final int highClassificationThreshold = 4;
    private static final int mediumClassificationThreshold = 2;

    public static boolean lowerCase, upperCase , special, number;

    public static PasswordStrength parsePassword(String password) {
        initializeFields();
        if (password.length() < defaultMinSizeThreshold) return LOW;

        int count = checkPassword(password);

        if (count == highClassificationThreshold) return HIGH;
        else if (count > mediumClassificationThreshold) return MEDIUM;
        else return LOW;
    }

    private static int checkPassword(String password) {
        int count = 0;

        for (char c : password.toCharArray()) {
            if (Character.isLowerCase(c)) {
                if (!lowerCase) {
                    lowerCase = true;
                    count++;
                }
            } else if (Character.isUpperCase(c)) {
                if (!upperCase) {
                    upperCase = true;
                    count++;
                }
            } else if (Character.isDigit(c)) {
                if (!number) {
                    number = true;
                    count++;
                }
            } else {
                if (!special) {
                    special = true;
                    count++;
                }
            }
        }
        return count;
    }

    private static void initializeFields() {
        lowerCase = upperCase = special = number = false;
    }
}
