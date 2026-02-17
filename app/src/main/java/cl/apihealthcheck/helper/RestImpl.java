package cl.apihealthcheck.helper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.logging.Logger;

public class RestImpl {

    private static final Logger LOGGER = Logger.getLogger(RestImpl.class.getName());

    public static void buildRequest(String uri) {
        try {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(new URI(uri)).GET().build();
            handleResponse(httpClient.send(request, BodyHandlers.ofString()));
        } catch (IOException | InterruptedException | URISyntaxException e) {
            LOGGER.warning("Ha ocurrido una excepción al solicitar el servicio.");
            // return null;
        }
    }

    public static void handleResponse(HttpResponse<String> response) {
        System.out.println("Reponse: " + response + " - Status: " + response.statusCode());
    }

}
