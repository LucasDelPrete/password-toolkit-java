package github.lucas.core.pass_strength.domain;

import java.util.List;

public class PasswordFeedback {
    private final PasswordStrength strength;
    private final List<String> missingRequirements;

    public PasswordFeedback(PasswordStrength strength, List<String> missingRequirements) {
        this.strength = strength;
        this.missingRequirements = missingRequirements;
    }

    public PasswordStrength getStrength() {
        return strength;
    }

    public List<String> getMissingRequirements() {
        return missingRequirements;
    }

    @Override
    public String toString() {
        return "Strength: " + strength + ", Missing: " + missingRequirements;
    }
}
