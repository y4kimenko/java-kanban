package main.manager;


import main.tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InMemoryHistoryManager implements HistoryManager {
    private static final List<Task> history = new ArrayList<>();

    @Override
    public List<Task> getHistory(){
        return new ArrayList<Task>(history);
    }

    @Override
    public void add(Task task) {
        if (!Objects.isNull(task)) {
            if (history.size() == 10) {
                history.removeFirst();
            }
            history.add(new Task(task));
        }
    }
}
