package com.example.bank_account_app.service;


import com.example.bank_account_app.config.CurrencyExchangeConf;
import com.example.bank_account_app.dto.CurrencyExchangeResponse;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExternalAPIService {
    private final CurrencyExchangeConf config;
    private final WebClient.Builder webClientBuilder;

    /**
     * Fetch currency exchange rates from external API.
     */
    public Map<String, Double> getCurrencyExchangeRates(String fromCurrency) {
        if (fromCurrency == null || fromCurrency.isBlank()) {
            throw new IllegalArgumentException("Invalid base currency provided");
        }

        String url = config.getCurrencyExchangeEndpoint() + fromCurrency.toUpperCase();
        log.debug("Fetching currency exchange rates from external API: {}", url);

        WebClient client = webClientBuilder.build();

        try {
            Mono<CurrencyExchangeResponse> response = client.get()
                    .uri(url)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, resp -> {
                        log.error("Failed to fetch exchange rates. Status: {}", resp.statusCode());
                        return resp.createException().flatMap(Mono::error);
                    })
                    .bodyToMono(CurrencyExchangeResponse.class);

            return response
                    .retry(2)
                    .blockOptional(config.getTimeout())
                    .map(CurrencyExchangeResponse::getRates)
                    .orElse(Collections.emptyMap());

        } catch (Exception ex) {
            log.error("Error fetching exchange rates: {}", ex.getMessage());
            return Collections.emptyMap();
        }
    }
}

