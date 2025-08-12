package main.manager;

import java.io.File;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistoryManager() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTaskManager getFileBackedManager(File file) {
        return FileBackedTaskManager.loadFromFile(file);
    }
}
