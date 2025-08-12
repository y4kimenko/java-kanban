package main.managers;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import main.tasks.Epic;
import main.tasks.StatusTask;
import main.tasks.Subtask;
import main.tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest <T extends TaskManager>{

    T tm;
    static Task task1;
    static Task task2;
    static Epic epic1;
    static Subtask subtask1;
    static Subtask subtask2;
    static Subtask subtask3;
    static Subtask subtask4;
    static Epic epic2;
    static Task task3;
    static Task task4;
    static Epic epic3;
    static Subtask subtask5;
    static Subtask subtask6;
    static Subtask subtask7;
    static Subtask subtask8;
    static Epic epic4;


    @BeforeEach
    abstract void createManager();

    @BeforeEach
    void createSomeTasks() {
        task1 = new Task("Переезд", "В новую квартиру", StatusTask.NEW);
        task2 = new Task("Переезд2", "В новый дом", StatusTask.NEW);
        epic1 = new Epic("Перевод денег", "Перевести деньги другу");
        subtask1 = new Subtask("Приложение банка", "Открыть приложение банка",
                StatusTask.IN_PROGRESS, 3);
        subtask2 = new Subtask("Открыть вкладку расходов", "Открытие вкладки расходов",
                StatusTask.NEW, 3);
        subtask3 = new Subtask("Отправка", "Отправка денег другу", StatusTask.DONE, 3);
        epic2 = new Epic("Пройти курс", "Пройти курс от ЯП");
        subtask4 = new Subtask("Тренировка", "Провести тренировку в зале", StatusTask.DONE, 4);
        task3 = new Task("Переезд", "В новую квартиру", StatusTask.NEW, Duration.ofMinutes(240),
                LocalDateTime.of(2025, Month.JULY, 11, 14, 40));
        task4 = new Task("Переезд", "В новый дом", StatusTask.NEW, Duration.ofMinutes(20),
                LocalDateTime.of(2025, Month.JULY, 11, 15, 50));
        epic3 = new Epic("Перевод денег", "Перевести деньги другу");
        subtask5 = new Subtask("Приложение банка", "Открыть приложение банка",
                StatusTask.IN_PROGRESS, 3, Duration.ofMinutes(90),
                LocalDateTime.of(2025, Month.JULY, 11, 19, 37));
        subtask6 = new Subtask("Открыть вкладку расходов", "Открытие вкладки расходов",
                StatusTask.NEW, 3, Duration.ofMinutes(15),
                LocalDateTime.of(2025, Month.JULY, 11, 15 , 25));
        subtask7 = new Subtask("Отправка", "Отправка денег другу", StatusTask.DONE, 3,
                Duration.ofMinutes(40), LocalDateTime.of(2025, Month.JULY, 11, 18, 10));
        epic4 = new Epic("Пройти курс", "Пройти курс от ЯП");
        subtask8 = new Subtask("Тренировка", "Провести тренировку в зале", StatusTask.DONE, 7,
                Duration.ofMinutes(40), LocalDateTime.of(2025, Month.JULY, 11, 18, 5));
    }

    @Test
    public void getAllTasksCheck() {
        tm.addTask(task1);
        tm.addTask(task2);
        tm.addEpic(epic1);
        tm.addEpic(epic2);
        tm.addSubtask(subtask1);
        tm.addSubtask(subtask2);
        tm.addSubtask(subtask3);
        tm.addSubtask(subtask4);
        assertEquals(2, tm.getAllTasks().size());
    }

    @Test
    public void getAllSubtasksAndEpicsCheckWithTime() {
        tm.addTask(task1);
        tm.addTask(task2);
        tm.addEpic(epic1);
        tm.addEpic(epic2);
        tm.addSubtask(subtask1);
        tm.addSubtask(subtask2);
        tm.addSubtask(subtask3);
        tm.addSubtask(subtask4);
        assertEquals(2, tm.getAllEpics().size());
        assertEquals(4, tm.getAllSubtasks().size());
    }

    @Test
    public void deleteAllForAllTypesCheck() {
        tm.addTask(task1);
        tm.addTask(task2);
        tm.addEpic(epic1);
        tm.addEpic(epic2);
        tm.addSubtask(subtask1);
        tm.addSubtask(subtask2);
        tm.addSubtask(subtask3);
        tm.addSubtask(subtask4);
        tm.deleteAllSubtasks();
        tm.deleteAllTasks();
        tm.deleteAllEpics();
        assertEquals(0, tm.getAllSubtasks().size());
        assertEquals(0, tm.getAllTasks().size());
        assertEquals(0, tm.getAllEpics().size());
    }

    @Test
    public void getSthById() {
        tm.addTask(task1);
        tm.addTask(task2);
        tm.addEpic(epic1);
        tm.addEpic(epic2);
        tm.addSubtask(subtask1);
        tm.addSubtask(subtask2);
        tm.addSubtask(subtask3);
        tm.addSubtask(subtask4);
        assertEquals(tm.getTaskById(1).getName(), task1.getName());
        assertEquals(tm.getEpicById(3).getName(), epic1.getName());
        assertEquals(tm.getSubtaskById(5).getName(), subtask1.getName());
    }
}