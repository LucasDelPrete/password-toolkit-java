package github.lucas.ui.cli;

import java.util.Scanner;

public class MainMenu {

    public static void main(String[] args) {
        int option;
        Scanner sc = new Scanner(System.in);

        do {
            System.out.println("\nMain Menu:");
            System.out.println("1. Analyze Password Strength");
            System.out.println("2. Generate Password");
            System.out.println("3. Check Password Breach");
            System.out.println("0. Exit\n");

            System.out.print("Please select an option: ");
            if (!sc.hasNextInt()) {
                sc.nextLine();
                option = -1;
                continue;
            }

            option = sc.nextInt();
            sc.nextLine();

            switch (option) {
                case 1:
                    PasswordStrengthMenu.display(sc);
                    break;
                case 2:
                    break;
                case 3:
                    break;
                default:
                    if (option != 0) {
                        System.out.println("Invalid option. Please try again.");
                    }
                    break;
            }

        } while (option != 0);
        System.out.println("Exiting the application.");
    }
}
