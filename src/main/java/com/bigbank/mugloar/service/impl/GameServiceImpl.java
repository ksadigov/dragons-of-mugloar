package com.bigbank.mugloar.service.impl;

import com.bigbank.mugloar.client.MugloarApiClient;
import com.bigbank.mugloar.dto.GameStateDto;
import com.bigbank.mugloar.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {
    private final MugloarApiClient mugloarApiClient;

    @Override
    public GameStateDto playGame() {
        return mugloarApiClient.startNewGame();
    }
}
