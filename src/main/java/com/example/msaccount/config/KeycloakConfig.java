package com.example.msaccount.config;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.ClientRequestFilter;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakConfig {

    @Value("${keycloak.server-url}")
    private String serverUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    @Value("${cloudflare.access.client-id}")
    private String cfClientId;

    @Value("${cloudflare.access.client-secret}")
    private String cfClientSecret;

    @Bean
    public Keycloak keycloak() {
        Client resteasyClient = ClientBuilder.newClient();
        resteasyClient.register((ClientRequestFilter) requestContext -> {
            requestContext.getHeaders().add("CF-Access-Client-Id", cfClientId);
            requestContext.getHeaders().add("CF-Access-Client-Secret", cfClientSecret);
        });

        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .resteasyClient(resteasyClient)
                .build();
    }
}