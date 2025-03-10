package main.manager;


import java.util.List;

import main.tasks.*;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;


class InMemoryHistoryManagerTest {
    @Test
    void testSavingDataInHistory(){
        TaskManager manager = Managers.getDefaultTaskManager();

        Task task = new Task("test1", "descrption1", StatusTask.NEW);

        manager.createTask(task);
        manager.searchTaskById(task.getId());

        task.setStatus(StatusTask.IN_PROGRESS);
        manager.searchTaskById(task.getId());

        List<Task> history = manager.getHistory();
        assertEquals("test1", history.get(0).getName());
        assertEquals("descrption1", history.get(0).getDescription());
        assertEquals(StatusTask.NEW, history.get(0).getStatus());


        assertEquals(StatusTask.IN_PROGRESS, history.get(1).getStatus());
    }
}