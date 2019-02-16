package ru.loaltyplant.movierater.configuration.api;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import ru.loaltyplant.movierater.property.ApplicationProperties;

@Configuration
@Profile(ApplicationProperties.PROFILE_LOALTYPLANT_API)
public class LoaltyplantApiConfiguration {

}
