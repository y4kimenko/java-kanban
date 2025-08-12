package main.tasks;

import org.junit.jupiter.api.BeforeEach;
import main.managers.Managers;
import main.managers.TaskManager;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    private static Epic epic1;
    private static Subtask subtask1;
    private static Subtask subtask2;
    private static Subtask subtask3;
    private static Subtask subtask4;
    private static Subtask subtask5;
    private static Subtask subtask6;

    private static TaskManager taskManager;


    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();
        epic1 = new Epic("Перевод денег", "Перевести деньги другу");
        subtask1 = new Subtask("Приложение банка", "Открыть приложение банка", StatusTask.DONE, 1);
        subtask2 = new Subtask("Открыть вкладку расходов", "Открытие вкладки расходов",
                StatusTask.NEW, 1);
        subtask3 = new Subtask("Отправка", "Отправка денег другу", StatusTask.DONE, 1);
        subtask4 = new Subtask("Открыть вкладку расходов", "Открытие вкладки расходов",
                StatusTask.IN_PROGRESS, 1, Duration.ofMinutes(90),
                LocalDateTime.of(2025, Month.JULY, 12, 19, 37));
        subtask5 = new Subtask("Приложение банка", "Открыть приложение банка", StatusTask.IN_PROGRESS,
                1, Duration.ofMinutes(90), LocalDateTime.of(2025, Month.JULY, 11, 19,
                37));
        subtask6 = new Subtask("Приложение банка", "Открыть приложение банка", StatusTask.NEW, 1);
    }

    @Test
    void epicIsNewWhenAllSubtaskStatusesNew() {
        taskManager.addEpic(epic1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask6);
        assertEquals(StatusTask.NEW, taskManager.getEpicById(1).getStatus());
    }

    @Test
    void epicIsInProgressWhenSubtaskStatusesNewAndDone() {
        taskManager.addEpic(epic1);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        assertEquals(StatusTask.IN_PROGRESS, taskManager.getEpicById(1).getStatus());
    }

    @Test
    void epicIsInProgressWhenSubtaskStatusesNewAndInProgress() {
        taskManager.addEpic(epic1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask5);
        assertEquals(StatusTask.IN_PROGRESS, taskManager.getEpicById(1).getStatus());
    }

    @Test
    void epicIsInProgressWhenSubtaskStatusesDoneAndInProgress() {
        taskManager.addEpic(epic1);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask5);
        assertEquals(StatusTask.IN_PROGRESS, taskManager.getEpicById(1).getStatus());
    }

    @Test
    void epicIsInProgressWhenSubtaskStatusesAllInProgress() {
        taskManager.addEpic(epic1);
        taskManager.addSubtask(subtask4);
        taskManager.addSubtask(subtask5);
        assertEquals(StatusTask.IN_PROGRESS, taskManager.getEpicById(1).getStatus());
    }

    @Test
    void epicIsNewWhenAllSubtaskStatusesDone() {
        taskManager.addEpic(epic1);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask3);
        assertEquals(StatusTask.DONE, taskManager.getEpicById(1).getStatus());
    }

    @Test
    void checkEpicStartAndEndTime() {
        taskManager.addEpic(epic1);
        taskManager.addSubtask(subtask4);
        taskManager.addSubtask(subtask5);
        List<LocalDateTime> endTime = taskManager.getAllEpics()
                .stream()
                .map(Epic::getEndTime)
                .toList();
        List<LocalDateTime> startTime = taskManager.getAllEpics()
                .stream()
                .map(Epic::getStartTime)
                .toList();
        assertEquals(subtask5.getStartTime(), startTime.getFirst());
        assertEquals(subtask4.getEndTime(), endTime.getFirst());
    }
}