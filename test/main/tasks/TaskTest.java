package main.tasks;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void equalsById_onlyIdMatters() {
        Task a = new Task("A","", StatusTask.NEW);
        a.setId(42);
        Task b = new Task("B","different", StatusTask.DONE,
                LocalDateTime.parse("2025-08-11T10:00"), Duration.ofMinutes(15));
        b.setId(42);

        assertEquals(a, b, "Tasks with the same id must be equal");
        assertEquals(a.hashCode(), b.hashCode(), "hashCode must match when ids match");
    }

    @Test
    void endTime_isNullIfStartOrDurationMissing() {
        Task noStart = new Task("A","", StatusTask.NEW, null, Duration.ofMinutes(30));
        Task noDuration = new Task("B","", StatusTask.NEW, LocalDateTime.parse("2025-08-11T10:00"), null);
        assertNull(noStart.getEndTime());
        assertNull(noDuration.getEndTime());
    }

    @Test
    void endTime_computedAsStartPlusDuration() {
        LocalDateTime start = LocalDateTime.parse("2025-08-11T09:00");
        Duration dur = Duration.ofMinutes(45);
        Task t = new Task("A","", StatusTask.NEW, start, dur);
        assertEquals(start.plus(dur), t.getEndTime());
    }
}
