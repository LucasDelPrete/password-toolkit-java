package github.lucas.core.pass_generation;
import github.lucas.core.common.PasswordRequirements;
import java.util.*;

import static github.lucas.core.common.PasswordRequirements.*;

public class PasswordGenerator {

    private static final PasswordRequirements[] requirements = {UPPER, LOWER, DIGIT, SPECIAL};

    public static String generate(int length) {
        Random rd = new Random();
        List<Character> chars = new ArrayList<>();

        chars.add((char) ('A' + rd.nextInt(26)));
        chars.add((char) ('a' + rd.nextInt(26)));
        chars.add((char) ('0' + rd.nextInt(10)));

        String specials = "!@#$%^&*()-_=+<>?\\";
        chars.add(specials.charAt(rd.nextInt(specials.length())));

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

        Collections.shuffle(chars, rd);

        StringBuilder password = new StringBuilder();
        for (char c : chars) {
            password.append(c);
        }

        return password.toString();
    }
}
