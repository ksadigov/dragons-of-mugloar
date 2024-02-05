package com.bigbank.mugloar;

import com.bigbank.mugloar.config.GameProps;
import com.bigbank.mugloar.game.GameManager;
import com.bigbank.mugloar.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.bigbank.mugloar.constant.Constants.INITIAL_GAME_RETRY_LIMIT;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class MugloarApplication implements CommandLineRunner {
    private final GameManager gameManager;
    private final StatisticsService statisticsService;
    private final GameProps gameSettings;

    public static void main(String[] args) {
        SpringApplication.run(MugloarApplication.class, args);
    }

    @Override
    public void run(String... args) throws InterruptedException {
        int maxGameRuns = Objects.requireNonNullElse(gameSettings.getRetryLimit(), INITIAL_GAME_RETRY_LIMIT);
        log.info("Starting a batch of {} games. Please wait for all results after completion.", maxGameRuns);

        ExecutorService executor = Executors.newFixedThreadPool(gameSettings.getThreadPoolSize());
        AtomicInteger gameNumber = new AtomicInteger(1);

        for (int i = 0; i < maxGameRuns; i++) {
            int currentGameNumber = gameNumber.getAndIncrement();
            executor.submit(() -> gameManager.startGame(currentGameNumber));
        }

        executor.shutdown();
        boolean finished = executor.awaitTermination(1, TimeUnit.HOURS);

        if (finished) {
            statisticsService.printGameStats();
        } else {
            log.error("The games did not finish within the expected time.");
        }
    }

}
