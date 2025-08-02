package main.manager;

import main.tasks.Epic;
import main.tasks.StatusTask;
import main.tasks.Subtask;
import main.tasks.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {

    @TempDir
    Path tempDir;       // Временная папка


    private Path csvPath; // Имя CSV-файла, с которым работает менеджер

    @BeforeEach
    void setUp() {
        csvPath = tempDir.resolve("tasks.csv");
    }

    void saveAndLoadEmptyFile_shouldKeepStateEmpty() throws Exception {
        /* создаём пустой файл-заглушку с единственной строкой-заголовком */
        Files.writeString(csvPath, "id,type,name,status,description,epic\n");

        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(csvPath.toFile());

        /* убеждаемся, что списки задач, эпиков и подзадач действительно пусты */
        assertAll(
                () -> assertTrue(manager.tasks.isEmpty(),    "tasks не пустой"),
                () -> assertTrue(manager.epics.isEmpty(),    "epics не пустой"),
                () -> assertTrue(manager.subtasks.isEmpty(), "subtasks не пустой")
        );

        /* три «removeAll…» вызывают save(); он не должен уронить тест */
        manager.removeAllTasks();
        manager.removeAllEpics();
        manager.removeAllSubtasks();

        /* проверяем, что файл всё ещё содержит только «шапку» и пустую строку */
        List<String> lines = Files.readAllLines(csvPath);
        assertEquals(2, lines.size(), "после сохранения появилось лишнее содержимое");
    }

    @Test
    void loadSeveralTasks_shouldRestoreAllEntities() throws Exception {
        /* вручную формируем CSV с тремя строками данных */
        Files.write(csvPath, List.of(
                "id,type,name,status,description,epic",
                "",
                "1,TASK,Task 1,NEW,Desc,",
                "2,EPIC,Epic 1,NEW,Desc,",
                "3,SUBTASK,Sub 1,DONE,Desc,2"
        ));

        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(csvPath.toFile());

        /* проверяю, что всё прочлось ровно в те коллекции, куда и должно */
        assertEquals(1, manager.tasks.size(),    "ожидалась 1 Task");
        assertEquals(1, manager.epics.size(),    "ожидался 1 Epic");
        assertEquals(1, manager.subtasks.size(), "ожидалась 1 Subtask");

        /* базовая валидация содержимого */
        assertEquals("Task 1", manager.searchTaskById(1).getName());
        assertEquals("Epic 1", manager.searchEpicById(2).getName());
        assertEquals(2,         manager.searchSubtaskById(3).getEpicId(), "epicId подзадачи некорректен");
    }

    @Test
    void saveSeveralTasks_shouldWriteAllEntitiesToFile_withoutLambdas() throws Exception {
        // 1. Создаём менеджер и сущности
        FileBackedTaskManager manager =
                FileBackedTaskManager.loadFromFile(csvPath.toFile());

        Task   task = manager.createTask (new Task ("Task 1", "Desc", StatusTask.NEW));
        Epic   epic = manager.createEpic (new Epic ("Epic 1", "Desc"));
        Subtask sub = manager.createSubtask(
                new Subtask("Sub 1", "Desc", StatusTask.DONE, epic.getId()));

        // 2. Читаем строки файла
        List<String> lines = Files.readAllLines(csvPath);


        // Проверка количества строк (шапка + пустая + 3 сущности = 5)

        assertEquals(5, lines.size(),
                "в файле должно быть ровно 3 сущности + шапка");

        // 3. Убеждаемся, что каждая сущность действительно записалась
        assertTrue(startsWithAny(lines, task.getId() + ","),
                "в файле нет строки с Task-id " + task.getId());
        assertTrue(startsWithAny(lines, epic.getId() + ","),
                "в файле нет строки с Epic-id " + epic.getId());
        assertTrue(startsWithAny(lines, sub.getId() + ","),
                "в файле нет строки с Subtask-id " + sub.getId());
    }

    private static boolean startsWithAny(List<String> lines, String prefix) {
        for (String line : lines) {
            if (line.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }
}
