package cl.apihealthcheck;

import cl.apihealthcheck.helper.SchedulerExecutor;

import java.util.logging.Logger;

public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    static { System.setProperty("java.util.logging.SimpleFormatter.format", "[%4$s] %5$s %n"); }

    public static void main(String[] args) {
       SchedulerExecutor monitor =  new SchedulerExecutor();

       Runtime.getRuntime().addShutdownHook(new Thread(() -> {
           LOGGER.info("[SYSTEM] Señal de apagado detectada. Cerrando recursos de forma segura...");
           monitor.stopAll();
       }));

       monitor.start();
    }
}