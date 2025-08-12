package main.tasks;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SubtaskTest {

    @Test
    void equalsById_forSubtasks() {
        Subtask s1 = new Subtask("A", "", StatusTask.NEW, 100);
        s1.setId(5);
        Subtask s2 = new Subtask("B", "", StatusTask.DONE, 200);
        s2.setId(5);
        assertEquals(s1, s2);
    }

    @Test
    void endTimeComputed_forSubtask() {
        LocalDateTime st = LocalDateTime.parse("2025-08-11T08:15");
        Duration d = Duration.ofMinutes(30);
        Subtask s = new Subtask("S", "", StatusTask.NEW, st, d, 1);
        assertEquals(st.plus(d), s.getEndTime());
        assertEquals(1, s.getEpicId());
    }
}
