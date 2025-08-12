package github.lucas.ui.gui.utils;

import github.lucas.core.pass_generation.Credential;
import github.lucas.persistence.security.EncryptedPersistence;
import javafx.application.Platform;

import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.util.Map;

public class SaveFileUtils {

    public static void saveDataEncrypted(Map<String, Credential> passwordDatabase, File saveFile, byte[] salt, SecretKeySpec secretKey) throws Exception {
        try {
            if (secretKey == null || salt == null) {
                System.err.println("Secret key or salt not initialized, cannot save securely.");
                return;
            }
            EncryptedPersistence.saveToFileEncrypted(passwordDatabase, saveFile, salt, secretKey);
            System.out.println("Data saved with encryption.");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public record EncryptionParams(byte[] salt, SecretKeySpec secretKey) {
    }

    public static EncryptionParams loadDataEncrypted(Map<String, Credential> passwordDatabase, File saveFile, String passphrase) throws Exception {
        byte[] salt;
        SecretKeySpec secretKey;
        try {
            if (saveFile.exists()) {
                EncryptedPersistence.EncryptedFileData encryptedFileData = EncryptedPersistence.loadEncryptedFile(saveFile);
                if (encryptedFileData != null) {
                    salt = encryptedFileData.getSaltBytes();
                    secretKey = EncryptedPersistence.deriveKeyWithSaltPBKDF2(passphrase, salt);

                    try {
                        Map<String, Credential> loaded = EncryptedPersistence.decryptData(encryptedFileData, secretKey);
                        passwordDatabase.clear();
                        passwordDatabase.putAll(loaded);
                        System.out.println("Data loaded with encryption.");
                    } catch (Exception e) {
                        DialogUtils.showError("Error decrypting", "Failed to decrypt data. Wrong password?");
                        Platform.exit();
                    }
                } else {
                    throw new Exception("Invalid save file.");
                }
            } else {
                // First execution: generate salt, derive key, save empty database
                salt = EncryptedPersistence.generateSalt(16);
                secretKey = EncryptedPersistence.deriveKeyWithSaltPBKDF2(passphrase, salt);
                EncryptedPersistence.saveToFileEncrypted(passwordDatabase, saveFile, salt, secretKey);
                System.out.println("Starting with empty database and encryption.");
            }
            return new EncryptionParams(salt, secretKey);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}


