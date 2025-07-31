package github.lucas.ui.cli;

import github.lucas.core.pass_breach.PasswordBreachVerifier;
import github.lucas.core.pass_generation.PasswordGenerator;
import github.lucas.core.pass_strength.PasswordStrengthAnalyzer;

import java.util.Scanner;

public class PasswordBreachMenu {

    public static void display(Scanner sc) {
        System.out.print("Enter password to search: ");
        String password = sc.nextLine();

        try {
            boolean feedback = PasswordBreachVerifier.checkPassword(password);

            if (feedback) {
                System.out.println("Password has been breached");
            } else {
                System.out.println("Password has not been breached");
            }

        } catch (Exception e) {
            System.err.println("Error checking password");
        }


    }
}
