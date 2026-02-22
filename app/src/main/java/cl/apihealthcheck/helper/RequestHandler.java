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

    /*
    - Si el LOGGER no fuera static, por cada instancia de RequestHandler, se crearia uno nuevo. Imagina 10.000
    - RequestHandlers, tendrían 10.000 LOGGERs diferentes. Es mejor tener uno, compartido por todas las instancias de clase.
    - Usamos static en clases que no deben cambiar con el tiempo, que no guardan estado, que son constantes o tambien funciones puras
      como Math.min(0, 1), ej: funciones como de calculadora, calculan un valor y devuelven el resultado, nada más
     */
    private static final Logger LOGGER = Logger.getLogger(RequestHandler.class.getName());
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30)).build();

    // Thread Safe, cada hilo genera su propia instancia con las variables locales del metodo. Cada uno vive en el stack del hilo.
    // También mencionar que HttpClient está diseñado para ser Thread Safe e Inmutable, desde Java 11
    // Heap es donde viven los objetos compartidos por el programa, cada instancia nueva se genera aca, y el GC pasa por aca para limpiar lo que no se usa
    // Stack se refiere a la memoria privada de corto plazo de cada hilo, guarda las variables primitivas propias del metodo y las REFERENCIAS a objetos que viven en el Heap
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
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}
