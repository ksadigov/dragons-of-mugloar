package com.bigbank.mugloar.service;

import com.bigbank.mugloar.dto.GameStateDto;
import com.bigbank.mugloar.model.Task;
import com.bigbank.mugloar.dto.TaskResultDto;

import java.util.List;

public interface TaskService {

    List<Task> getTasks(String gameId);

    TaskResultDto solveTask(GameStateDto gameStateDto, Task task);

    Task chooseOptimalTask(List<Task> taskList, boolean reputationAlertFlag);
}
