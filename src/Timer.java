import java.time.Duration;
import java.time.Instant;

public class Timer {
    private Instant start = Instant.now();
    private Instant end = start;

    public long startTimer() {
        this.start = Instant.now();
        return start.toEpochMilli();
    }

    public long endTimer() {
        this.end = Instant.now();
        return end.toEpochMilli();
    }

    public long getSpeed() {
        return Duration.between(start, end).toMillis();
    }
}
