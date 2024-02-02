package com.bigbank.mugloar.client;

import com.bigbank.mugloar.dto.*;
import com.bigbank.mugloar.exception.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class MugloarApiClient {

    public static final String START_NEW_GAME_ENDPOINT = "game/start";
    public static final String GET_ALL_MESSAGES_ENDPOINT = "{gameId}/messages";
    public static final String SOLVE_TASK_ENDPOINT = "{gameId}/solve/{taskId}";
    public static final String GET_ALL_SHOP_ITEMS_ENDPOINT = "{gameId}/shop";
    public static final String PURCHASE_AN_ITEM_ENDPOINT = "{gameId}/shop/buy/{itemId}";
    public static final String RUN_INVESTIGATION_ENDPOINT = "{gameId}/investigate/reputation";

    private final RestTemplate mugloarRestTemplate;

    public GameStateDto startNewGame() {
        GameStateDto gameStateDto;
        try {
            UriComponents url = UriComponentsBuilder.fromPath(START_NEW_GAME_ENDPOINT).build();
            var responseEntity = mugloarRestTemplate.exchange(url.toString(), HttpMethod.POST, null, GameStateDto.class);
            gameStateDto = responseEntity.getBody();
        } catch (HttpClientErrorException ex) {
            String errorMessage = "Failed to start a new game. Something went wrong.";
            log.error(errorMessage, ex);
            throw new FailedStartNewGameException(errorMessage, ex);
        }
        return gameStateDto;
    }

    public ReputationDto runInvestigation(String gameId) {
        ReputationDto reputationDto;
        try {
            UriComponents url = UriComponentsBuilder.fromPath(RUN_INVESTIGATION_ENDPOINT).buildAndExpand(gameId);
            var responseEntity = mugloarRestTemplate.exchange(url.toString(), HttpMethod.POST, null, ReputationDto.class);
            reputationDto = responseEntity.getBody();
        } catch (HttpClientErrorException ex) {
            String errorMessage = "Failed to get reputation for gameId=" + gameId;
            log.error(errorMessage, ex);
            throw new FailedRunInvestigationException(errorMessage, ex);
        }
        return reputationDto;
    }

    public List<MessageDto> getAllMessages(String gameId) {
        List<MessageDto> allMessageDtos;
        try {
            UriComponents url = UriComponentsBuilder.fromPath(GET_ALL_MESSAGES_ENDPOINT).buildAndExpand(gameId);
            var responseEntity = mugloarRestTemplate.exchange(url.toString(), HttpMethod.GET, null, new ParameterizedTypeReference<List<MessageDto>>() {
            });
            allMessageDtos = responseEntity.getBody();
        } catch (HttpClientErrorException ex) {
            String errorMessage = "Failed to get all messages for gameId=" + gameId;
            log.error(errorMessage, ex);
            throw new FailedGetAllMessagesException(errorMessage, ex);
        }
        return allMessageDtos;
    }

    public TaskResultDto solveTask(String gameId, String taskId) {
        TaskResultDto taskResultDto;
        try {
            UriComponents url = UriComponentsBuilder.fromPath(SOLVE_TASK_ENDPOINT).buildAndExpand(gameId, taskId);
            var responseEntity = mugloarRestTemplate.exchange(url.toString(), HttpMethod.POST, null, TaskResultDto.class);
            taskResultDto = responseEntity.getBody();
        } catch (HttpClientErrorException ex) {
            String errorMessage = "Failed to solve taskId=" + taskId + " for gameId= " + gameId;
            log.error(errorMessage, ex);
            throw new FailedSolveTaskException(errorMessage, ex);
        }
        return taskResultDto;
    }

    public List<ItemDto> getAllShopItems(String gameId) {
        List<ItemDto> itemDtos;
        try {
            UriComponents url = UriComponentsBuilder.fromPath(GET_ALL_SHOP_ITEMS_ENDPOINT).buildAndExpand(gameId);
            var responseEntity = mugloarRestTemplate.exchange(url.toString(), HttpMethod.GET, null, new ParameterizedTypeReference<List<ItemDto>>() {
            });
            itemDtos = responseEntity.getBody();
        } catch (HttpClientErrorException ex) {
            String errorMessage = "Failed to get all shop items for gameId=" + gameId;
            log.error(errorMessage, ex);
            throw new FailedGetAllShopItemsException(errorMessage, ex);
        }
        return itemDtos;
    }

    public PurchaseResultDto purchaseItem(String gameId, String itemId) {
        PurchaseResultDto purchaseResultDto;
        try {
            UriComponents url = UriComponentsBuilder.fromPath(PURCHASE_AN_ITEM_ENDPOINT).buildAndExpand(gameId, itemId);
            var responseEntity = mugloarRestTemplate.exchange(url.toString(), HttpMethod.POST, null, PurchaseResultDto.class);
            purchaseResultDto = responseEntity.getBody();
        } catch (HttpClientErrorException ex) {
            String errorMessage = "Failed to purchase an itemId=" + itemId + " for gameId=" + gameId;
            log.error(errorMessage, ex);
            throw new FailedPurchaseItemException(errorMessage, ex);
        }
        return purchaseResultDto;
    }
}
