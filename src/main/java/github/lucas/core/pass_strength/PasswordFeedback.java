package github.lucas.core.pass_strength;

import github.lucas.core.common.PasswordRequirements;
import java.util.List;

public class PasswordFeedback {
    private final PasswordStrength strength;
    private final List<PasswordRequirements> missingRequirements;

    public PasswordFeedback(PasswordStrength strength, List<PasswordRequirements> missingRequirements) {
        this.strength = strength;
        this.missingRequirements = missingRequirements;
    }

    public PasswordStrength getStrength() {
        return strength;
    }

    public List<PasswordRequirements> getMissingRequirements() {
        return missingRequirements;
    }
}
