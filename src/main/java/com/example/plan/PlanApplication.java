package com.example.plan;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class PlanApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlanApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(PlanApplication.class, args);
        executeFixedRate();
        LOGGER.info("调度开始");
    }


    public static void executeFixedRate() {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleWithFixedDelay(
                new schedulerMain.InspectionProcess(),
                0,
                1,
                TimeUnit.MINUTES);

    }
}
