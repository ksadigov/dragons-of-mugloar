package com.bigbank.mugloar.service.impl;

import com.bigbank.mugloar.client.MugloarApiClient;
import com.bigbank.mugloar.dto.ReputationDto;
import com.bigbank.mugloar.service.InvestigationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InvestigationServiceImpl implements InvestigationService {
    private final MugloarApiClient mugloarApiClient;

    @Override
    public ReputationDto investigate(String gameId) {
        return mugloarApiClient.runInvestigation(gameId);
    }
}
