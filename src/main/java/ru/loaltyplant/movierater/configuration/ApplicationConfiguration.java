package ru.loaltyplant.movierater.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import ru.loaltyplant.movierater.Application;

@Configuration
@PropertySource("file:${applicationProperties}")
@ComponentScan(basePackageClasses = Application.class)
public class ApplicationConfiguration {

}
