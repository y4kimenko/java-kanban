package main.manager;


import main.tasks.*;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryTaskManagerTest {
    private TaskManager manager;
    @BeforeEach
    void setUp() {
        manager = Managers.getDefaultTaskManager();
    }

    @Test
    void testSavingAndSearchTaskInMemory() {
        Task task = new Task("test1", "descrption1", StatusTask.NEW);

        manager.createTask(task);

        assertEquals(task, manager.searchTaskById(1));
    }

    @Test
    void testSavingAndSearchEpicInMemory() {
        Epic epic = new Epic("test1", "descrption1");

        manager.createEpic(epic);

        assertEquals(epic, manager.searchEpicById(1));
    }

    @Test
    void testSavingAndSearchSubtaskInMemory() {
        Epic epic = new Epic("test1", "descrption1");
        manager.createEpic(epic);

        Subtask subtask = new Subtask("test1", "descrption1", StatusTask.NEW, epic.getId());
        manager.createSubtask(subtask);

        assertEquals(subtask, manager.searchSubtaskById(2));
    }

    @Test
    void testCheckingImmutabilityWhenSavingTaskInMemory() {
        Task task = new Task("test1", "descrption1", StatusTask.NEW);

        manager.createTask(task);

        assertEquals("test1", manager.searchTaskById(1).getName());
        assertEquals("descrption1", manager.searchTaskById(1).getDescription());
        assertEquals(StatusTask.NEW, manager.searchTaskById(1).getStatus());
    }

    @Test
    void testCheckingImmutabilityWhenSavingEpicInMemory() {
        Epic epic = new Epic("test1", "descrption1");

        manager.createEpic(epic);

        assertEquals("test1", manager.searchEpicById(1).getName());
        assertEquals("descrption1", manager.searchEpicById(1).getDescription());

    }

    @Test
    void testCheckingImmutabilityWhenSavingSubtaskInMemory() {
        Epic epic = new Epic("test1", "descrption1");
        manager.createEpic(epic);

        Subtask subtask = new Subtask("test1", "descrption1", StatusTask.NEW, epic.getId());
        manager.createSubtask(subtask);

        assertEquals("test1", manager.searchSubtaskById(2).getName());
        assertEquals("descrption1", manager.searchSubtaskById(2).getDescription());
        assertEquals(StatusTask.NEW, manager.searchSubtaskById(2).getStatus());
        assertEquals(subtask.getId(), manager.searchSubtaskById(2).getId());

    }

    @Test
    void testCheckingCreatingTaskWithId() {
        Task task = new Task("test1", "descrption1", StatusTask.NEW);
        manager.createTaskWithID(task, 5);

        assertEquals(task, manager.searchTaskById(5));
    }

    @Test
    void testCheckingCreatingEpicWithId() {
        Epic epic = new Epic("test1", "descrption1");
        manager.createEpicWithID(epic, 6);

        assertEquals(epic, manager.searchEpicById(6));
    }

    @Test
    void testCheckingCreatingSubtaskWithId() {
        Epic epic = new Epic("test1", "descrption1");
        manager.createEpic(epic);

        Subtask subtask = new Subtask("test1", "descrption1", StatusTask.NEW, epic.getId());
        manager.createSubtaskWithID(subtask, 3);

        assertEquals(subtask, manager.searchSubtaskById(3));
    }

}