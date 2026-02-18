package cl.apihealthcheck;

import cl.apihealthcheck.helper.SchedulerExecutor;

public class Main {
    static { System.setProperty("java.util.logging.SimpleFormatter.format", "[%4$s] %5$s %n"); }
    public static void main(String[] args) {
       SchedulerExecutor.start();
    }
}