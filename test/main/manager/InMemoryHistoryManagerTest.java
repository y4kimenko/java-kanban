package main.manager;


import main.tasks.StatusTask;
import main.tasks.Task;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


class InMemoryHistoryManagerTest {
    @Test
    void testSavingDataInHistory() {
        TaskManager manager = Managers.getDefaultTaskManager();

        Task task = new Task("test1", "descrption1", StatusTask.NEW);
        Task task2 = new Task("test2", "descrption2", StatusTask.NEW);

        manager.createTask(task);
        manager.createTask(task2);
        manager.searchTaskById(task.getId());
        manager.searchTaskById(task2.getId());

        assertEquals(List.of(task, task2), manager.getHistory(), "В истории должен остаться только последний просмотр каждой задачи");

    }

    @Test
    void testRemovingDataInHistory() {
        TaskManager manager = Managers.getDefaultTaskManager();

        Task task = new Task("test1", "descrption1", StatusTask.NEW);
        Task task2 = new Task("test2", "descrption2", StatusTask.NEW);

        manager.createTask(task);
        manager.createTask(task2);
        manager.searchTaskById(task.getId());
        manager.searchTaskById(task2.getId());

        manager.removeTaskById(task.getId());

        assertEquals(List.of(task2), manager.getHistory(), "removeTaskById(id) должен убирать задачу из истории");

    }
}