package com.bigbank.mugloar.service.impl;

import com.bigbank.mugloar.config.GameProps;
import com.bigbank.mugloar.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {
    private static final List<Integer> GAME_SCORES = Collections.synchronizedList(new ArrayList<>());

    private final GameProps gameSettings;

    @Override
    public void printGameStats() {
        log.info("******************** Batch Game Statistics ****************************");
        log.info("Game Count: {}", getGameScores().size());
        long wins = getGameScores().stream().filter(score -> score >= gameSettings.getFinalScoreThreshold()).count();
        log.info("Games won: {}", wins);
        log.info("Games lost: {}", getGameScores().size() - wins);
        log.info("Min Score: {}", getGameScores().stream().mapToInt(v -> v).min().orElseThrow(NoSuchElementException::new));
        log.info("Max Score: {}", getGameScores().stream().mapToInt(v -> v).max().orElseThrow(NoSuchElementException::new));
        float successRate = (float) wins * 100 / gameSettings.getRetryLimit();
        log.info("Success Rate: {}%", String.format("%.2f", successRate));
        log.info("***********************************************************************");
    }

    @Override
    public void addGameScore(int score) {
        GAME_SCORES.add(score);
    }

    public List<Integer> getGameScores() {
        return GAME_SCORES;
    }
}
