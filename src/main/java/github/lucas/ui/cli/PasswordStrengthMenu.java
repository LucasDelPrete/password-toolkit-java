package github.lucas.ui.cli;

import github.lucas.core.common.PasswordRequirements;
import github.lucas.core.pass_strength.PasswordFeedback;
import github.lucas.core.pass_strength.PasswordStrengthAnalyzer;

import java.util.Scanner;

public class PasswordStrengthMenu {

    private static void printMissingRequirements(PasswordFeedback feedback) {
        if (!feedback.getMissingRequirements().isEmpty()) {
            System.out.println("Missing requirements:");
            for (PasswordRequirements req : feedback.getMissingRequirements()) {
                System.out.println("- " + req.getDescription());
            }
        }
    }

    public static void display(Scanner sc) {
        System.out.print("Enter password to analyze: ");
        String password = sc.nextLine();

        PasswordFeedback feedback = PasswordStrengthAnalyzer.analyzePassword(password);
        System.out.println("Password strength: " + feedback.getStrength());

        printMissingRequirements(feedback);
    }
}
