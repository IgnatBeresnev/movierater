package ru.loaltyplant.movierater.configuration.shell;

import org.jline.reader.LineReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.shell.InputProvider;
import org.springframework.shell.SpringShellAutoConfiguration;
import org.springframework.shell.jcommander.JCommanderParameterResolverAutoConfiguration;
import org.springframework.shell.jline.InteractiveShellApplicationRunner;
import org.springframework.shell.jline.JLineShellAutoConfiguration;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.shell.standard.StandardAPIAutoConfiguration;
import org.springframework.shell.standard.commands.StandardCommandsAutoConfiguration;

/**
 * Configures running spring shell without spring boot
 * 1. Spring shell 1.2 cannot be used because it does not expose
 * configuration to external spring contexts, so I found it impossible
 * to use the the same, single context for both spring shell and our
 * application.
 * 2. In current state, I don't need SpringBoot's autoconfiguration
 * capabilities, they seem to be excessive, I just need spring-context
 * (DI, IoC) and basic Spring Shell
 * <p>
 * https://stackoverflow.com/a/49496448/6395606
 */
@Configuration
@Import({
        SpringShellAutoConfiguration.class,
        JLineShellAutoConfiguration.class,
        JCommanderParameterResolverAutoConfiguration.class,
        StandardAPIAutoConfiguration.class,
        StandardCommandsAutoConfiguration.class,
})
public class SpringShellConfiguration {

    @Bean
    @Autowired
    public InputProvider inputProvider(LineReader lineReader, PromptProvider promptProvider) {
        return new InteractiveShellApplicationRunner.JLineInputProvider(lineReader, promptProvider);
    }
}
