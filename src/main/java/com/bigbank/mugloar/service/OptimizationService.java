package com.bigbank.mugloar.service;

import com.bigbank.mugloar.dto.GameStateDto;
import com.bigbank.mugloar.dto.ItemDto;
import com.bigbank.mugloar.model.Task;

import java.util.List;

public interface OptimizationService {

    Task getOptimalTask(List<Task> tasks, boolean alertForReputation);

    List<ItemDto> chooseOptimalItems(List<ItemDto> shopItemDtos, GameStateDto gameStateDto);

}
