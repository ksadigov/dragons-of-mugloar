package com.bigbank.mugloar.service;

import com.bigbank.mugloar.client.MugloarApiClient;
import com.bigbank.mugloar.dto.ReputationDto;
import com.bigbank.mugloar.service.impl.InvestigationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvestigationServiceImplTest {

    @Mock
    private MugloarApiClient mugloarApiClient;

    @InjectMocks
    private InvestigationServiceImpl testObj;

    @Test
    void investigate_ShouldCallApiClientAndReturnReputation() {
        String gameId = "gameId";
        ReputationDto expectedReputation = new ReputationDto(0.0, 0.0, 0.0);
        when(mugloarApiClient.runInvestigation(gameId)).thenReturn(expectedReputation);

        ReputationDto actualReputation = testObj.investigate(gameId);

        verify(mugloarApiClient).runInvestigation(gameId);
        assertThat(actualReputation).isNotNull().isEqualTo(expectedReputation);
    }


}
