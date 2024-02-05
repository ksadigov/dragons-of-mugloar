package com.bigbank.mugloar.client;

import com.bigbank.mugloar.dto.*;
import com.bigbank.mugloar.exception.*;
import com.bigbank.mugloar.model.Probability;
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
import static com.bigbank.mugloar.util.Constants.GAME_ID;
import static com.bigbank.mugloar.util.Constants.TASK_ID;
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
        GameStateDto expectedDto = new GameStateDto(GAME_ID, 3, 0, 0, 0, 0, 0);
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
        ReputationDto expectedDto = new ReputationDto(0, 0, 0);
        when(restTemplate.exchange(
                eq(UriComponentsBuilder.fromPath(MugloarApiClient.RUN_INVESTIGATION_ENDPOINT).buildAndExpand(GAME_ID).toString()),
                eq(HttpMethod.POST),
                any(),
                eq(ReputationDto.class))
        ).thenReturn(ResponseEntity.ok(expectedDto));

        ReputationDto actualDto = testObj.runInvestigation(GAME_ID);

        assertThat(actualDto).isNotNull().isEqualTo(expectedDto);
    }

    @Test
    void runInvestigation_WhenError_ShouldThrowFailedRunInvestigationException() {
        when(restTemplate.exchange(
                eq(UriComponentsBuilder.fromPath(MugloarApiClient.RUN_INVESTIGATION_ENDPOINT).buildAndExpand(GAME_ID).toString()),
                eq(HttpMethod.POST),
                any(),
                eq(ReputationDto.class))
        ).thenThrow(new RuntimeException("Connection error"));

        assertThatThrownBy(() -> testObj.runInvestigation(GAME_ID))
                .isInstanceOf(FailedRunInvestigationException.class)
                .hasMessageContaining("Failed to get reputation for gameId=" + GAME_ID);
    }

    @Test
    void getAllMessages_ShouldReturnListOfMessages() {
        List<MessageDto> expectedMessages = List.of(new MessageDto(TASK_ID, "description", 10, 5, null, Probability.PIECE_OF_CAKE.getMessage()));
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(),
                any(ParameterizedTypeReference.class))
        ).thenReturn(new ResponseEntity<>(expectedMessages, HttpStatus.OK));

        List<MessageDto> actualMessages = testObj.getAllMessages(GAME_ID);

        assertThat(actualMessages).isNotNull().isEqualTo(expectedMessages);
    }

    @Test
    void getAllMessages_WhenError_ShouldThrowFailedGetAllMessagesException() {
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(),
                any(ParameterizedTypeReference.class))
        ).thenThrow(new RuntimeException("Connection error"));

        assertThatThrownBy(() -> testObj.getAllMessages(GAME_ID))
                .isInstanceOf(FailedGetAllMessagesException.class)
                .hasMessageContaining("Failed to get all messages for gameId=" + GAME_ID);
    }

    @Test
    void solveTask_ShouldReturnTaskResult() {
        TaskResultDto expectedTaskResult = new TaskResultDto(true, 3, 100, 63, 0, 5, "message");
        when(restTemplate.exchange(
                eq(UriComponentsBuilder.fromPath(SOLVE_TASK_ENDPOINT).buildAndExpand(GAME_ID, TASK_ID).toString()),
                eq(HttpMethod.POST),
                any(),
                eq(TaskResultDto.class))
        ).thenReturn(new ResponseEntity<>(expectedTaskResult, HttpStatus.OK));

        TaskResultDto actualTaskResult = testObj.solveTask(GAME_ID, TASK_ID);

        assertThat(actualTaskResult).isNotNull().isEqualTo(expectedTaskResult);
    }

    @Test
    void solveTask_WhenError_ShouldThrowFailedSolveTaskException() {
        when(restTemplate.exchange(
                eq(UriComponentsBuilder.fromPath(SOLVE_TASK_ENDPOINT).buildAndExpand(GAME_ID, TASK_ID).toString()),
                eq(HttpMethod.POST),
                any(),
                eq(TaskResultDto.class))
        ).thenThrow(new RuntimeException("Connection error"));

        assertThatThrownBy(() -> testObj.solveTask(GAME_ID, TASK_ID))
                .isInstanceOf(FailedSolveTaskException.class)
                .hasMessageContaining("Failed to solve taskId=" + TASK_ID + " for gameId= " + GAME_ID);
    }

    @Test
    void getAllShopItems_ShouldReturnListOfItems() {
        List<ItemDto> expectedItems = List.of(
                new ItemDto("hpot", "Healing Potion", 50),
                new ItemDto("cs", "Claw Sharpening", 100));
        when(restTemplate.exchange(
                eq(UriComponentsBuilder.fromPath(GET_ALL_SHOP_ITEMS_ENDPOINT).buildAndExpand(GAME_ID).toString()),
                eq(HttpMethod.GET),
                any(),
                any(ParameterizedTypeReference.class))
        ).thenReturn(new ResponseEntity<>(expectedItems, HttpStatus.OK));

        List<ItemDto> actualItems = testObj.getAllShopItems(GAME_ID);

        assertThat(actualItems).isNotNull().isEqualTo(expectedItems);
    }

    @Test
    void getAllShopItems_WhenError_ShouldThrowFailedGetAllShopItemsException() {
        when(restTemplate.exchange(
                eq(UriComponentsBuilder.fromPath(GET_ALL_SHOP_ITEMS_ENDPOINT).buildAndExpand(GAME_ID).toString()),
                eq(HttpMethod.GET),
                any(),
                any(ParameterizedTypeReference.class))
        ).thenThrow(new RuntimeException("Connection error"));

        assertThatThrownBy(() -> testObj.getAllShopItems(GAME_ID))
                .isInstanceOf(FailedGetAllShopItemsException.class)
                .hasMessageContaining("Failed to get all shop items for gameId=" + GAME_ID);
    }

    @Test
    void purchaseItem_ShouldReturnPurchaseResult() {
        PurchaseResultDto expectedPurchaseResult = new PurchaseResultDto(true, 1, 3, 1, 1);
        when(restTemplate.exchange(
                eq(UriComponentsBuilder.fromPath(PURCHASE_AN_ITEM_ENDPOINT).buildAndExpand(GAME_ID, TASK_ID).toString()),
                eq(HttpMethod.POST),
                any(),
                eq(PurchaseResultDto.class))
        ).thenReturn(new ResponseEntity<>(expectedPurchaseResult, HttpStatus.OK));

        PurchaseResultDto actualPurchaseResult = testObj.purchaseItem(GAME_ID, TASK_ID);

        assertThat(actualPurchaseResult).isNotNull().isEqualTo(expectedPurchaseResult);
    }

    @Test
    void purchaseItem_WhenError_ShouldThrowFailedPurchaseItemException() {
        when(restTemplate.exchange(
                eq(UriComponentsBuilder.fromPath(PURCHASE_AN_ITEM_ENDPOINT).buildAndExpand(GAME_ID, TASK_ID).toString()),
                eq(HttpMethod.POST),
                any(),
                eq(PurchaseResultDto.class))
        ).thenThrow(new RuntimeException("Connection error"));

        assertThatThrownBy(() -> testObj.purchaseItem(GAME_ID, TASK_ID))
                .isInstanceOf(FailedPurchaseItemException.class)
                .hasMessageContaining("Failed to purchase an itemId=" + TASK_ID + " for gameId=" + GAME_ID);
    }
}

