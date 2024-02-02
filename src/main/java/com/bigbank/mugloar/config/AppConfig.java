package com.bigbank.mugloar.config;

import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.util.Timeout;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate mugloarRestTemplate(MugloarClientProps mugloarClientProps, RestTemplateProps restTemplateProps) {
        var restTemplate = setupRestTemplate(restTemplateProps, mugloarClientProps);
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(mugloarClientProps.getHost()));
        return restTemplate;
    }

    private RestTemplate setupRestTemplate(RestTemplateProps restTemplateProps, MugloarClientProps mugloarClientProps) {
        final var connectionManager = getConnectionManager(restTemplateProps);
        final var requestFactory = setupConnectionFactory(mugloarClientProps, connectionManager);

        final var restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(requestFactory);

        return restTemplate;
    }

    private PoolingHttpClientConnectionManager getConnectionManager(RestTemplateProps restTemplateProps) {
        final var connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(restTemplateProps.getMaxPoolSize().intValue());
        connectionManager.setDefaultMaxPerRoute(restTemplateProps.getMaxConnectionsPerRoute().intValue());
        return connectionManager;
    }

    private ClientHttpRequestFactory setupConnectionFactory(MugloarClientProps mugloarClientProps, PoolingHttpClientConnectionManager connectionManager) {
        connectionManager.setDefaultSocketConfig(SocketConfig.custom().setSoTimeout(Timeout.ofMilliseconds(mugloarClientProps.getReadTimeout())).build());
        final var requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectTimeout(mugloarClientProps.getConnectionTimeout());
        requestFactory.setHttpClient(HttpClientBuilder.create().setConnectionManager(connectionManager).build());
        return requestFactory;
    }
}
