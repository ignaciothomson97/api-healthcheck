package cl.apihealthcheck;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Main {

    private final static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static void main(String[] args) {
        final Runnable beeper = new Runnable() {
            public void run() {
                try {
                    HttpClient httpClient = HttpClient.newHttpClient();
                    HttpRequest request = HttpRequest.newBuilder().uri(new URI("https://httpbin.org/status/500")).GET()
                            .build();
                    HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

                    if (response.statusCode() != 200) {
                        System.err.printf("Error: %s %s\n", response.statusCode(), response.body());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        // Se configura un delay inicial de 0 segundos, y un intervalo de 10 segundos
        final ScheduledFuture<?> beeperHandle = scheduler.scheduleAtFixedRate(beeper, 0, 10, TimeUnit.SECONDS);

        // Limita la ejecución a una hora
        scheduler.schedule(new Runnable() {
            public void run() {
                beeperHandle.cancel(true);
                scheduler.shutdown();
            }
        }, 60 * 60, TimeUnit.SECONDS);
    }
}