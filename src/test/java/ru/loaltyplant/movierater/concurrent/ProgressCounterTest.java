package ru.loaltyplant.movierater.concurrent;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProgressCounterTest {

    @Test
    public void shouldReturnZeroPercentForNonUsedCounter() {
        ProgressCounter progressCounter = new ProgressCounter(0);

        double expected = 0d;
        double actual = progressCounter.getProgress();
        assertEquals(expected, actual);
    }

    @Test
    public void shouldReturnOneThird() {
        ProgressCounter progressCounter = new ProgressCounter(75);
        progressCounter.updateProcessed(25);

        double expected = 33.33333333333333d;
        double actual = progressCounter.getProgress();
        assertEquals(expected, actual);
    }

    @Test
    public void shouldReturnHundredPercentForFullCounter() {
        ProgressCounter progressCounter = new ProgressCounter(100);
        progressCounter.updateProcessed(100);

        double expected = 100d;
        double actual = progressCounter.getProgress();
        assertEquals(expected, actual);
    }

    @Test
    public void shouldNotGoUnderZeroEvenIfIncorrectlyUsed() {
        ProgressCounter progressCounter = new ProgressCounter(100);
        progressCounter.updateProcessed(-50);

        double expected = 0d;
        double actual = progressCounter.getProgress();
        assertEquals(expected, actual);
    }

    @Test
    public void shouldNotGoOverHundredEvenIfIncorrectlyUsed() {
        ProgressCounter progressCounter = new ProgressCounter(1);
        progressCounter.updateProcessed(100);

        double expected = 100d;
        double actual = progressCounter.getProgress();
        assertEquals(expected, actual);
    }
}