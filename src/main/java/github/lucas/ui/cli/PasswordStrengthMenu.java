package github.lucas.ui.cli;

import github.lucas.core.common.PasswordRequirements;
import github.lucas.core.pass_strength.domain.PasswordFeedback;
import github.lucas.core.pass_strength.domain.PasswordStrengthAnalyzer;

import java.util.Scanner;

public class PasswordStrengthMenu {

    public static void display(Scanner sc) {
        System.out.print("Enter password to analyze: ");
        String password = sc.nextLine();

        PasswordFeedback feedback = PasswordStrengthAnalyzer.analyzePassword(password);
        System.out.println("Password strength: " + feedback.getStrength());

        if (!feedback.getMissingRequirements().isEmpty()) {
            System.out.println("Missing requirements:");
            for (PasswordRequirements req : feedback.getMissingRequirements()) {
                System.out.println("- " + req.getDescription());
            }
        }
    }
}
