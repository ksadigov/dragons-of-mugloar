package com.bigbank.mugloar.service;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.bigbank.mugloar.config.GameProps;
import com.bigbank.mugloar.service.impl.StatisticsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceImplTest {

    @Mock
    private GameProps gameSettings;

    @InjectMocks
    private StatisticsServiceImpl testObj;

    private ListAppender<ILoggingEvent> listAppender;

    @BeforeEach
    void setUp() {
        Logger statisticsLogger = (Logger) LoggerFactory.getLogger(StatisticsServiceImpl.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        statisticsLogger.addAppender(listAppender);
    }

    @Test
    void printGameStats_ShouldLogCorrectStatistics() {
        when(gameSettings.getFinalScoreThreshold()).thenReturn(1000);
        when(gameSettings.getRetryLimit()).thenReturn(10);

        testObj.getGameScores().clear();
        testObj.addGameScore(950);
        testObj.addGameScore(1200);
        testObj.addGameScore(1800);
        testObj.addGameScore(2000);
        testObj.addGameScore(1001);
        testObj.addGameScore(1070);
        testObj.addGameScore(1111);
        testObj.addGameScore(1320);
        testObj.addGameScore(1500);
        testObj.addGameScore(1023);

        testObj.printGameStats();

        assertThat(listAppender.list)
                .extracting(ILoggingEvent::getFormattedMessage)
                .contains("Game Count: 10",
                        "Games won: 9",
                        "Games lost: 1",
                        "Min Score: 950",
                        "Max Score: 2000",
                        "Success Rate: 90.00%");
    }

    @Test
    void addGameScore_ShouldAddScoreToList() {
        int score = 75;

        testObj.addGameScore(score);

        List<Integer> gameScores = testObj.getGameScores();
        assertThat(gameScores).hasSize(1).contains(score);
    }
}
