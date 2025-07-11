package main.tasks;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subTasks = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description, StatusTask.NEW);
    }

    // копирующий конструктор
    public Epic(Epic epic) {
        super(epic);
        subTasks.addAll(epic.subTasks);
    }

    @Override
    public String toString() {
        return "Epic{" + "subTasks=" + subTasks + ", id=" + getId() + ", description='" + getDescription() + '\'' + ", status=" + getStatus() + ", name='" + getName() + '\'' + '}';
    }

    public List<Integer> getSubTasks() {
        return new ArrayList<>(subTasks);
    }

    // ----------------------- методы связанные SubTask ------------------------------------
    public Boolean addSubTask(int id) {
        if (!subTasks.contains(id)) {
            subTasks.add(id);
            return true;
        }
        return false;
    }

    public void removeSubTask(int id) {
        subTasks.remove(Integer.valueOf(id));
    }

    public void cleanSubTasks() {
        subTasks.clear();
    }

}





