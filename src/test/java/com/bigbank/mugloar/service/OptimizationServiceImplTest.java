package com.bigbank.mugloar.service;

import com.bigbank.mugloar.config.GameProps;
import com.bigbank.mugloar.dto.GameStateDto;
import com.bigbank.mugloar.dto.ItemDto;
import com.bigbank.mugloar.model.Task;
import com.bigbank.mugloar.service.impl.OptimizationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OptimizationServiceImplTest {
    @Mock
    private GameProps gameSettings;

    @InjectMocks
    private OptimizationServiceImpl testObj;

    @Test
    void getOptimalTask_WithReputationAlertFalse_FindsTaskWithHighestReward() {
        boolean reputationAlertFlag = false;

        List<Task> tasks = List.of(
                Task.builder().taskId("1").message("message")
                        .reward(10).expiresIn(3).encrypted(null).probability("Piece of cake").build(),
                Task.builder().taskId("2").message("message")
                        .reward(13).expiresIn(3).encrypted(null).probability("Piece of cake").build(),
                Task.builder().taskId("3").message("message")
                        .reward(16).expiresIn(3).encrypted(null).probability("Piece of cake").build());

        Task optimalTask = testObj.getOptimalTask(tasks, reputationAlertFlag);

        assertThat(optimalTask.getTaskId()).isEqualTo("3");
    }

    @Test
    void getOptimalItems_WhenLifeBelowThreshold_ShouldIncludeHealingPotion() {
        int healingPotionCost = 50;
        int baseLifeThreshold = 3;
        int cheapItemCost = 30;
        int expensiveItemCost = 100;

        var gameStateDto = new GameStateDto("gameId", 1, 99, 2, 200, 0, 50);
        List<ItemDto> shopItems = List.of(
                new ItemDto("hpot", "Healing Potion", 50),
                new ItemDto("cs", "Claw Sharpening", 100),
                new ItemDto("gas", "Gasoline", 100),
                new ItemDto("wax", "Copper Plating", 100)
        );

        when(gameSettings.getHealingPotionCost()).thenReturn(healingPotionCost);
        when(gameSettings.getBaseLifeThreshold()).thenReturn(baseLifeThreshold);
        when(gameSettings.getCheapItemCost()).thenReturn(cheapItemCost);
        when(gameSettings.getExpensiveItemCost()).thenReturn(expensiveItemCost);

        List<ItemDto> optimalItems = testObj.getOptimalItems(shopItems, gameStateDto);

        assertThat(optimalItems).hasSize(1).extracting("id").contains("hpot");
    }

}