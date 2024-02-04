package com.bigbank.mugloar.mapper;

import com.bigbank.mugloar.dto.GameStateDto;
import com.bigbank.mugloar.dto.PurchaseResultDto;
import com.bigbank.mugloar.dto.TaskResultDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class GameStateMapper {
    public static final GameStateMapper INSTANCE = Mappers.getMapper(GameStateMapper.class);

    public abstract GameStateDto toGameStateDto(TaskResultDto taskResultDto, String gameId);

    @Mapping(target = "gold", source = "purchaseResultDto.gold")
    @Mapping(target = "lives", source = "purchaseResultDto.lives")
    @Mapping(target = "level", source = "purchaseResultDto.level")
    @Mapping(target = "turn", source = "purchaseResultDto.turn")
    public abstract GameStateDto toGameStateDto(GameStateDto gameStateDto, PurchaseResultDto purchaseResultDto);


}
