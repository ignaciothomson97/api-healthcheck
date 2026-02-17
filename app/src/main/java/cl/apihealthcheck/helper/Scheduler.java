package cl.apihealthcheck.helper;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import cl.apihealthcheck.Main;
import cl.apihealthcheck.constants.Constants;

public class Scheduler {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static void startScheduler() {
        LOGGER.info("Iniciando Scheduler");
        final Runnable invoker = () -> {
            try {
                RestImpl.buildRequest(Constants.redditApi + "/api/v2/status.json");
            } catch (Exception e) {
                LOGGER.warning("Ha ocurrido una excepción al realizar la llamada programada");
            }
        };
        final ScheduledFuture<?> invokerHandler = scheduler.scheduleAtFixedRate(invoker, 0, 60, TimeUnit.SECONDS);
        scheduler.schedule(() -> {
            invokerHandler.cancel(true);
            scheduler.shutdown();
        }, 60 * 60, TimeUnit.SECONDS);
    }

}
