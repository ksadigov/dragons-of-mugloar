package com.bigbank.mugloar.client;

import com.bigbank.mugloar.dto.*;
import com.bigbank.mugloar.exception.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static com.bigbank.mugloar.client.MugloarApiClient.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MugloarApiClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private MugloarApiClient testObj;

    @Test
    void startNewGame_ShouldReturnGameStateDto() {
        GameStateDto expectedDto = new GameStateDto("gameId", 3, 0, 0, 0, 0, 0);
        when(restTemplate.exchange(
                eq(MugloarApiClient.START_NEW_GAME_ENDPOINT),
                eq(HttpMethod.POST),
                any(),
                eq(GameStateDto.class))
        ).thenReturn(ResponseEntity.ok(expectedDto));

        GameStateDto actualDto = testObj.startNewGame();

        assertThat(actualDto).isNotNull().isEqualTo(expectedDto);
    }

    @Test
    void startNewGame_WhenError_ShouldThrowFailedStartNewGameException() {
        when(restTemplate.exchange(
                eq(MugloarApiClient.START_NEW_GAME_ENDPOINT),
                eq(HttpMethod.POST),
                any(),
                eq(GameStateDto.class))
        ).thenThrow(new RuntimeException("Connection error"));

        assertThatThrownBy(() -> testObj.startNewGame())
                .isInstanceOf(FailedStartNewGameException.class)
                .hasMessageContaining("Failed to start a new game. Something went wrong.");
    }

    @Test
    void runInvestigation_ShouldReturnReputationDto() {
        String gameId = "gameId";
        ReputationDto expectedDto = new ReputationDto(0, 0, 0);
        when(restTemplate.exchange(
                eq(UriComponentsBuilder.fromPath(MugloarApiClient.RUN_INVESTIGATION_ENDPOINT).buildAndExpand(gameId).toString()),
                eq(HttpMethod.POST),
                any(),
                eq(ReputationDto.class))
        ).thenReturn(ResponseEntity.ok(expectedDto));

        ReputationDto actualDto = testObj.runInvestigation(gameId);

        assertThat(actualDto).isNotNull().isEqualTo(expectedDto);
    }

    @Test
    void runInvestigation_WhenError_ShouldThrowFailedRunInvestigationException() {
        String gameId = "gameId";
        when(restTemplate.exchange(
                eq(UriComponentsBuilder.fromPath(MugloarApiClient.RUN_INVESTIGATION_ENDPOINT).buildAndExpand(gameId).toString()),
                eq(HttpMethod.POST),
                any(),
                eq(ReputationDto.class))
        ).thenThrow(new RuntimeException("Connection error"));

        assertThatThrownBy(() -> testObj.runInvestigation(gameId))
                .isInstanceOf(FailedRunInvestigationException.class)
                .hasMessageContaining("Failed to get reputation for gameId=" + gameId);
    }

    @Test
    void getAllMessages_ShouldReturnListOfMessages() {
        String gameId = "gameId";
        List<MessageDto> expectedMessages = List.of(new MessageDto("QwWnKPr2", "description", 10, 5, null, "Piece of cake"));
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(),
                any(ParameterizedTypeReference.class))
        ).thenReturn(new ResponseEntity<>(expectedMessages, HttpStatus.OK));

        List<MessageDto> actualMessages = testObj.getAllMessages(gameId);

        assertThat(actualMessages).isNotNull().isEqualTo(expectedMessages);
    }

    @Test
    void getAllMessages_WhenError_ShouldThrowFailedGetAllMessagesException() {
        String gameId = "gameId";
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(),
                any(ParameterizedTypeReference.class))
        ).thenThrow(new RuntimeException("Connection error"));

        assertThatThrownBy(() -> testObj.getAllMessages(gameId))
                .isInstanceOf(FailedGetAllMessagesException.class)
                .hasMessageContaining("Failed to get all messages for gameId=" + gameId);
    }

    @Test
    void solveTask_ShouldReturnTaskResult() {
        String gameId = "gameId";
        String taskId = "taskId";
        TaskResultDto expectedTaskResult = new TaskResultDto(true, 3, 100, 63, 0, 5, "message");
        when(restTemplate.exchange(
                eq(UriComponentsBuilder.fromPath(SOLVE_TASK_ENDPOINT).buildAndExpand(gameId, taskId).toString()),
                eq(HttpMethod.POST),
                any(),
                eq(TaskResultDto.class))
        ).thenReturn(new ResponseEntity<>(expectedTaskResult, HttpStatus.OK));

        TaskResultDto actualTaskResult = testObj.solveTask(gameId, taskId);

        assertThat(actualTaskResult).isNotNull().isEqualTo(expectedTaskResult);
    }

    @Test
    void solveTask_WhenError_ShouldThrowFailedSolveTaskException() {
        String gameId = "gameId";
        String taskId = "taskId";
        when(restTemplate.exchange(
                eq(UriComponentsBuilder.fromPath(SOLVE_TASK_ENDPOINT).buildAndExpand(gameId, taskId).toString()),
                eq(HttpMethod.POST),
                any(),
                eq(TaskResultDto.class))
        ).thenThrow(new RuntimeException("Connection error"));

        assertThatThrownBy(() -> testObj.solveTask(gameId, taskId))
                .isInstanceOf(FailedSolveTaskException.class)
                .hasMessageContaining("Failed to solve taskId=" + taskId + " for gameId= " + gameId);
    }

    @Test
    void getAllShopItems_ShouldReturnListOfItems() {
        String gameId = "gameId";
        List<ItemDto> expectedItems = List.of(
                new ItemDto("hpot", "Healing Potion", 50),
                new ItemDto("cs", "Claw Sharpening", 100));
        when(restTemplate.exchange(
                eq(UriComponentsBuilder.fromPath(GET_ALL_SHOP_ITEMS_ENDPOINT).buildAndExpand(gameId).toString()),
                eq(HttpMethod.GET),
                any(),
                any(ParameterizedTypeReference.class))
        ).thenReturn(new ResponseEntity<>(expectedItems, HttpStatus.OK));

        List<ItemDto> actualItems = testObj.getAllShopItems(gameId);

        assertThat(actualItems).isNotNull().isEqualTo(expectedItems);
    }

    @Test
    void getAllShopItems_WhenError_ShouldThrowFailedGetAllShopItemsException() {
        String gameId = "gameId";
        when(restTemplate.exchange(
                eq(UriComponentsBuilder.fromPath(GET_ALL_SHOP_ITEMS_ENDPOINT).buildAndExpand(gameId).toString()),
                eq(HttpMethod.GET),
                any(),
                any(ParameterizedTypeReference.class))
        ).thenThrow(new RuntimeException("Connection error"));

        assertThatThrownBy(() -> testObj.getAllShopItems(gameId))
                .isInstanceOf(FailedGetAllShopItemsException.class)
                .hasMessageContaining("Failed to get all shop items for gameId=" + gameId);
    }

    @Test
    void purchaseItem_ShouldReturnPurchaseResult() {
        String gameId = "gameId";
        String itemId = "itemId";
        PurchaseResultDto expectedPurchaseResult = new PurchaseResultDto(true, 1, 3, 1, 1);
        when(restTemplate.exchange(
                eq(UriComponentsBuilder.fromPath(PURCHASE_AN_ITEM_ENDPOINT).buildAndExpand(gameId, itemId).toString()),
                eq(HttpMethod.POST),
                any(),
                eq(PurchaseResultDto.class))
        ).thenReturn(new ResponseEntity<>(expectedPurchaseResult, HttpStatus.OK));

        PurchaseResultDto actualPurchaseResult = testObj.purchaseItem(gameId, itemId);

        assertThat(actualPurchaseResult).isNotNull().isEqualTo(expectedPurchaseResult);
    }

    @Test
    void purchaseItem_WhenError_ShouldThrowFailedPurchaseItemException() {
        String gameId = "gameId";
        String itemId = "itemId";
        when(restTemplate.exchange(
                eq(UriComponentsBuilder.fromPath(PURCHASE_AN_ITEM_ENDPOINT).buildAndExpand(gameId, itemId).toString()),
                eq(HttpMethod.POST),
                any(),
                eq(PurchaseResultDto.class))
        ).thenThrow(new RuntimeException("Connection error"));

        assertThatThrownBy(() -> testObj.purchaseItem(gameId, itemId))
                .isInstanceOf(FailedPurchaseItemException.class)
                .hasMessageContaining("Failed to purchase an itemId=" + itemId + " for gameId=" + gameId);
    }
}

