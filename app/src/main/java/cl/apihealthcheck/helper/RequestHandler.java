package cl.apihealthcheck.helper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.logging.Logger;

public class RequestHandler {

    private static final Logger LOGGER = Logger.getLogger(RequestHandler.class.getName());

    public static HttpResponse<String> buildRequest(String uri) {
        try {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(new URI(uri)).GET().build();
            return httpClient.send(request, BodyHandlers.ofString());
        } catch (IOException | InterruptedException | URISyntaxException e) {
            LOGGER.warning("Ha ocurrido una excepción al solicitar el servicio.");
            return null;
        }
    }
}
