package ru.loaltyplant.movierater.shell.input;

import org.jline.reader.LineReader;
import org.springframework.shell.Input;
import org.springframework.shell.jline.InteractiveShellApplicationRunner;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ObservableInputProvider extends InteractiveShellApplicationRunner.JLineInputProvider {
    private final List<Observer> observers = new ArrayList<>();

    public ObservableInputProvider(LineReader lineReader, PromptProvider promptProvider) {
        super(lineReader, promptProvider);
    }

    public void subscribe(Observer observer) {
        observers.add(observer);
    }

    private void notifyObservers(String newInput) {
        observers.forEach(observer -> observer.handleNewInput(newInput));
    }

    @Override
    public Input readInput() {
        Input input = super.readInput();
        String newInput = input.rawText();
        notifyObservers(newInput);
        return () -> newInput;
    }

    public interface Observer {
        void handleNewInput(String input);
    }
}
