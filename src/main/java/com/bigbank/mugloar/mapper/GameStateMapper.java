package com.bigbank.mugloar.mapper;

import com.bigbank.mugloar.dto.GameStateDto;
import com.bigbank.mugloar.dto.PurchaseResultDto;
import com.bigbank.mugloar.dto.TaskResultDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class GameStateMapper {
    public static final GameStateMapper INSTANCE = Mappers.getMapper(GameStateMapper.class);

    public abstract void toGameState(@MappingTarget GameStateDto gameStateDto, TaskResultDto taskResultDto);

    public abstract void toGameState(@MappingTarget GameStateDto gameStateDto, PurchaseResultDto purchaseResultDto);


}
