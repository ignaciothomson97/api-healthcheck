package cl.apihealthcheck.helper;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import cl.apihealthcheck.service.impl.StatusCheckImpl;

public class SchedulerExecutor {
    private static final Logger LOGGER = Logger.getLogger(SchedulerExecutor.class.getName());
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final StatusCheckImpl statusCheck = new StatusCheckImpl();

    public void start() {
        LOGGER.info("Iniciando Monitor de APIs...");

        Runnable safeTask = () -> {
            try {
                statusCheck.parallelCheck();
            } catch (RuntimeException ex) {
                LOGGER.severe(() -> "Ha ocurrido una excepción al intentar iniciar el Scheduler - " + ex.getMessage());
            }
        };

        scheduler.scheduleWithFixedDelay(safeTask, 0, 60, TimeUnit.SECONDS);
    }

    public void stopAll() {
        try {
            LOGGER.info("Deteniendo Monitor de APIs...");
            scheduler.shutdown();
            if (!scheduler.awaitTermination(30, TimeUnit.SECONDS)) {
                LOGGER.warning("Forzando apagado de hilos rezagados...");
                scheduler.shutdownNow();
            }
        } catch (InterruptedException ex) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
            LOGGER.severe(() -> "Ha ocurrido una excepción al intentar detener el Scheduler - " + ex.getMessage());
        }
    }
}
