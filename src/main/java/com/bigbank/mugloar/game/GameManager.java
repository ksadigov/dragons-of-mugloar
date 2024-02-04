package com.bigbank.mugloar.game;

import com.bigbank.mugloar.config.GameProps;
import com.bigbank.mugloar.mapper.GameStateMapper;
import com.bigbank.mugloar.service.GameService;
import com.bigbank.mugloar.service.InvestigationService;
import com.bigbank.mugloar.service.ShopService;
import com.bigbank.mugloar.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameManager {
    private static final List<Integer> GAME_SCORES = Collections.synchronizedList(new ArrayList<>());

    private final GameService gameService;
    private final ShopService shopService;
    private final TaskService taskService;
    private final InvestigationService investigationService;
    private final GameProps gameSettings;

    public void startGame(int gameNumber) {
        boolean reputationAlertFlag = false;

        var gameState = gameService.playGame();
        var gameId = gameState.getGameId();

        do {
            var tasks = taskService.getTasks(gameId);
            var optimalTask = taskService.chooseOptimalTask(tasks, reputationAlertFlag);
            var taskResultDto = taskService.solveTask(gameState, optimalTask);
            gameState = GameStateMapper.INSTANCE.toGameStateDto(taskResultDto, gameId);
            if (gameState.getLives() > 0) {
                shopService.doShopping(gameState);
                var reputationDto = investigationService.investigate(gameId);
                if (reputationDto.getState() < gameSettings.getReputationMaxLimit()) {
                    reputationAlertFlag = true;
                }
            }
        } while (gameState.getScore() < gameSettings.getFinalScoreThreshold() && gameState.getLives() > 0);

        addGameScore(gameState.getScore());
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

    public List<Integer> getGameScores() {
        return Collections.unmodifiableList(GAME_SCORES);
    }

    private void addGameScore(int score) {
        GAME_SCORES.add(score);
    }
}
