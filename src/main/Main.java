package main;

import main.manager.Managers;
import main.manager.TaskManager;
import main.tasks.Epic;
import main.tasks.StatusTask;
import main.tasks.Subtask;
import main.tasks.Task;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        File file = new File("tasks.csv");
        TaskManager manager = Managers.getFileBackedManager(file);

        // Примеры задач
        Task t1 = new Task("Почта", "Разобрать входящие", StatusTask.NEW,
                LocalDateTime.parse("2025-08-11T10:00"), Duration.ofMinutes(60));
        manager.createTask(t1);

        Task t2 = new Task("Созвон", "Созвон с командой", StatusTask.NEW,
                LocalDateTime.parse("2025-08-11T12:00"), Duration.ofMinutes(30));
        manager.createTask(t2);

        Epic e1 = new Epic("Релиз 1.2", "Подготовка к релизу");
        manager.createEpic(e1);

        Subtask s1 = new Subtask("Чек-лист", "Собрать чек-лист", StatusTask.NEW,
                LocalDateTime.parse("2025-08-11T13:00"), Duration.ofMinutes(45), e1.getId());
        manager.createSubtask(s1);

        // Демонстрация пересечения
        try {
            Task bad = new Task("Конфликт", "Пересекается с t1", StatusTask.NEW,
                    LocalDateTime.parse("2025-08-11T10:30"), Duration.ofMinutes(15));
            manager.createTask(bad);
        } catch (IllegalStateException ex) {
            System.out.println("Ожидаемая ошибка: " + ex.getMessage());
        }

        System.out.println("Приоритезированный список:");
        manager.getPrioritizedTasks().forEach(System.out::println);

        System.out.println("История (пусто): " + manager.getHistory().size());
        manager.searchTaskById(t1.getId());
        manager.searchEpicById(e1.getId());
        manager.searchSubtaskById(s1.getId());
        System.out.println("История (после просмотров): " + manager.getHistory().size());
    }
}
