package main.tasks;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subTasks = new ArrayList<>();

    // кэш вычисляемого конца
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description, StatusTask.NEW);
    }

    // копирующий конструктор
    public Epic(Epic epic) {
        super(epic);
        this.subTasks.addAll(epic.subTasks);
        this.endTime = epic.endTime;
    }

    public List<Integer> getSubTasks() {
        return subTasks;
    }

    public void addSubTask(int id) {
        subTasks.add(id);
    }

    public void removeSubTask(int id) {
        subTasks.remove(Integer.valueOf(id));
    }

    public void cleanSubTasks() {
        subTasks.clear();
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    // Нужен публичный сеттер, т.к. пересчёт времени теперь в менеджере
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public TypeTask getTypeTask() {
        return TypeTask.EPIC;
    }
}
