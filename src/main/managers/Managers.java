package main.managers;

import java.io.File;
import java.io.IOException;

public class Managers {
    public static TaskManager getDefault() {
        try {
            return new FileBackedTaskManager(File.createTempFile("file", ".csv"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static HistoryManager getDefaultHistoryManager() {
        return new InMemoryHistoryManager();
    }


}
