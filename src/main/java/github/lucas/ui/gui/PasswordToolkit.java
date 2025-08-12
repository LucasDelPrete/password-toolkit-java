package github.lucas.ui.gui;

import github.lucas.core.pass_generation.Credential;
import github.lucas.persistence.security.EncryptedPersistence;
import github.lucas.ui.gui.controller.PasswordToolkitController;
import github.lucas.ui.gui.utils.DialogUtils;
import github.lucas.ui.gui.utils.SaveFileUtils;
import github.lucas.ui.gui.utils.SaveFileUtils.*;
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

    private byte[] salt;
    private SecretKeySpec secretKey;

    @Override
    public void start(Stage stage) throws Exception {
        String passphrase = promptUserForKeyWithRetries();
        if (passphrase == null) {
            Platform.exit();
            return;
        }

        EncryptionParams params = SaveFileUtils.loadDataEncrypted(passwordDatabase, saveFile, passphrase);
        this.salt = params.salt();
        this.secretKey = params.secretKey();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/PasswordToolkit.fxml"));
        Parent root = fxmlLoader.load();

        PasswordToolkitController controller = fxmlLoader.getController();
        controller.initData(passwordDatabase, saveFile, this);

        stage.setTitle("Password Toolkit");
        Scene scene = new Scene(root, 1000, 700);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        SaveFileUtils.saveDataEncrypted(passwordDatabase, saveFile, salt, secretKey);
    }

    public void setEncryptionParams(byte[] salt, SecretKeySpec secretKey) {
        this.salt = salt;
        this.secretKey = secretKey;
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
                    DialogUtils.showError("Wrong password", "Incorrect password. Try again.");
                }
            } catch (Exception e) {
                DialogUtils.showError("Error decrypting", "Error trying to decrypt. Try again.");
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

            EncryptedPersistence.decryptData(encryptedData, testKey);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void main(String[] args) {
        launch();
    }
}