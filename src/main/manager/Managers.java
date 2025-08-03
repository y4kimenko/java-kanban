package main.manager;

import java.io.File;

public final class Managers {
    private Managers() {
    }

    public static TaskManager getDefaultTaskManager() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistoryManager() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTaskManager getDefaultFileBackedTaskManager() {
        return FileBackedTaskManager.loadFromFile(new File("\\resources\\package.csv"));
    }
}
