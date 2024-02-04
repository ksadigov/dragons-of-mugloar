package com.bigbank.mugloar.service;

import com.bigbank.mugloar.dto.GameStateDto;
import com.bigbank.mugloar.dto.PurchaseResultDto;

import java.util.List;

public interface StatisticsService {
    void updateGameStateStatsAfterShopping(GameStateDto gameStateDto, PurchaseResultDto purchaseResultDto);

    void printGameStats(List<Integer> gameScores);

}
