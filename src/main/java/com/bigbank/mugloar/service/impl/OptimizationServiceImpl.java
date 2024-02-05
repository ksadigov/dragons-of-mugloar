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

import static com.bigbank.mugloar.constant.Constants.NEGATIVE_REPUTATION_TASK_KEYWORD;

@Slf4j
@Service
@RequiredArgsConstructor
public class OptimizationServiceImpl implements OptimizationService {
    private static final Random random = new Random();

    private final GameProps gameSettings;

    @Override
    public Task findOptimalTask(List<Task> tasks, boolean reputationAlertFlag) {
        var tasksWithEvaluationScores = generateTasksWithEvaluationScores(tasks, reputationAlertFlag);
        var tasksWithMaxEvaluationScores = findTasksWithMaxEvaluationScore(tasksWithEvaluationScores);
        return Collections.max(tasksWithMaxEvaluationScores, Comparator.comparing(Task::getReward));
    }

    @Override
    public List<ItemDto> findOptimalItems(List<ItemDto> shopItemDtos, GameStateDto gameStateDto) {
        List<ItemDto> optimalItems = new ArrayList<>();

        findAndAddHealingPotionIfRequired(shopItemDtos, gameStateDto, gameSettings.getHealingPotionCost(), gameSettings.getBaseLifeThreshold())
                .ifPresent(optimalItems::add);
        findAndAddItemWithinBudget(shopItemDtos, gameStateDto.getGold() - gameSettings.getHealingPotionCost())
                .ifPresent(optimalItems::add);
        return optimalItems;
    }

    private Optional<ItemDto> findAndAddHealingPotionIfRequired(List<ItemDto> shopItemDtos, GameStateDto gameStateDto, int healingPotionCost, int lifeThreshold) {
        if (gameStateDto.getLives() < lifeThreshold) {
            return getItemByCost(shopItemDtos, healingPotionCost);
        }
        return Optional.empty();
    }

    private Optional<ItemDto> findAndAddItemWithinBudget(List<ItemDto> shopItemDtos, int remainingGold) {
        if (remainingGold >= gameSettings.getCheapItemCost() && remainingGold < gameSettings.getExpensiveItemCost()) {
            return Optional.ofNullable(getRandomItemByCostRange(shopItemDtos, gameSettings.getCheapItemCost()));
        } else if (remainingGold >= gameSettings.getExpensiveItemCost()) {
            return Optional.ofNullable(getRandomItemByCostRange(shopItemDtos, gameSettings.getExpensiveItemCost()));
        }
        return Optional.empty();
    }

    private ItemDto getRandomItemByCostRange(List<ItemDto> shopItemDtos, int costThreshold) {
        List<ItemDto> filteredItems = shopItemDtos.stream()
                .filter(item -> item.getCost() == costThreshold)
                .toList();
        return filteredItems.isEmpty() ? null : filteredItems.get(random.nextInt(filteredItems.size()));
    }

    private List<Task> generateTasksWithEvaluationScores(List<Task> tasks, boolean reputationAlertFlag) {
        int averageExpiresIn = calculateAverageExpiresIn(tasks);
        int averageProbabilityValue = calculateAverageProbabilityValue(tasks);

        return tasks.stream().map(task -> {
            int evaluationScore = calculateEvaluationScore(task, averageExpiresIn, averageProbabilityValue, reputationAlertFlag);
            return createTaskWithEvaluationScore(task, evaluationScore);
        }).toList();
    }

    private Task createTaskWithEvaluationScore(Task originalTask, int evaluationScore) {
        return TaskMapper.INSTANCE.toTaskWithEvaluationScore(originalTask, evaluationScore);
    }

    private int calculateEvaluationScore(Task task, int averageExpiresIn, int averageProbabilityValue, boolean reputationAlertFlag) {
        var taskProbability = Probability.from(task.getProbability());
        int scoreImprovement = 10 - taskProbability.getValue();
        int evaluationScore = task.getEvaluationScore() + scoreImprovement;

        if (isEligibleForScoreBoost(taskProbability.getValue(), averageProbabilityValue, task.getExpiresIn(), averageExpiresIn)) {
            evaluationScore += (task.getExpiresIn() - averageExpiresIn);
        }

        if (reputationAlertFlag && task.getMessage().contains(NEGATIVE_REPUTATION_TASK_KEYWORD)) {
            evaluationScore = 0;
        }

        return evaluationScore;
    }

    private boolean isEligibleForScoreBoost(int taskProbabilityValue, int averageProbabilityIndex, int expiresIn, int averageExpiresIn) {
        return taskProbabilityValue <= averageProbabilityIndex && expiresIn >= averageExpiresIn;
    }

    private int calculateAverageExpiresIn(List<Task> tasks) {
        if (tasks.isEmpty()) {
            return 0;
        }
        return (int) tasks.stream()
                .mapToInt(Task::getExpiresIn)
                .average()
                .orElse(0);
    }

    private int calculateAverageProbabilityValue(List<Task> tasks) {
        return (int) tasks.stream()
                .map(Task::getProbability)
                .map(Probability::from)
                .mapToInt(Probability::getValue)
                .average()
                .orElse(0);
    }

    private List<Task> findTasksWithMaxEvaluationScore(List<Task> tasks) {
        return filterTasksWithMaxEvaluationScore(findMaxEvaluationScore(tasks), tasks);
    }

    private int findMaxEvaluationScore(List<Task> tasks) {
        return tasks
                .stream()
                .map(Task::getEvaluationScore)
                .mapToInt(i -> i)
                .max()
                .orElseThrow(NoSuchElementException::new);
    }

    private List<Task> filterTasksWithMaxEvaluationScore(int maxPoint, List<Task> tasks) {
        return tasks.stream().filter(task -> task.getEvaluationScore() == (maxPoint)).toList();
    }

    private Optional<ItemDto> getItemByCost(List<ItemDto> shopItemDtos, int cost) {
        return shopItemDtos.stream().filter(shopItemDto -> shopItemDto.getCost() == cost).findFirst();
    }
}
