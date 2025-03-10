package main.manager;

import main.tasks.Task;


import java.util.Deque;
import java.util.Queue;

public interface HistoryManager {
    void add(Task task);

    Deque<Task> getHistory();

}
