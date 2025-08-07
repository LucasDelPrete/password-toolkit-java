package github.lucas.persistence.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import github.lucas.core.pass_generation.Credential;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
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

/**
 * Utility class for secure data persistence using AES encryption.
 * Provides methods to save and load encrypted files, derive keys from passphrase and salt,
 * and perform encryption and decryption operations. Data is serialized as JSON and protected
 * with a user-derived key.
 */
public class EncryptedPersistence {
    private static final ObjectMapper mapper = new ObjectMapper();

    // AES algorithm and transformation settings
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";

    // Container class for encrypted file data and salt
    public static class EncryptedFileData {
        public String salt;
        public String data;
        public String iv;

        public EncryptedFileData() {
        } // Default constructor for Jackson

        public EncryptedFileData(String salt, String data, String iv) {
            this.salt = salt;
            this.data = data;
            this.iv = iv;
        }

        @JsonIgnore
        public byte[] getSaltBytes() {
            return Base64.getDecoder().decode(salt);
        }

        @JsonIgnore
        public byte[] getIvBytes() {
            return Base64.getDecoder().decode(iv);
        }
    }

    // Encrypted safe save/load functions
    public static void saveToFileEncrypted(Map<String, Credential> map, File file, byte[] salt, SecretKeySpec key) throws Exception {
        // 1) Convert map to JSON string
        String jsonData = mapper.writeValueAsString(map);

        // 2) Encrypt JSON string
        String[] encryptedWithIv = encrypt(jsonData, key);
        String encryptedData = encryptedWithIv[0];
        String iv = encryptedWithIv[1];

        // 3) Prepare container with base64 salt and encrypted data
        EncryptedFileData container = new EncryptedFileData(Base64.getEncoder().encodeToString(salt), encryptedData, iv);

        // 4) Save container as JSON file
        mapper.writerWithDefaultPrettyPrinter().writeValue(file, container);
    }

    // Loads encrypted file data from disk
    public static EncryptedFileData loadEncryptedFile(File file) throws IOException {
        if (!file.exists()) {
            return null;
        }
        return mapper.readValue(file, EncryptedFileData.class);
    }

    // Decrypts encrypted data and deserializes it to a map of credentials
    public static Map<String, Credential> decryptData(EncryptedFileData fileData, SecretKeySpec key) throws Exception {
        // Decrypt encrypted string back to JSON string
        String decryptedJson = decrypt(fileData.data, key, fileData.getIvBytes());

        // Deserialize JSON to Map
        return mapper.readValue(decryptedJson, new TypeReference<>() {
        });
    }

    // Creates an AES key from a passphrase using SHA-256
    public static SecretKeySpec createSecretKeySHA256(String passphrase) throws Exception {
        byte[] key = passphrase.getBytes(StandardCharsets.UTF_8);
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        key = sha.digest(key);
        key = Arrays.copyOf(key, 16);
        return new SecretKeySpec(key, ALGORITHM);
    }

    // Derives an AES key from a passphrase and salt using PBKDF2
    public static SecretKeySpec deriveKeyWithSaltPBKDF2(String passphrase, byte[] salt) throws Exception {
        int iterations = 65536;
        int keyLength = 128;

        PBEKeySpec spec = new PBEKeySpec(passphrase.toCharArray(), salt, iterations, keyLength);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] key = factory.generateSecret(spec).getEncoded();

        return new SecretKeySpec(key, ALGORITHM);
    }

    // Encrypts a string using AES and returns the result as Base64
    public static String[] encrypt(String input, SecretKeySpec key) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        SecureRandom random = new SecureRandom();
        byte[] iv = new byte[16];
        random.nextBytes(iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
        byte[] encryptedBytes = cipher.doFinal(input.getBytes(StandardCharsets.UTF_8));

        return new String[]{
                Base64.getEncoder().encodeToString(encryptedBytes),
                Base64.getEncoder().encodeToString(iv)
        };
    }

    // Decrypts a Base64-encoded string using AES
    public static String decrypt(String encrypted, SecretKeySpec key, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
        byte[] decodedBytes = Base64.getDecoder().decode(encrypted);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    // Generates a random salt of the specified length
    public static byte[] generateSalt(int length) {
        byte[] salt = new byte[length];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

}
