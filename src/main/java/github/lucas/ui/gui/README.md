# Graphical Interface for Password Toolkit 

## UI Guide:

1. **Clone the repository:**
   git clone https://github.com/LucasDelPrete/password-toolkit-java.git
2. **Run the GUI application with Maven:**
   mvn javafx:run

Maven will download all dependencies and start the graphical interface automatically.

## Application Password

- The first time you run the program, you will be asked to set a master password.
- This password is required to access the encrypted password vault.

You can later change it from the menu:
File → Change Password.

## Saving Passwords

- When generating a password, you can choose to save it along with:
  - Website
  - Username

All saved entries are encrypted and stored in a file named **passwords.enc** created in the project’s root folder.