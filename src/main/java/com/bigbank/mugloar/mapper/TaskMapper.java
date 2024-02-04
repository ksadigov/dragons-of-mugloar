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

    @Mapping(target = "taskId", source = "originalTask.taskId")
    @Mapping(target = "message", source = "originalTask.message")
    @Mapping(target = "reward", source = "originalTask.reward")
    @Mapping(target = "expiresIn", source = "originalTask.expiresIn")
    @Mapping(target = "encrypted", source = "originalTask.encrypted")
    @Mapping(target = "probability", source = "originalTask.probability")
    @Mapping(target = "evaluationScore", source = "evaluationScore")
    public abstract Task toTaskWithEvaluationScore(Task originalTask, int evaluationScore);
}
