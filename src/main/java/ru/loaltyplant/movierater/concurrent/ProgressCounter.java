package ru.loaltyplant.movierater.concurrent;

import lombok.Data;
import net.jcip.annotations.ThreadSafe;
import ru.loaltyplant.movierater.util.math.MathUtils;

@Data
@ThreadSafe
public class ProgressCounter {
    private final int totalEntires;

    private volatile double progress;

    public void updateProcessed(int processedAmount) {
        this.progress = MathUtils.getPercentage(processedAmount, totalEntires);
    }
}
