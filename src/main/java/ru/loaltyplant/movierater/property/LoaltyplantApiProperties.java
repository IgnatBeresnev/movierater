package ru.loaltyplant.movierater.property;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile(ApplicationProperties.PROFILE_LOALTYPLANT_API)
public class LoaltyplantApiProperties {

    @Getter
    @Value("${loaltyplant.api.fjp.workers}")
    private int fjpWorkers;

    @Value("${loaltyplant.api.key}")
    private String apiKey;

    @Value("${loaltyplant.api.url.get.genres}")
    private String genresGetUrl;

    @Value("${loaltyplant.api.url.get.movies}")
    private String moviesGetUrl;

    @Getter
    @Value("${loaltyplant.api.maxRps}")
    private int maxRps;

    public boolean isApiKeySet() {
        return !"SET_YOUR_OWN".equalsIgnoreCase(apiKey);
    }

    public String getMoviesUrlWithApiKeyAndPage(int page) {
        return moviesGetUrl + "?api_key=" + apiKey + "&page=" + page;
    }

    public String getGenresUrlWithApiKey() {
        return genresGetUrl + "?api_key=" + apiKey;
    }
}
