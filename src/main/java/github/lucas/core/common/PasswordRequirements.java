package github.lucas.core.common;

public enum PasswordRequirements {
    UPPER("Uppercase letter"),
    LOWER("Lowercase letter"),
    DIGIT("Digit"),
    SPECIAL("Special character"),
    SIZE("Minimum length (6)");

    private final String description;

    PasswordRequirements(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
