package main.manager;

import main.tasks.Task;


import java.util.List;

public interface HistoryManager {
    void add(Task task);

    List<Task> getHistory();

}
