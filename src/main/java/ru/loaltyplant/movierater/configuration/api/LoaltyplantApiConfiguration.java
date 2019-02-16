package ru.loaltyplant.movierater.configuration.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.RateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import ru.loaltyplant.movierater.property.ApplicationProperties;
import ru.loaltyplant.movierater.property.LoaltyplantApiProperties;

import java.net.http.HttpClient;

@Configuration
@Profile(ApplicationProperties.PROFILE_LOALTYPLANT_API)
public class LoaltyplantApiConfiguration {

    /**
     * Controls max requests per second so as not to DDOS api
     */
    @Bean
    @SuppressWarnings("UnstableApiUsage")
    public RateLimiter apiRateLimiter(LoaltyplantApiProperties properties) {
        double maxRps = (double) properties.getMaxRps();
        return RateLimiter.create(maxRps);
    }

    @Bean
    public HttpClient httpClient(LoaltyplantApiProperties properties) {
        if (!properties.isApiKeySet()) {
            throw new IllegalArgumentException("Set your own API key in application's properties");
        }
        return HttpClient.newHttpClient();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
