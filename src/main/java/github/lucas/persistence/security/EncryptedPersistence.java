package github.lucas.persistence.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import github.lucas.core.pass_generation.Credential;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;

public class EncryptedPersistence {
    private static final ObjectMapper mapper = new ObjectMapper();

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding"; // CBC using initialization vector is safer

    public static class EncryptedFileData {
        public String salt;
        public String data;

        public EncryptedFileData() {
        } // Default constructor for Jackson

        public EncryptedFileData(String salt, String data) {
            this.salt = salt;
            this.data = data;
        }

        @JsonIgnore
        public byte[] getSaltBytes() {
            return Base64.getDecoder().decode(salt);
        }
    }

    // Encrypted safe save/load functions
    public static void saveToFileEncrypted(Map<String, Credential> map, File file, byte[] salt, SecretKeySpec key) throws Exception {
        // 1) Convert map to JSON string
        String jsonData = mapper.writeValueAsString(map);

        // 2) Encrypt JSON string
        String encryptedData = encrypt(jsonData, key);

        // 3) Prepare container with base64 salt and encrypted data
        EncryptedFileData container = new EncryptedFileData(Base64.getEncoder().encodeToString(salt), encryptedData);

        // 4) Save container as JSON file
        mapper.writerWithDefaultPrettyPrinter().writeValue(file, container);
    }

    public static EncryptedFileData loadEncryptedFile(File file) throws IOException {
        if (!file.exists()) {
            return null;
        }
        return mapper.readValue(file, EncryptedFileData.class);
    }

    public static Map<String, Credential> decryptData(String encrypted, SecretKeySpec key) throws Exception {
        // Decrypt encrypted string back to JSON string
        String decryptedJson = decrypt(encrypted, key);

        // Deserialize JSON to Map
        return mapper.readValue(decryptedJson, new TypeReference<>() {
        });
    }

    //Encryption functions
    public static SecretKeySpec createSecretKeySHA256(String passphrase) throws Exception {
        byte[] key = passphrase.getBytes(StandardCharsets.UTF_8);
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        key = sha.digest(key);
        key = Arrays.copyOf(key, 16);
        return new SecretKeySpec(key, ALGORITHM);
    }

    public static SecretKeySpec deriveKeyWithSaltPBKDF2(String passphrase, byte[] salt) throws Exception {
        int iterations = 65536;
        int keyLength = 128;

        PBEKeySpec spec = new PBEKeySpec(passphrase.toCharArray(), salt, iterations, keyLength);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] key = factory.generateSecret(spec).getEncoded();

        return new SecretKeySpec(key, ALGORITHM);
    }

    public static String encrypt(String input, SecretKeySpec key) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedBytes = cipher.doFinal(input.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String decrypt(String encrypted, SecretKeySpec key) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedBytes = Base64.getDecoder().decode(encrypted);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    public static byte[] generateSalt(int length) {
        byte[] salt = new byte[length];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

}
