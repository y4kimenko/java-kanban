package main.manager;

import main.tasks.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    private TaskManager manager;

    @BeforeEach
    void setUp() {
        manager = Managers.getDefault();
    }

    // ------ Epic status cases (a–d) ------
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
    void epicStatus_newAndDone_isInProgress() {
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

    // ------ Prioritized list and null-start exclusion ------
    @Test
    void prioritized_excludesNullStart_andSortsByStart() {
        Task tNoTime = manager.createTask(new Task("A","", StatusTask.NEW));
        Task t2 = manager.createTask(new Task("B","", StatusTask.NEW,
                LocalDateTime.parse("2025-08-11T12:00"), Duration.ofMinutes(30)));
        Task t1 = manager.createTask(new Task("C","", StatusTask.NEW,
                LocalDateTime.parse("2025-08-11T08:00"), Duration.ofMinutes(15)));

        // если метод не объявлен в интерфейсе, берём из реализации
        List<Task> prio = ((InMemoryTaskManager) manager).getPrioritizedTasks();
        assertEquals(2, prio.size(), "Tasks without startTime must be excluded");
        assertEquals(t1.getId(), prio.get(0).getId());
        assertEquals(t2.getId(), prio.get(1).getId());
        assertNotEquals(tNoTime.getId(), prio.get(0).getId());
    }

    // ------ Overlap detection ------
    @Test
    void overlap_onCreate_task_throws() {
        manager.createTask(new Task("A","", StatusTask.NEW,
                LocalDateTime.parse("2025-08-11T10:00"), Duration.ofMinutes(60)));
        Task overlapping = new Task("B","", StatusTask.NEW,
                LocalDateTime.parse("2025-08-11T10:30"), Duration.ofMinutes(15));
        assertThrows(IllegalStateException.class, () -> manager.createTask(overlapping));
    }

    @Test
    void overlap_onUpdate_task_throws() {
        Task a = manager.createTask(new Task("A","", StatusTask.NEW,
                LocalDateTime.parse("2025-08-11T10:00"), Duration.ofMinutes(60)));
        Task b = manager.createTask(new Task("B","", StatusTask.NEW,
                LocalDateTime.parse("2025-08-11T12:00"), Duration.ofMinutes(15)));

        b.setStartTime(LocalDateTime.parse("2025-08-11T10:30"));
        b.setDuration(Duration.ofMinutes(30));
        assertThrows(IllegalStateException.class, () -> manager.updateTask(b));
    }

    @Test
    void epicTimes_recalculatedFromSubtasks() {
        Epic e = manager.createEpic(new Epic("E",""));
        manager.createSubtask(new Subtask("S1","", StatusTask.NEW,
                LocalDateTime.parse("2025-08-11T08:00"), Duration.ofMinutes(30), e.getId()));
        manager.createSubtask(new Subtask("S2","", StatusTask.NEW,
                LocalDateTime.parse("2025-08-11T09:00"), Duration.ofMinutes(45), e.getId()));

        Epic re = manager.searchEpicById(e.getId());
        assertEquals(LocalDateTime.parse("2025-08-11T08:00"), re.getStartTime());
        assertEquals(LocalDateTime.parse("2025-08-11T09:45"), re.getEndTime());
        assertEquals(Duration.ofMinutes(75), re.getDuration());
    }
}
