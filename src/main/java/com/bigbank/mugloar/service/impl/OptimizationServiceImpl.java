package com.bigbank.mugloar.service.impl;

import com.bigbank.mugloar.config.GameProps;
import com.bigbank.mugloar.dto.GameStateDto;
import com.bigbank.mugloar.dto.ItemDto;
import com.bigbank.mugloar.model.Probability;
import com.bigbank.mugloar.model.Task;
import com.bigbank.mugloar.service.OptimizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class OptimizationServiceImpl implements OptimizationService {
    private static final String NEGATIVE_REPUTATION_TASK_KEYWORD = "Steal";
    private static final Random random = new Random();

    private final GameProps gameSettings;

    @Override
    public Task getOptimalTask(List<Task> tasks, boolean alertForReputation) {
        calculateOptimalScore(tasks, alertForReputation);
        List<Task> tasksWithBestScores = getTaskWithHighestOptimalScore(tasks);
        return Collections.max(tasksWithBestScores, Comparator.comparing(Task::getReward));
    }

    @Override
    public List<ItemDto> chooseOptimalItems(List<ItemDto> shopItemDtos, GameStateDto gameStateDto) {
        List<ItemDto> bestItemDtos = new ArrayList<>();
        Optional<ItemDto> optionalHealingPotion = getItemByCost(shopItemDtos, gameSettings.getHealingPotionCost());

        if (optionalHealingPotion.isPresent()) {
            ItemDto healingPotion = optionalHealingPotion.get();
            if (gameStateDto.getLives() < gameSettings.getBaseLifeThreshold()) {
                bestItemDtos.add(healingPotion);

                int goldAfterHealingPotion = gameStateDto.getGold() - gameSettings.getHealingPotionCost();
                if (goldAfterHealingPotion >= gameSettings.getCheapItemCost() && goldAfterHealingPotion < gameSettings.getExpensiveItemCost()) {
                    bestItemDtos.add(getRandomlyCheapItem(shopItemDtos));
                } else if (goldAfterHealingPotion >= gameSettings.getExpensiveItemCost()) {
                    bestItemDtos.add(getRandomlyExpensiveItem(shopItemDtos));
                }
            } else if (gameStateDto.getGold() >= gameSettings.getExpensiveItemCost()) {
                bestItemDtos.add(getRandomlyExpensiveItem(shopItemDtos));
            }
        }
        return bestItemDtos;
    }

    private void calculateOptimalScore(List<Task> tasks, boolean alertForReputation) {
        int averageExpiresIn = getAverageExpiresIn(tasks);
        int averageProbabilityValue = getAverageProbabilityValue(tasks);

        tasks.forEach(task -> {
            var taskProbability = Probability.from(task.getProbability());
            int scoreImprovement = 10 - taskProbability.getValue();
            task.setEvaluationScore(task.getEvaluationScore() + scoreImprovement);

            if (shouldIncreaseScoreBasedOnExpiresIn(taskProbability.getValue(), averageProbabilityValue, task.getExpiresIn(), averageExpiresIn)) {
                task.setEvaluationScore(task.getEvaluationScore() + (task.getExpiresIn() - averageExpiresIn));
            }

            if (alertForReputation && task.getMessage().contains(NEGATIVE_REPUTATION_TASK_KEYWORD)) {
                task.setEvaluationScore(0);
            }
        });
    }

    private boolean shouldIncreaseScoreBasedOnExpiresIn(int taskProbabilityValue, int averageProbabilityIndex, int expiresIn, int averageExpiresIn) {
        return taskProbabilityValue <= averageProbabilityIndex && expiresIn >= averageExpiresIn;
    }

    private int getAverageExpiresIn(List<Task> tasks) {
        if (tasks.isEmpty()) {
            return 0;
        }
        return (int) tasks.stream()
                .mapToInt(Task::getExpiresIn)
                .average()
                .orElse(0.0);
    }

    private int getAverageProbabilityValue(List<Task> tasks) {
        return (int) tasks.stream()
                .map(Task::getProbability)
                .map(Probability::from)
                .mapToInt(Probability::getValue)
                .average()
                .orElse(0.0);
    }

    private List<Task> getTaskWithHighestOptimalScore(List<Task> tasks) {
        return getTasksWithScoresOf(getHighestScores(tasks), tasks);
    }

    private int getHighestScores(List<Task> tasks) {
        return tasks
                .stream()
                .map(Task::getEvaluationScore)
                .mapToInt(i -> i)
                .max()
                .orElseThrow(NoSuchElementException::new);
    }

    private List<Task> getTasksWithScoresOf(int maxPoint, List<Task> tasks) {
        return tasks.stream().filter(task -> task.getEvaluationScore() == (maxPoint)).toList();
    }

    private ItemDto getRandomItem(List<ItemDto> itemDtos) {
        return itemDtos.isEmpty() ? null : itemDtos.get(random.nextInt(itemDtos.size()));
    }

    private ItemDto getRandomlyExpensiveItem(List<ItemDto> shopItemDtos) {
        return getRandomItem(getItemsByCost(shopItemDtos, gameSettings.getExpensiveItemCost()));
    }

    private ItemDto getRandomlyCheapItem(List<ItemDto> shopItemDtos) {
        return getRandomItem(getItemsByCost(shopItemDtos, gameSettings.getCheapItemCost()));
    }

    private Optional<ItemDto> getItemByCost(List<ItemDto> shopItemDtos, int cost) {
        return shopItemDtos.stream().filter(shopItemDto -> shopItemDto.getCost() == cost).findFirst();
    }

    private List<ItemDto> getItemsByCost(List<ItemDto> shopItemDtos, int cost) {
        return shopItemDtos.stream().filter(shopItemDto -> shopItemDto.getCost() == cost).toList();
    }
}
