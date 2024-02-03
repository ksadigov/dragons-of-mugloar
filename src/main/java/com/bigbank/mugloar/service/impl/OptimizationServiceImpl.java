package com.bigbank.mugloar.service.impl;

import com.bigbank.mugloar.config.GameProps;
import com.bigbank.mugloar.dto.GameStateDto;
import com.bigbank.mugloar.dto.ItemDto;
import com.bigbank.mugloar.mapper.TaskMapper;
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
        var tasksWithEvaluationScores = generateTasksWithEvaluationScores(tasks, alertForReputation);
        var tasksWithBestEvaluationScores = getTasksWithHighestEvaluationScore(tasksWithEvaluationScores);
        return Collections.max(tasksWithBestEvaluationScores, Comparator.comparing(Task::getReward));
    }

    @Override
    public List<ItemDto> getOptimalItems(List<ItemDto> shopItemDtos, GameStateDto gameStateDto) {
        List<ItemDto> bestItems = new ArrayList<>();
        addHealingPotionIfNecessary(shopItemDtos, gameStateDto).ifPresent(bestItems::add);
        addAdditionalItemBasedOnGold(shopItemDtos, gameStateDto).ifPresent(bestItems::add);
        return bestItems;
    }

    private Optional<ItemDto> addHealingPotionIfNecessary(List<ItemDto> shopItemDtos, GameStateDto gameStateDto) {
        if (gameStateDto.getLives() < gameSettings.getBaseLifeThreshold()) {
            return getItemByCost(shopItemDtos, gameSettings.getHealingPotionCost());
        }
        return Optional.empty();
    }

    private Optional<ItemDto> addAdditionalItemBasedOnGold(List<ItemDto> shopItemDtos, GameStateDto gameStateDto) {
        int goldAfterPossiblePurchase = gameStateDto.getGold() - gameSettings.getHealingPotionCost();
        if (goldAfterPossiblePurchase >= gameSettings.getCheapItemCost() &&
                goldAfterPossiblePurchase < gameSettings.getExpensiveItemCost()) {
            return Optional.of(getRandomlyCheapItem(shopItemDtos));
        } else if (goldAfterPossiblePurchase >= gameSettings.getExpensiveItemCost()) {
            return Optional.of(getRandomlyExpensiveItem(shopItemDtos));
        }
        return Optional.empty();
    }

    private List<Task> generateTasksWithEvaluationScores(List<Task> tasks, boolean alertForReputation) {
        int averageExpiresIn = getAverageExpiresIn(tasks);
        int averageProbabilityValue = getAverageProbabilityValue(tasks);

        return tasks.stream().map(task -> {
            int evaluationScore = calculateEvaluationScore(task, averageExpiresIn, averageProbabilityValue, alertForReputation);
            return createNewTaskWithEvaluationScore(task, evaluationScore);
        }).toList();
    }

    private Task createNewTaskWithEvaluationScore(Task originalTask, int evaluationScore) {
        return TaskMapper.INSTANCE.toTaskWithEvaluationScore(originalTask, evaluationScore);
    }

    private int calculateEvaluationScore(Task task, int averageExpiresIn, int averageProbabilityValue, boolean alertForReputation) {
        var taskProbability = Probability.from(task.getProbability());
        int scoreImprovement = 10 - taskProbability.getValue();
        int evaluationScore = task.getEvaluationScore() + scoreImprovement;

        if (shouldIncreaseScoreBasedOnExpiresIn(taskProbability.getValue(), averageProbabilityValue, task.getExpiresIn(), averageExpiresIn)) {
            evaluationScore += (task.getExpiresIn() - averageExpiresIn);
        }

        if (alertForReputation && task.getMessage().contains(NEGATIVE_REPUTATION_TASK_KEYWORD)) {
            evaluationScore = 0;
        }

        return evaluationScore;
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

    private List<Task> getTasksWithHighestEvaluationScore(List<Task> tasks) {
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
