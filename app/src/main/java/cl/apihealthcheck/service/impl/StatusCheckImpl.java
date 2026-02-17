package cl.apihealthcheck.service.impl;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cl.apihealthcheck.constants.Constants;
import cl.apihealthcheck.helper.RequestHandler;
import cl.apihealthcheck.service.Healthcheck;

public class StatusCheckImpl implements Healthcheck {

    private final ExecutorService ioExecutor = Executors.newFixedThreadPool(4);

    @Override
    public void parallelCheck() {
        List<String> targets = List.of(
            Constants.REDDIT_API,
            Constants.LINKEDIN_API, 
            Constants.DISCORD_API, 
            Constants.GITHUB_API);

        for (String target : targets) {
            CompletableFuture.supplyAsync(() -> RequestHandler.buildRequest(target), ioExecutor)
                .thenAccept(result -> System.err.println("Resultado " + result + " - Status: " + result.statusCode()))
                .exceptionally(ex -> {
                    System.out.println("Error");
                    return null;
                });
        }
    }

}
