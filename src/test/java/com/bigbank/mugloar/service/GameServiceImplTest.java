package com.bigbank.mugloar.service;

import com.bigbank.mugloar.client.MugloarApiClient;
import com.bigbank.mugloar.dto.GameStateDto;
import com.bigbank.mugloar.service.impl.GameServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.bigbank.mugloar.util.Constants.GAME_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameServiceImplTest {

    @Mock
    private MugloarApiClient mugloarApiClient;

    @InjectMocks
    private GameServiceImpl testObj;

    @Test
    void playGame_ShouldStartNewGameAndReturnGameState() {
        GameStateDto expectedGameState = new GameStateDto(GAME_ID, 3, 0, 0, 0, 0, 0);
        when(mugloarApiClient.startNewGame()).thenReturn(expectedGameState);

        GameStateDto actualGameState = testObj.playGame();

        assertThat(actualGameState).isNotNull().isEqualTo(expectedGameState);
        verify(mugloarApiClient).startNewGame();
    }

}
