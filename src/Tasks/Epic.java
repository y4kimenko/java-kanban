package tasks;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subTasks = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description, StatusTask.NEW);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subTasks=" + subTasks +
                ", id=" + getId() +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", name='" + getName() + '\'' +
                '}';
    }

    public List<Integer> getSubTasks() {
        return new ArrayList<>(subTasks);
    }

    // ----------------------- методы связанные SubTask ------------------------------------
    public void addSubTask(int id) {
        if (!subTasks.contains(id)) {
            subTasks.add(id);
        }
    }

    public void removeSubTask(int id) {
        subTasks.remove(id);
    }

    public void cleanSubTasks() {
        subTasks.clear();
    }

}





