package ru.loaltyplant.movierater;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.shell.InputProvider;
import org.springframework.shell.Shell;
import ru.loaltyplant.movierater.configuration.ApplicationConfiguration;

public class Application {
    public static void main(String[] args) throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfiguration.class);
        context.registerShutdownHook();

        Shell shell = context.getBean(Shell.class);

        // blocks until "exit" or "quit" is called
        shell.run(context.getBean(InputProvider.class));
        System.exit(0);
    }
}
