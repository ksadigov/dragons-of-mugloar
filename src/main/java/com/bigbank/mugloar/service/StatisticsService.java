package com.bigbank.mugloar.service;

import com.bigbank.mugloar.dto.GameStateDto;
import com.bigbank.mugloar.dto.PurchaseResultDto;
import com.bigbank.mugloar.dto.TaskResultDto;

import java.util.List;

public interface StatisticsService {
    void updateGameStateStatsAfterTask(GameStateDto gameStateDto, TaskResultDto taskResultDto);

    void updateGameStateStatsAfterShopping(GameStateDto gameStateDto, PurchaseResultDto purchaseResultDto);

    void printGameStats(List<Integer> gameScores);

}
