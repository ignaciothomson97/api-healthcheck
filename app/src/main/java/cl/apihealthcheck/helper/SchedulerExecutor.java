package cl.apihealthcheck.helper;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import cl.apihealthcheck.records.ScheduledJob;
import cl.apihealthcheck.repository.RequestRepository;
import cl.apihealthcheck.service.StatusCheckService;
import cl.apihealthcheck.service.impl.StatusCheckServiceImpl;

public class SchedulerExecutor {
    private static final Logger LOGGER = Logger.getLogger(SchedulerExecutor.class.getName());
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final StatusCheckService statusCheck = new StatusCheckServiceImpl();
    private final DataHandler dataHandler = new DataHandler(new RequestRepository());
    private final List<ScheduledJob> scheduledJobs = buildScheduledJobs();

    public void start() {
        LOGGER.info("Iniciando Monitor de APIs...");
        if (scheduledJobs.isEmpty()) LOGGER.warning("No hay trabajos programados");
        for (ScheduledJob scheduledJob : scheduledJobs) registerJob(scheduledJob);
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

    private void registerJob(ScheduledJob scheduledJob) {
        scheduler.scheduleWithFixedDelay(() -> {
           try {
               scheduledJob.action().run();
           } catch (Throwable ex) {
               LOGGER.severe(() -> "Ha ocurrido una excepción al ejecutar el trabajo '" + scheduledJob.name() + "' - " + ex.getMessage());
           }
        }, scheduledJob.initialDelay(), scheduledJob.period(), scheduledJob.timeUnit());
        LOGGER.info(() -> String.format("Trabajo '%s' programado con periodo de %d %s",
                scheduledJob.name(), scheduledJob.period(), scheduledJob.timeUnit()));
    }

    private List<ScheduledJob> buildScheduledJobs() {
        return List.of(
                new ScheduledJob(
                        "StatusCheck",
                        statusCheck::parallelCheck,
                        0, 60, TimeUnit.SECONDS
                ),
                new ScheduledJob(
                        "CsvExport",
                        dataHandler::exportRecords,
                        7, 7, TimeUnit.DAYS
                )
        );
    }
}
