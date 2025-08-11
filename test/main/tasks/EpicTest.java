package main.tasks;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    @Test
    void statusCalculated_fromSubtasks_allNew() {
        Epic epic = new Epic("Release", "");
        // Здесь моделируем пересчёт временных полей внутри Epic:
        Subtask s1 = new Subtask("S1","", StatusTask.NEW,
                LocalDateTime.parse("2025-08-11T10:00"), Duration.ofMinutes(10), epic.getId());
        Subtask s2 = new Subtask("S2","", StatusTask.NEW,
                LocalDateTime.parse("2025-08-11T12:00"), Duration.ofMinutes(20), epic.getId());

        epic.recalcFromSubtasks(List.of(s1, s2));

        assertEquals(LocalDateTime.parse("2025-08-11T10:00"), epic.getStartTime());
        assertEquals(LocalDateTime.parse("2025-08-11T12:20"), epic.getEndTime());
        assertEquals(Duration.ofMinutes(30), epic.getDuration());
    }

    @Test
    void timesCleared_whenNoSubtasks() {
        Epic epic = new Epic("Empty","");

        epic.recalcFromSubtasks(List.of());

        assertNull(epic.getStartTime());
        assertNull(epic.getEndTime());
        assertNull(epic.getDuration());
    }

    @Test
    void equalsById_forEpics() {
        Epic e1 = new Epic("A", "");
        e1.setId(7);
        Epic e2 = new Epic("B", "");
        e2.setId(7);
        assertEquals(e1, e2);
    }
}
