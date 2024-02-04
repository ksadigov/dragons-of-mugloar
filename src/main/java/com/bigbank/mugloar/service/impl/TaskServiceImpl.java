package com.bigbank.mugloar.service.impl;

import com.bigbank.mugloar.client.MugloarApiClient;
import com.bigbank.mugloar.dto.GameStateDto;
import com.bigbank.mugloar.dto.TaskResultDto;
import com.bigbank.mugloar.mapper.TaskMapper;
import com.bigbank.mugloar.model.Task;
import com.bigbank.mugloar.service.OptimizationService;
import com.bigbank.mugloar.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.bigbank.mugloar.util.MessageDecryptor.decryptMessages;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final MugloarApiClient mugloarApiClient;
    private final OptimizationService optimizationService;

    @Override
    public List<Task> getTasks(String gameId) {
        var decryptedMessageDtos = decryptMessages(mugloarApiClient.getAllMessages(gameId));
        return TaskMapper.INSTANCE.toTaskList(decryptedMessageDtos);
    }

    @Override
    public TaskResultDto solveTask(GameStateDto gameStateDto, Task task) {
        return mugloarApiClient.solveTask(gameStateDto.getGameId(), task.getTaskId());
    }

    @Override
    public Task chooseOptimalTask(List<Task> tasks, boolean reputationAlertFlag) {
        return optimizationService.getOptimalTask(tasks, reputationAlertFlag);
    }
}
