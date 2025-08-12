package main.managers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ManagersTest {
    @Test
    void testTaskManagerIsAlwaysInitialized() {
        TaskManager manager = Managers.getDefault();
        assertEquals(InMemoryTaskManager.class, manager.getClass());
    }

    @Test
    void testHistoryManagerIsAlwaysInitialized() {
        HistoryManager manager = Managers.getDefaultHistoryManager();
        assertEquals(InMemoryHistoryManager.class, manager.getClass());
    }
}
