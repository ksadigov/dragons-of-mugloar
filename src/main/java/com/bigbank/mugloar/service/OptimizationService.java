package com.bigbank.mugloar.service;

import com.bigbank.mugloar.dto.GameStateDto;
import com.bigbank.mugloar.dto.ItemDto;
import com.bigbank.mugloar.model.Task;

import java.util.List;

public interface OptimizationService {

    Task findOptimalTask(List<Task> tasks, boolean reputationAlertFlag);

    List<ItemDto> findOptimalItems(List<ItemDto> shopItemDtos, GameStateDto gameStateDto);

}
