package cl.apihealthcheck.helper;

import cl.apihealthcheck.model.RestResponse;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.logging.Logger;

public class RequestHandler {
    private static final Logger LOGGER = Logger.getLogger(RequestHandler.class.getName());
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30)).build();

    public static RestResponse buildRequest(String uri, String apiName) {
        int maxRetries = 3;
        int attempt = 0;
        while (true) {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI(uri))
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
                                "(KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                        .header("Accept", "application/json")
                        .timeout(Duration.ofSeconds(30)) // Patrón 'Timeout'
                        .GET()
                        .build();
                HttpResponse<String> response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
                String errorMessage = (response.statusCode() >= 400) ? response.body() : "";

                return new RestResponse(response.statusCode(), response.body(), errorMessage);
            } catch (ConnectException e) {
                // Patrón 'Retry'
                final int currentAttempt = attempt++;
                LOGGER.warning(() -> "Intento " + currentAttempt + " fallido para " + uri + " (Conexión rechazada). Reintentando...");

                if (attempt < maxRetries) {
                    waitUntilRetry();
                } else {
                    LOGGER.severe("Se agotaron los reintentos para " + apiName);
                    return new RestResponse(0, "", "ConnectException tras " + maxRetries + " intentos");
                }
            } catch (HttpTimeoutException e) {
                return new RestResponse(408, "", "Timeout - La API ha tardado demasiado " +
                        "en responder");
            } catch (IOException | InterruptedException | URISyntaxException e) {
                LOGGER.severe(() -> "Ha ocurrido una excepción al solicitar el servicio: " + uri + " - " +
                        e.getClass().getSimpleName() + " - Mensaje: " + e.getMessage());
                return new RestResponse(0, "", e.getMessage());
            }
        }
    }

    private static void waitUntilRetry() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}
