package cl.apihealthcheck.service.impl;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import cl.apihealthcheck.constants.Constants;
import cl.apihealthcheck.entity.ApiRequest;
import cl.apihealthcheck.helper.RequestHandler;
import cl.apihealthcheck.records.RestResponse;
import cl.apihealthcheck.repository.RequestRepository;
import cl.apihealthcheck.service.StatusCheckService;

public class StatusCheckServiceImpl implements StatusCheckService {
    private final static Logger LOGGER = Logger.getLogger(StatusCheckServiceImpl.class.getName());
    private final Map<String, String> targetMap = returnTargetList();
    private final ExecutorService ioExecutor = Executors.newFixedThreadPool(Math.min(targetMap.size(), 20));

    @Override
    public void parallelCheck() {
        for (Map.Entry<String, String> entry : targetMap.entrySet()) {
            String targetUrl = entry.getValue();
            String apiName = entry.getKey();

            CompletableFuture.supplyAsync(() -> RequestHandler.buildRequest(targetUrl, apiName), ioExecutor)
                .thenAccept(result -> persistEntity(buildEntity(apiName, result)))
                .exceptionally(ex -> {
                    LOGGER.severe("Error crítico en hilo asíncrono para " + apiName + ": " + ex.getMessage());
                    return null;
                });
        }
    }

    private Map<String, String> returnTargetList() {
        return Map.of(
            Constants.REDDIT, Constants.REDDIT_API,
            Constants.LINKEDIN, Constants.LINKEDIN_API,
            Constants.DISCORD, Constants.DISCORD_API,
            Constants.GITHUB, Constants.GITHUB_API
        );
    }

    private ApiRequest buildEntity(String target, RestResponse restResponse) {
        return new ApiRequest.Builder()
            .apiName(target)
            .status(restResponse.statusCode())
            .checked(Timestamp.valueOf(LocalDateTime.now()))
            .isUp(restResponse.isSuccess())
            .errorMessage(restResponse.errorMessage())
            .build();
    }

    private void persistEntity(ApiRequest apiRequest) {
        LOGGER.info(() -> "Persistiendo resultado de: " + apiRequest.getApiName() + " (Status: " + apiRequest.getStatus() + ")");
        RequestRepository requestRepository = new RequestRepository();
        requestRepository.save(apiRequest);
    }
}
