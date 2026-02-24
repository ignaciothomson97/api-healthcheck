package cl.apihealthcheck.records;

import java.util.concurrent.TimeUnit;

public record ScheduledJob(
        String name,
        Runnable action,
        long initialDelay,
        long period,
        TimeUnit timeUnit
) {}
