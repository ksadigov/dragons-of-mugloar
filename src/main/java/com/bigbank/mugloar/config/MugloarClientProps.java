package com.bigbank.mugloar.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.mugloar-client-configuration")
public class MugloarClientProps {

    private String host;
    private Integer readTimeout;
    private Integer connectionTimeout;
}

