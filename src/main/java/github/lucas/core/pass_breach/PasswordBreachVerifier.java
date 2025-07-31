package github.lucas.core.pass_breach;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.MessageDigest;

public class PasswordBreachVerifier {

    public static boolean checkPassword(String password) throws Exception {
        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
        byte[] hashBytes = sha1.digest(password.getBytes());
        StringBuilder hashBuilder = new StringBuilder();
        for (byte b : hashBytes) {
            hashBuilder.append(String.format("%02X", b));
        }
        String fullHash = hashBuilder.toString();
        String prefix = fullHash.substring(0, 5);
        String suffix = fullHash.substring(5);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.pwnedpasswords.com/range/" + prefix))
                .header("Add-Padding", "true")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return response.body().lines().anyMatch(line -> line.startsWith(suffix));
    }
}
