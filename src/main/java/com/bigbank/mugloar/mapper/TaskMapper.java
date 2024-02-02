package com.bigbank.mugloar.mapper;

import com.bigbank.mugloar.dto.MessageDto;
import com.bigbank.mugloar.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class TaskMapper {
    public static final TaskMapper INSTANCE = Mappers.getMapper(TaskMapper.class);

    @Mapping(target = "taskId", source = "messageId")
    @Mapping(target = "message", source = "messageText")
    public abstract Task toTask(MessageDto messageDto);

    public abstract List<Task> toTaskList(List<MessageDto> messageDtos);
}
