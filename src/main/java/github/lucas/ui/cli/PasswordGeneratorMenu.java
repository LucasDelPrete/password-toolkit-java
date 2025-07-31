package github.lucas.ui.cli;

import github.lucas.core.pass_generation.PasswordGenerator;

import java.util.Scanner;

public class PasswordGeneratorMenu {

    public static void display(Scanner sc) {
        System.out.print("Enter password's length: ");

        int length;
        if (!sc.hasNextInt()){
            System.out.println("Invalid input");
            return;
        }

        length = sc.nextInt();
        if (length <= 0){
            System.out.println("Invalid length");
            return;
        }

        String password = PasswordGenerator.generate(length);

        System.out.printf("\nGenerated Password:\n%s\n", password);

    }
}
