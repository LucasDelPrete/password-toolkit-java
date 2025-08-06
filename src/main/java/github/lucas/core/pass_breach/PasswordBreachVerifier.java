package github.lucas.core.pass_breach;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.MessageDigest;

/**
 * Utility class to verify if a password has been compromised in public data breaches
 * using the (<a href="https://api.pwnedpasswords.com">"Pwned Passwords" API</a>).
 */
public class PasswordBreachVerifier {

    /**
     * Checks if the given password has been exposed in any known data breach.
     *
     * @param password The password to check.
     * @return true if the password was found in breaches, false otherwise.
     * @throws Exception If an error occurs during the request or hash processing.
     */
    public static boolean checkPassword(String password) throws Exception {
        // Hash the password using SHA-1
        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
        byte[] hashBytes = sha1.digest(password.getBytes());
        StringBuilder hashBuilder = new StringBuilder();
        for (byte b : hashBytes) {
            hashBuilder.append(String.format("%02X", b));
        }
        String fullHash = hashBuilder.toString();
        String prefix = fullHash.substring(0, 5);
        String suffix = fullHash.substring(5);

        // Query the API with the hash prefix
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.pwnedpasswords.com/range/" + prefix))
                .header("Add-Padding", "true")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check if the hash suffix is present in the response
        return response.body().lines().anyMatch(line -> line.startsWith(suffix));
    }
}