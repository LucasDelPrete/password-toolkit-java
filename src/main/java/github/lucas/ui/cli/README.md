# Command Line Interface for Password Toolkit

This toolkit provides a simple command line interface (CLI) for password-related utilities, including strength analysis, secure password generation, and breach verification.

---

## UI Guide:

1. **Clone the repository:**<br>
   git clone https://github.com/LucasDelPrete/password-toolkit-java.git

2. **Run the command:**<br>
    ```
   mvn package
   ```
   This will generate target/password-toolkit-java-1.0-SNAPSHOT-jar-with-dependencies.jar.

3. **Run the CLI application:**
   ```
   java -cp target/password-toolkit-java-1.0-SNAPSHOT-jar-with-dependencies.jar github.lucas.ui.cli.MainMenu
    ```

When you run the application, you will see the following main menu:

Main Menu:

 1- Analyze Password Strength <br>
 2- Generate Password <br>
 3- Check Password Breach <br>
 4- Exit <br>

Please select an option:


### Options:

#### 1. Analyze Password Strength
- Opens the **Password Strength Menu**.
- Allows you to input a password and receive a strength evaluation (e.g., weak, medium, strong).
- The analysis may include checks for:
 - Minimum length
 - Character variety (uppercase, lowercase, numbers, symbols)
 - Common patterns or dictionary words

#### 2. Generate Password
- Opens the **Password Generator Menu**.
- Lets you generate strong passwords based on configurable options such as:
 - Length
 - Inclusion of uppercase, lowercase, numbers, and special characters
- Ensures secure and random password creation.

#### 3. Check Password Breach
- Opens the **Password Breach Menu**.
- Allows you to verify whether a password has been exposed in known data breaches.
- Uses a secure API (HaveIBeenPwned)
- Provides feedback if the password is compromised.

#### 0. Exit
- Exits the application.

---
