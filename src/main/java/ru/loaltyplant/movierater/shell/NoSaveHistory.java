package ru.loaltyplant.movierater.shell;

import org.jline.reader.impl.history.DefaultHistory;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Spring shell by default creates a spring.shell.log file,
 * that's the only way to make it NOT do that.
 * <p>
 * https://github.com/spring-projects/spring-shell/issues/194
 */
@Component
public class NoSaveHistory extends DefaultHistory {
    @Override
    public void save() throws IOException {
    }
}
