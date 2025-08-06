package github.lucas.core.pass_generation;

import github.lucas.core.common.PasswordRequirements;
import java.security.SecureRandom;
import java.util.*;
import static github.lucas.core.common.PasswordRequirements.*;

/**
 * Utility class for generating random passwords that meet complexity requirements.
 * Ensures the password contains at least one uppercase letter, one lowercase letter,
 * one digit, and one special character.
 */
public class PasswordGenerator {

    // Array of password requirements to randomize character types
    private static final PasswordRequirements[] requirements = {UPPER, LOWER, DIGIT, SPECIAL};

    /**
     * Generates a random password of the specified length, including all complexity requirements.
     *
     * @param length Desired password length.
     * @return Generated password string.
     */
    public static String generate(int length) {
        SecureRandom rd = new SecureRandom();
        List<Character> chars = new ArrayList<>();

        // Add at least one character of each required type
        chars.add((char) ('A' + rd.nextInt(26))); // Uppercase letter
        chars.add((char) ('a' + rd.nextInt(26))); // Lowercase letter
        chars.add((char) ('0' + rd.nextInt(10))); // Digit

        String specials = "!@#$%^&*()-_=+<>?\\";
        chars.add(specials.charAt(rd.nextInt(specials.length()))); // Special character

        // Fill the rest of the password with random characters from all requirements
        for (int i = 4; i < length; i++) {
            PasswordRequirements req = requirements[rd.nextInt(requirements.length)];
            switch (req) {
                case UPPER:
                    chars.add((char) ('A' + rd.nextInt(26)));
                    break;
                case LOWER:
                    chars.add((char) ('a' + rd.nextInt(26)));
                    break;
                case DIGIT:
                    chars.add((char) ('0' + rd.nextInt(10)));
                    break;
                case SPECIAL:
                    chars.add(specials.charAt(rd.nextInt(specials.length())));
                    break;
            }
        }

        // Shuffle the characters to ensure randomness
        Collections.shuffle(chars, rd);

        // Build the final password string
        StringBuilder password = new StringBuilder();
        for (char c : chars) {
            password.append(c);
        }

        return password.toString();
    }
}