package com.bigbank.mugloar.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.game-settings")
public class GameProps {

    private Integer retryLimit;
    private Integer healingPotionCost;
    private Integer cheapItemCost;
    private Integer expensiveItemCost;
    private Integer reputationMaxLimit;
    private Integer finalScoreThreshold;
    private Integer baseLifeThreshold;
}

