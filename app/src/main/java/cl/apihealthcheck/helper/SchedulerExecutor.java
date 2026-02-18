package cl.apihealthcheck.helper;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import cl.apihealthcheck.Main;
import cl.apihealthcheck.service.impl.StatusCheckImpl;

public class SchedulerExecutor {
    private static final StatusCheckImpl statusCheckImpl = new StatusCheckImpl();
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void start() {
        scheduler.scheduleAtFixedRate(statusCheckImpl::parallelCheck, 0, 60, TimeUnit.SECONDS);
        LOGGER.info("Monitor iniciado");
    }

    public void stopAll() {
        scheduler.shutdown();
        LOGGER.info("Monitor detenido");
    }
}
