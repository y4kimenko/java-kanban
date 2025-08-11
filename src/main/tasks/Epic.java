package main.tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
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

    // Пересчёт временных полей по сабтаскам (вызов из менеджера)
    public void recalcFromSubtasks(List<Subtask> subtasksOfEpic) {
        if (subtasksOfEpic == null || subtasksOfEpic.isEmpty()) {
            setDuration(null);
            setStartTime(null);
            endTime = null;
            return;
        }

        Duration total = subtasksOfEpic.stream()
                .map(Subtask::getDuration)
                .filter(d -> d != null)
                .reduce(Duration.ZERO, Duration::plus);

        setDuration(total);

        LocalDateTime start = subtasksOfEpic.stream()
                .map(Subtask::getStartTime)
                .filter(t -> t != null)
                .min(Comparator.naturalOrder())
                .orElse(null);
        setStartTime(start);

        endTime = subtasksOfEpic.stream()
                .map(Subtask::getEndTime)
                .filter(t -> t != null)
                .max(Comparator.naturalOrder())
                .orElse(null);
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public TypeTask getTypeTask() {
        return TypeTask.EPIC;
    }
}
