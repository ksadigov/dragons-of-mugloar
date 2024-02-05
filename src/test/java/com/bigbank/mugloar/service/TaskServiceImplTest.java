package com.bigbank.mugloar.service;

import com.bigbank.mugloar.client.MugloarApiClient;
import com.bigbank.mugloar.dto.GameStateDto;
import com.bigbank.mugloar.dto.MessageDto;
import com.bigbank.mugloar.dto.TaskResultDto;
import com.bigbank.mugloar.mapper.TaskMapper;
import com.bigbank.mugloar.model.Task;
import com.bigbank.mugloar.service.impl.TaskServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.bigbank.mugloar.util.Constants.GAME_ID;
import static com.bigbank.mugloar.util.Constants.TASK_ID;
import static com.bigbank.mugloar.util.MessageDecryptor.decryptMessages;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {
    @Mock
    private MugloarApiClient mugloarApiClient;

    @Mock
    private OptimizationService optimizationService;

    @InjectMocks
    private TaskServiceImpl testObj;

    @Test
    void getTasks_WithoutDecryptionRequirement_ShouldReturnTasks() {
        List<MessageDto> mockedMessages = List.of(generateMockMessageDtoWithoutEncryption());
        when(mugloarApiClient.getAllMessages(GAME_ID)).thenReturn(mockedMessages);
        List<Task> expectedTasks = TaskMapper.INSTANCE.toTaskList(mockedMessages);

        List<Task> tasks = testObj.getTasks(GAME_ID);

        verify(mugloarApiClient).getAllMessages(GAME_ID);
        assertThat(tasks).isNotNull().isEqualTo(expectedTasks);
    }

    @Test
    void getTasks_WithBase64DecryptionRequirement_ShouldReturnTasks() {
        List<MessageDto> mockedMessages = List.of(generateMockMessageDtoWithBase64());
        when(mugloarApiClient.getAllMessages(GAME_ID)).thenReturn(mockedMessages);
        List<Task> expectedTasks = TaskMapper.INSTANCE.toTaskList(decryptMessages(mockedMessages));

        List<Task> tasks = testObj.getTasks(GAME_ID);

        verify(mugloarApiClient).getAllMessages(GAME_ID);
        assertThat(tasks).isNotNull().isEqualTo(expectedTasks);
    }

    @Test
    void getTasks_WithRot13DecryptionRequirement_ShouldReturnTasks() {
        List<MessageDto> mockedMessages = List.of(generateMockMessageDtoWithRot13());
        when(mugloarApiClient.getAllMessages(GAME_ID)).thenReturn(mockedMessages);
        List<Task> expectedTasks = TaskMapper.INSTANCE.toTaskList(decryptMessages(mockedMessages));

        List<Task> tasks = testObj.getTasks(GAME_ID);

        verify(mugloarApiClient).getAllMessages(GAME_ID);
        assertThat(tasks).isNotNull().isEqualTo(expectedTasks);
    }

    @Test
    void getTasks_WithUnknownDecryptionRequirement_ShouldReturnEmptyTasks() {
        List<MessageDto> mockedMessages = List.of(generateMockMessageDtoWithUnknownDecryption());
        when(mugloarApiClient.getAllMessages(GAME_ID)).thenReturn(mockedMessages);
        List<Task> expectedTasks = TaskMapper.INSTANCE.toTaskList(decryptMessages(mockedMessages));

        List<Task> tasks = testObj.getTasks(GAME_ID);

        verify(mugloarApiClient).getAllMessages(GAME_ID);
        assertThat(tasks).isNotNull().isEqualTo(expectedTasks);
    }

    @Test
    void solveTask_WithGameStateAndTask_ShouldReturnExpectedTaskResultDto() {
        GameStateDto gameStateDto = new GameStateDto(GAME_ID, 3, 100, 3, 63, 0, 5);
        Task task = generateMockTask();
        TaskResultDto expectedTaskResultDto = new TaskResultDto(true, 3, 100, 63, 0, 5, "message");
        when(mugloarApiClient.solveTask(gameStateDto.getGameId(), task.getTaskId())).thenReturn(expectedTaskResultDto);

        TaskResultDto taskResultDto = testObj.solveTask(gameStateDto, task);

        verify(mugloarApiClient).solveTask(gameStateDto.getGameId(), task.getTaskId());
        assertThat(taskResultDto).isNotNull().isEqualTo(expectedTaskResultDto);
    }

    @Test
    void chooseOptimalTask_WhenReputationAlertIsFalse_ShouldReturnOptimalTask() {
        boolean reputationAlertFlag = false;
        List<Task> tasks = List.of(generateMockTask());
        Task expectedTask = tasks.get(0);
        when(optimizationService.findOptimalTask(tasks, reputationAlertFlag)).thenReturn(expectedTask);

        Task optimalTask = testObj.chooseOptimalTask(tasks, reputationAlertFlag);

        verify(optimizationService).findOptimalTask(tasks, reputationAlertFlag);
        assertThat(optimalTask).isNotNull().isEqualTo(expectedTask);
    }

    private Task generateMockTask() {
        return Task.builder().taskId(TASK_ID).message("message")
                .reward(12).expiresIn(3).encrypted(null).probability("Piece of cake").evaluationScore(101).build();
    }

    private MessageDto generateMockMessageDtoWithBase64() {
        return new MessageDto("WnNoVWp4YVo=", "SW5maWx0cmF0ZSBUaGUgWWVsbG93IFNhYmVyIFNpc3Rlcmhvb2QgYW5kIHJlY292ZXIgdGhlaXIgc2VjcmV0cy4=", 10, 5, 1, "U3VpY2lkZSBtaXNzaW9u");
    }

    private MessageDto generateMockMessageDtoWithRot13() {
        return new MessageDto("00W2OKuC", "Xvyy Fgnasbeq Pbafgnoyr jvgu cbgngbrf naq znxr Mnnuve Eraaryy sebz fjnzc va Ybjrgba gb gnxr gur oynzr", 10, 5, 2, "Fhvpvqr zvffvba");
    }

    private MessageDto generateMockMessageDtoWithoutEncryption() {
        return new MessageDto("QwWnKPr2", "description", 10, 5, null, "Piece of cake");
    }

    private MessageDto generateMockMessageDtoWithUnknownDecryption() {
        return new MessageDto("a1a1a1a1a1=", "b2b2b2b2b2b2=", 10, 5, 3, "c3c3c3c3c3c3c");
    }
}