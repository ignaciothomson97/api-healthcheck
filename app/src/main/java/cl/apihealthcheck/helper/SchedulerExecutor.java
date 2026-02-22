package cl.apihealthcheck.helper;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import cl.apihealthcheck.service.StatusCheckService;
import cl.apihealthcheck.service.impl.StatusCheckServiceImpl;

public class SchedulerExecutor {
    private static final Logger LOGGER = Logger.getLogger(SchedulerExecutor.class.getName());
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private final StatusCheckService statusCheck = new StatusCheckServiceImpl();
    private final CsvExport csvExport = new CsvExport();

    /* Estoy pensando dejar este metodo aún más generico, para pasar como argumento la funcionalidad y no tener que
      añadir un nuevo scheduleWithFixedDelay por cada funcionalidad asinconra de la APP.
      Por el momento, se queda así */
    public void start() {
        LOGGER.info("Iniciando Monitor de APIs...");

        scheduler.scheduleWithFixedDelay(() -> {
            try {
                statusCheck.parallelCheck();
            } catch (RuntimeException ex) {
                LOGGER.severe(() -> "Ha ocurrido una excepción al consultar la API - " + ex.getMessage());
            }
        }, 0, 60, TimeUnit.SECONDS);

        scheduler.scheduleWithFixedDelay(() -> {
            try {
                csvExport.exportAndCleanRecords();
            } catch (RuntimeException ex) {
                LOGGER.severe(() -> "Ha ocurrido una excepción al intentar exportar y limpiar la BD - " + ex.getMessage());
            }
        }, 0, 60, TimeUnit.SECONDS);
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
