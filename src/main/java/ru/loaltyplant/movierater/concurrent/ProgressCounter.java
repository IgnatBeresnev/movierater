package ru.loaltyplant.movierater.concurrent;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.jcip.annotations.ThreadSafe;
import ru.loaltyplant.movierater.util.math.MathUtils;

@ThreadSafe
@RequiredArgsConstructor
public class ProgressCounter {
    private final int totalEntires;

    /**
     * Values within the range of 0.0 - 100.0,
     * cannot go under or over than that
     */
    @Getter
    private volatile double progress;

    public void updateProcessed(int processedAmount) {
        // validated and set here since getProgress() is called way more often that update
        double calculatedProgressValue = this.progress = MathUtils.getPercentage(processedAmount, totalEntires);
        if (calculatedProgressValue < 0d) {
            this.progress = 0d;
        } else if (calculatedProgressValue > 100d) {
            this.progress = 100d;
        } else {
            this.progress = calculatedProgressValue;
        }
    }
}
