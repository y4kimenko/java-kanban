package main.manager;


import main.tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final Deque<Task> history = new ArrayDeque<>();

    @Override
    public Deque<Task> getHistory(){
        return new ArrayDeque<>(history) ;
    }

    @Override
    public void add(Task task) {
        if (!Objects.isNull(task)) {
            if (history.size() == 10) {
                history.removeFirst();
            }
            history.addLast(new Task(task));
        }
    }
}
