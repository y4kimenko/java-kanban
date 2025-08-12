package main.manager;

import main.tasks.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected T manager;

    protected abstract T createManager();

    @BeforeEach
    void setUp() {
        manager = createManager();
    }

    // ---- Epic status (a–d) ----
    @Test
    void epicStatus_allNew() {
        Epic e = manager.createEpic(new Epic("E",""));
        manager.createSubtask(new Subtask("S1","", StatusTask.NEW,
                LocalDateTime.parse("2025-08-11T10:00"), Duration.ofMinutes(10), e.getId()));
        manager.createSubtask(new Subtask("S2","", StatusTask.NEW,
                LocalDateTime.parse("2025-08-11T11:00"), Duration.ofMinutes(10), e.getId()));
        assertEquals(StatusTask.NEW, manager.searchEpicById(e.getId()).getStatus());
    }

    @Test
    void epicStatus_allDone() {
        Epic e = manager.createEpic(new Epic("E",""));
        manager.createSubtask(new Subtask("S1","", StatusTask.DONE,
                LocalDateTime.parse("2025-08-11T10:00"), Duration.ofMinutes(10), e.getId()));
        manager.createSubtask(new Subtask("S2","", StatusTask.DONE,
                LocalDateTime.parse("2025-08-11T11:00"), Duration.ofMinutes(10), e.getId()));
        assertEquals(StatusTask.DONE, manager.searchEpicById(e.getId()).getStatus());
    }

    @Test
    void epicStatus_newAndDone_givesInProgress() {
        Epic e = manager.createEpic(new Epic("E",""));
        manager.createSubtask(new Subtask("S1","", StatusTask.DONE,
                LocalDateTime.parse("2025-08-11T10:00"), Duration.ofMinutes(10), e.getId()));
        manager.createSubtask(new Subtask("S2","", StatusTask.NEW,
                LocalDateTime.parse("2025-08-11T11:00"), Duration.ofMinutes(10), e.getId()));
        assertEquals(StatusTask.IN_PROGRESS, manager.searchEpicById(e.getId()).getStatus());
    }

    @Test
    void epicStatus_inProgress() {
        Epic e = manager.createEpic(new Epic("E",""));
        manager.createSubtask(new Subtask("S1","", StatusTask.IN_PROGRESS,
                LocalDateTime.parse("2025-08-11T10:00"), Duration.ofMinutes(10), e.getId()));
        manager.createSubtask(new Subtask("S2","", StatusTask.IN_PROGRESS,
                LocalDateTime.parse("2025-08-11T11:00"), Duration.ofMinutes(10), e.getId()));
        assertEquals(StatusTask.IN_PROGRESS, manager.searchEpicById(e.getId()).getStatus());
    }

    // ---- Prioritized + null-start exclusion ----
    @Test
    void prioritized_excludesNullStart_andSorts() {
        manager.createTask(new Task("NoTime","", StatusTask.NEW)); // без времени
        Task t2 = manager.createTask(new Task("B","", StatusTask.NEW,
                LocalDateTime.parse("2025-08-11T12:00"), Duration.ofMinutes(30)));
        Task t1 = manager.createTask(new Task("A","", StatusTask.NEW,
                LocalDateTime.parse("2025-08-11T08:00"), Duration.ofMinutes(15)));

        List<Task> prio = manager.getPrioritizedTasks();
        assertEquals(2, prio.size());
        assertEquals(t1.getId(), prio.get(0).getId());
        assertEquals(t2.getId(), prio.get(1).getId());
    }

    // ---- Overlap checks ----
    @Test
    void overlap_onCreate_throws() {
        manager.createTask(new Task("A","", StatusTask.NEW,
                LocalDateTime.parse("2025-08-11T10:00"), Duration.ofMinutes(60)));
        Task overlapping = new Task("B","", StatusTask.NEW,
                LocalDateTime.parse("2025-08-11T10:30"), Duration.ofMinutes(15));
        assertThrows(IllegalStateException.class, () -> manager.createTask(overlapping));
    }

    @Test
    void overlap_onUpdate_throws() {
        manager.createTask(new Task("A","", StatusTask.NEW,
                LocalDateTime.parse("2025-08-11T10:00"), Duration.ofMinutes(60)));
        Task b = manager.createTask(new Task("B","", StatusTask.NEW,
                LocalDateTime.parse("2025-08-11T12:00"), Duration.ofMinutes(15)));

        b.setStartTime(LocalDateTime.parse("2025-08-11T10:30"));
        b.setDuration(Duration.ofMinutes(30));
        assertThrows(IllegalStateException.class, () -> manager.updateTask(b));
    }
}
