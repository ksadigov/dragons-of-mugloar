package com.bigbank.mugloar.client;

import com.bigbank.mugloar.dto.GameStateDto;
import com.bigbank.mugloar.dto.ReputationDto;
import com.bigbank.mugloar.exception.FailedRunInvestigationException;
import com.bigbank.mugloar.exception.FailedStartNewGameException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
                eq(org.springframework.http.HttpMethod.POST),
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
                eq(org.springframework.http.HttpMethod.POST),
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
                eq(org.springframework.http.HttpMethod.POST),
                any(),
                eq(ReputationDto.class))
        ).thenThrow(new RuntimeException("Connection error"));

        assertThatThrownBy(() -> testObj.runInvestigation(gameId))
                .isInstanceOf(FailedRunInvestigationException.class)
                .hasMessageContaining("Failed to get reputation for gameId=" + gameId);
    }
}

