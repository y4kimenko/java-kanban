package main.managers;

import org.junit.jupiter.api.BeforeEach;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    void createManager() {
        tm = new InMemoryTaskManager();
    }


}