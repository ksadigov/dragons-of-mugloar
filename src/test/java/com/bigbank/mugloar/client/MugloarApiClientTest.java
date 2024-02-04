package com.bigbank.mugloar.client;

import com.bigbank.mugloar.dto.GameStateDto;
import com.bigbank.mugloar.exception.FailedStartNewGameException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

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
    private MugloarApiClient client;

    @Test
    void startNewGame_ShouldReturnGameStateDto() {
        GameStateDto expectedDto = new GameStateDto("gameId", 3, 0, 0, 0, 0, 0);
        when(restTemplate.exchange(
                eq(MugloarApiClient.START_NEW_GAME_ENDPOINT),
                eq(HttpMethod.POST),
                any(),
                eq(GameStateDto.class))
        ).thenReturn(ResponseEntity.ok(expectedDto));

        GameStateDto actualDto = client.startNewGame();

        assertThat(actualDto).isNotNull().isEqualTo(expectedDto);
    }

    @Test
    void startNewGame_WhenError_ShouldThrowException() {
        when(restTemplate.exchange(
                eq(MugloarApiClient.START_NEW_GAME_ENDPOINT),
                eq(org.springframework.http.HttpMethod.POST),
                any(),
                eq(GameStateDto.class))
        ).thenThrow(new RuntimeException("Connection error"));

        assertThatThrownBy(() -> client.startNewGame())
                .isInstanceOf(FailedStartNewGameException.class)
                .hasMessageContaining("Failed to start a new game. Something went wrong.");
    }
}

