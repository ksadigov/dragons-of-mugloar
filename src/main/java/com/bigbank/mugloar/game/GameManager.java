package com.bigbank.mugloar.game;

import com.bigbank.mugloar.config.GameProps;
import com.bigbank.mugloar.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.bigbank.mugloar.constant.Constants.GAME_SCORES;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameManager {
    private final GameService gameService;
    private final ShopService shopService;
    private final TaskService taskService;
    private final InvestigationService investigationService;
    private final StatisticsService statisticsService;
    private final GameProps gameSettings;

    public void startGame(int gameNumber) {
        boolean reputationAlertFlag = false;

        var gameState = gameService.playGame();
        var gameId = gameState.getGameId();

        do {
            var tasks = taskService.getTasks(gameId);
            var optimalTask = taskService.chooseOptimalTask(tasks, reputationAlertFlag);
            var taskResultDto = taskService.solveTask(gameState, optimalTask);
            statisticsService.updateGameStateStatsAfterTask(gameState, taskResultDto);
            if (gameState.getLives() > 0) {
                shopService.doShopping(gameState);
                var reputationDto = investigationService.investigate(gameId);
                if (reputationDto.getState() < gameSettings.getReputationMaxLimit()) {
                    reputationAlertFlag = true;
                }
            }
        } while (gameState.getScore() < gameSettings.getFinalScoreThreshold() && gameState.getLives() > 0);

        GAME_SCORES.add(gameState.getScore());
        log.info("Game {} finished. Details - Score: {}, High Score: {}, Lives: {}, Gold: {}, Level: {}, Turn: {}, Game ID: {}",
                gameNumber,
                gameState.getScore(),
                gameState.getHighScore(),
                gameState.getLives(),
                gameState.getGold(),
                gameState.getLevel(),
                gameState.getTurn(),
                gameState.getGameId());

    }
}
