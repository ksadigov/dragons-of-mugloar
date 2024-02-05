package com.bigbank.mugloar.service;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.bigbank.mugloar.config.GameProps;
import com.bigbank.mugloar.service.impl.StatisticsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

    private StatisticsServiceImpl statisticsService;

    private ListAppender<ILoggingEvent> listAppender;

    @BeforeEach
    void setUp() {
        when(gameSettings.getFinalScoreThreshold()).thenReturn(1000);
        when(gameSettings.getRetryLimit()).thenReturn(10);

        statisticsService = new StatisticsServiceImpl(gameSettings);

        Logger statisticsLogger = (Logger) LoggerFactory.getLogger(StatisticsServiceImpl.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        statisticsLogger.addAppender(listAppender);
    }

    @Test
    void printGameStats_ShouldLogCorrectStatistics() {
        List<Integer> gameScores = List.of(950, 1200, 1800, 2000, 1001, 1070, 1111, 1320, 1500, 1023);

        when(statisticsService.getGameScores()).thenReturn(gameScores);

        statisticsService.printGameStats();

        assertThat(listAppender.list)
                .extracting(ILoggingEvent::getFormattedMessage)
                .contains("Game Count: 10",
                        "Games won: 9",
                        "Games lost: 1",
                        "Min Score: 950",
                        "Max Score: 2000",
                        "Success Rate: 90.00%");
    }
}
