package github.lucas.ui.gui;

import github.lucas.core.pass_generation.Credential;
import github.lucas.persistence.security.EncryptedPersistence;
import github.lucas.ui.gui.controller.PasswordToolkitController;
import github.lucas.ui.gui.utils.DialogUtils;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PasswordToolkit extends Application {

    private final Map<String, Credential> passwordDatabase = new HashMap<>();
    private final File saveFile = new File("passwords.enc");

    private String passphrase;
    private byte[] salt;
    private SecretKeySpec secretKey;

    @Override
    public void start(Stage stage) throws Exception {
        passphrase = promptUserForKeyWithRetries();
        if (passphrase == null) {
            Platform.exit();
            return;
        }

        loadDataEncrypted();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/PasswordToolkit.fxml"));
        Parent root = fxmlLoader.load();

        PasswordToolkitController controller = fxmlLoader.getController();
        controller.initData(passwordDatabase);

        stage.setTitle("Password Toolkit");
        Scene scene = new Scene(root, 1000, 700);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        saveDataEncrypted();
    }

    public void saveDataEncrypted() {
        try {
            if (secretKey == null || salt == null) {
                System.err.println("Secret key or salt not initialized, cannot save securely.");
                return;
            }
            EncryptedPersistence.saveToFileEncrypted(passwordDatabase, saveFile, salt, secretKey);
            System.out.println("Data saved with encryption.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadDataEncrypted() {
        try {
            if (saveFile.exists()) {
                EncryptedPersistence.EncryptedFileData encryptedFileData = EncryptedPersistence.loadEncryptedFile(saveFile);
                if (encryptedFileData != null) {
                    salt = encryptedFileData.getSaltBytes();
                    secretKey = EncryptedPersistence.deriveKeyWithSaltPBKDF2(passphrase, salt);

                    try {
                        Map<String, Credential> loaded = EncryptedPersistence.decryptData(encryptedFileData.data, secretKey);
                        passwordDatabase.clear();
                        passwordDatabase.putAll(loaded);
                        System.out.println("Data loaded with encryption.");
                    } catch (Exception e) {
                        DialogUtils.showAlert("Failed to decrypt data. Wrong password?");
                        Platform.exit();
                    }
                }
            } else {
                salt = EncryptedPersistence.generateSalt(16);
                secretKey = EncryptedPersistence.deriveKeyWithSaltPBKDF2(passphrase, salt);
                EncryptedPersistence.saveToFileEncrypted(passwordDatabase, saveFile, salt, secretKey);
                System.out.println("Starting with empty database and encryption.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String promptUserForKey() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Password Required");
        dialog.setHeaderText("Please enter your password:");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        dialog.getDialogPane().setContent(passwordField);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                return passwordField.getText();
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();
        return result.orElse(null);
    }

    private String promptUserForKeyWithRetries() {
        for (int i = 0; i < 3; i++) {
            String pass = promptUserForKey();
            if (pass == null || pass.isEmpty()) {
                return null;
            }
            try {
                if (testPassphrase(pass)) {
                    return pass;
                } else {
                    DialogUtils.showAlert("Incorrect password. Try again.");
                }
            } catch (Exception e) {
                DialogUtils.showAlert("Error trying to decrypt. Try again.");
            }
        }
        return null;
    }

    private boolean testPassphrase(String passphrase) {
        try {
            if (!saveFile.exists()) return true;

            EncryptedPersistence.EncryptedFileData encryptedData = EncryptedPersistence.loadEncryptedFile(saveFile);
            if (encryptedData == null) return false;

            byte[] saltBytes = encryptedData.getSaltBytes();
            SecretKeySpec testKey = EncryptedPersistence.deriveKeyWithSaltPBKDF2(passphrase, saltBytes);

            EncryptedPersistence.decryptData(encryptedData.data, testKey);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void main(String[] args) {
        launch();
    }
}