package cl.apihealthcheck.service.impl;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import cl.apihealthcheck.constants.Constants;
import cl.apihealthcheck.entity.ApiRequest;
import cl.apihealthcheck.helper.RequestHandler;
import cl.apihealthcheck.repository.ApiRequestRepository;
import cl.apihealthcheck.service.Healthcheck;

public class StatusCheckImpl implements Healthcheck {

    private final ExecutorService ioExecutor = Executors.newFixedThreadPool(4);
    private final static Logger LOGGER = Logger.getLogger(StatusCheckImpl.class.getName());

    @Override
    public void parallelCheck() {
        List<String> targets = buildTargets();
        for (String target : targets) {
            CompletableFuture.supplyAsync(() -> RequestHandler.buildRequest(target), ioExecutor)
                .thenAccept(result -> {
                    persistData(buildApiRequest(target, result));
                })
                .exceptionally(ex -> {
                    LOGGER.severe("Error en operación asíncrona");
                    return null;
                });
        }
    }

    private List<String> buildTargets() {
        return List.of(
            Constants.REDDIT_API,
            Constants.LINKEDIN_API, 
            Constants.DISCORD_API, 
            Constants.GITHUB_API
        );
    }

    private ApiRequest buildApiRequest(String target, int result) {
        return new ApiRequest.Builder()
            .apiName(target)
            .lastStatus(result)
            .isUp(result <= 300 && result >= 200)
            .lastErrorMessage("")
            .build();
    }

    private void persistData(ApiRequest apiRequest) {
        LOGGER.info(() -> "Persistiendo resultado de: " + apiRequest.getApiName() + " (Status: " + apiRequest.getLastStatus() + ")");
        ApiRequestRepository.upsertStatus(apiRequest);
    }
}
