package ru.loaltyplant.movierater.util.math;

import net.jcip.annotations.NotThreadSafe;

@NotThreadSafe
public class RunningAverage {
    private double total;
    private double sum;

    public void add(double value) {
        sum += value;
        total++;
    }

    public double getAverage() {
        double average = sum / total;
        if (Double.isNaN(average)) {
            return 0.0d;
        }
        return average;
    }
}
