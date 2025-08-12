package main.manager;

import main.tasks.StatusTask;
import main.tasks.Task;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryHistoryManagerTest {

    @Test
    void emptyHistory() {
        HistoryManager history = Managers.getDefaultHistoryManager();
        assertTrue(history.getHistory().isEmpty());
    }


    @Test
    void deduplicate() {
        HistoryManager history = Managers.getDefaultHistoryManager();
        Task t1 = new Task("A", "", StatusTask.NEW);
        t1.setId(1);
        Task t2 = new Task("B", "", StatusTask.NEW);
        t2.setId(2);

        history.add(t1);
        history.add(t2);
        history.add(t1); // повторный просмотр — t1 должен переехать в хвост

        var list = history.getHistory();
        assertEquals(2, list.size(), "Повторная задача не должна дублироваться в истории");
        assertEquals(2, list.get(0).getId(), "Первая должна быть t2");
        assertEquals(1, list.get(1).getId(), "Последняя должна быть t1 (переместилась в хвост)");
    }


    @Test
    void removeHeadMiddleTail() {
        HistoryManager history = Managers.getDefaultHistoryManager();
        Task t1 = new Task("A", "", StatusTask.NEW);
        t1.setId(1);
        Task t2 = new Task("B", "", StatusTask.NEW);
        t2.setId(2);
        Task t3 = new Task("C", "", StatusTask.NEW);
        t3.setId(3);

        history.add(t1);
        history.add(t2);
        history.add(t3);

        history.remove(1); // голова
        assertEquals(List.of(t2, t3), history.getHistory());

        history.remove(2); // середина
        assertEquals(List.of(t3), history.getHistory());

        history.remove(3); // хвост
        assertTrue(history.getHistory().isEmpty());
    }
}
