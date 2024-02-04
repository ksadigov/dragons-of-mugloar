package com.bigbank.mugloar.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.rest-template")
public class RestTemplateProps {

    private Long timeout;
    private Long maxConnectionsPerRoute;
    private Long maxPoolSize;
    private Long keepalive;
}
