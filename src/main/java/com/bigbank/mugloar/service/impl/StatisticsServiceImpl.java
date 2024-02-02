package com.bigbank.mugloar.service.impl;

import com.bigbank.mugloar.config.GameProps;
import com.bigbank.mugloar.dto.GameStateDto;
import com.bigbank.mugloar.dto.PurchaseResultDto;
import com.bigbank.mugloar.dto.TaskResultDto;
import com.bigbank.mugloar.mapper.GameStateMapper;
import com.bigbank.mugloar.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {
    private final GameProps gameSettings;

    @Override
    public void updateGameStateStatsAfterTask(GameStateDto gameStateDto, TaskResultDto taskResultDto) {
        GameStateMapper.INSTANCE.toGameState(gameStateDto, taskResultDto);
    }

    @Override
    public void updateGameStateStatsAfterShopping(GameStateDto gameStateDto, PurchaseResultDto purchaseResultDto) {
        GameStateMapper.INSTANCE.toGameState(gameStateDto, purchaseResultDto);
    }

    @Override
    public void printGameStats(List<Integer> gameScores) {
        log.info("******************** Batch Game Statistics *****************************");
        log.info("Game Count: " + gameScores.size());
        log.info("Min Score: " + gameScores.stream().mapToInt(v -> v).min().orElseThrow(NoSuchElementException::new));
        log.info("Max Score: " + gameScores.stream().mapToInt(v -> v).max().orElseThrow(NoSuchElementException::new));
        int successGameCount = Math.toIntExact(gameScores.stream().filter(score -> score >= gameSettings.getFinalScoreThreshold()).count());
        float successRate = (float) successGameCount * 100 / gameSettings.getRetryLimit();
        log.info("Success Rate: {}%", String.format("%.2f", successRate));
        log.info("***********************************************************************");
    }
}
