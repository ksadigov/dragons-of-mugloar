package com.bigbank.mugloar.game;

import com.bigbank.mugloar.config.GameProps;
import com.bigbank.mugloar.dto.GameStateDto;
import com.bigbank.mugloar.dto.ReputationDto;
import com.bigbank.mugloar.dto.TaskResultDto;
import com.bigbank.mugloar.model.Probability;
import com.bigbank.mugloar.model.Task;
import com.bigbank.mugloar.service.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static com.bigbank.mugloar.util.Constants.GAME_ID;
import static com.bigbank.mugloar.util.Constants.TASK_ID;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameManagerTest {

    @Mock
    private GameService gameService;
    @Mock
    private ShopService shopService;
    @Mock
    private TaskService taskService;
    @Mock
    private InvestigationService investigationService;
    @Mock
    private GameProps gameSettings;
    @Mock
    private StatisticsService statisticsService;

    @InjectMocks
    private GameManager testObj;


    @Test
    void startGame_ShouldExecuteGameFlowCorrectly() {
        GameStateDto initialGameState = new GameStateDto(GAME_ID, 3, 0, 0, 0, 0, 0);
        GameStateDto gameStateAfterTask = new GameStateDto(GAME_ID, 3, 100, 0, 120, 0, 5);
        Task optimalTask = Task.builder().taskId(TASK_ID).message("message")
                .reward(10).expiresIn(3).encrypted(null).probability(Probability.PIECE_OF_CAKE.getMessage())
                .evaluationScore(10).build();
        TaskResultDto taskResultDto = new TaskResultDto(true, 3, 100, 10, 0, 5, "message");
        ReputationDto reputationDto = new ReputationDto(0, -6, 0);

        when(gameService.playGame()).thenReturn(initialGameState);
        when(taskService.getTasks(anyString())).thenReturn(Collections.singletonList(optimalTask));
        when(taskService.chooseOptimalTask(anyList(), anyBoolean())).thenReturn(optimalTask);
        when(taskService.solveTask(any(GameStateDto.class), any(Task.class))).thenReturn(taskResultDto);
        when(shopService.doShopping(any(GameStateDto.class))).thenReturn(gameStateAfterTask); // Reflect the state change
        when(investigationService.investigate(anyString())).thenReturn(reputationDto);
        when(gameSettings.getFinalScoreThreshold()).thenReturn(20);
        when(gameSettings.getReputationMaxLimit()).thenReturn(-5);

        testObj.startGame(1);

        verify(gameService, times(1)).playGame();
        verify(taskService, atLeastOnce()).getTasks(anyString());
        verify(taskService, atLeastOnce()).chooseOptimalTask(anyList(), anyBoolean());
        verify(taskService, atLeastOnce()).solveTask(any(GameStateDto.class), any(Task.class));
        verify(shopService, atLeastOnce()).doShopping(any(GameStateDto.class));
        verify(investigationService, atLeastOnce()).investigate(anyString());
    }
}
