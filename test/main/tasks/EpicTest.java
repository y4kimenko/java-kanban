package main.tasks;

import main.manager.InMemoryTaskManager;
import main.manager.TaskManager;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    @Test
    void statusCalculated_fromSubtasks_allNew() {
        TaskManager manager = new InMemoryTaskManager();

        Epic epic = manager.createEpic(new Epic("Release", ""));
        Subtask s1 = new Subtask("S1", "", StatusTask.NEW,
                LocalDateTime.parse("2025-08-11T10:00"), Duration.ofMinutes(10), epic.getId());
        Subtask s2 = new Subtask("S2", "", StatusTask.NEW,
                LocalDateTime.parse("2025-08-11T12:00"), Duration.ofMinutes(20), epic.getId());

        manager.createSubtask(s1);
        manager.createSubtask(s2);

        Epic actual = manager.searchEpicById(epic.getId());

        assertEquals(LocalDateTime.parse("2025-08-11T10:00"), actual.getStartTime());
        assertEquals(LocalDateTime.parse("2025-08-11T12:20"), actual.getEndTime());
        assertEquals(Duration.ofMinutes(30), actual.getDuration());
    }

    @Test
    void timesCleared_whenNoSubtasks() {
        TaskManager manager = new InMemoryTaskManager();

        Epic epic = manager.createEpic(new Epic("Empty", ""));
        Epic actual = manager.searchEpicById(epic.getId());

        // у эпика без подзадач временные поля должны быть null
        assertNull(actual.getStartTime());
        assertNull(actual.getEndTime());
        assertNull(actual.getDuration());
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
