package main.tasks;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

import static main.managers.InMemoryTaskManager.formatter;

public class Epic extends Task {
    ArrayList<Integer> subtasksIds = new ArrayList<>();
    protected LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(String name, String description, int id) {
        this(name, description);
        setId(id);
    }

    public ArrayList<Integer> getSubtasksIds() {
        return subtasksIds;
    }

    @Override
    public TypeTask getType() {
        return TypeTask.EPIC;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        if (endTime != null && getStartTime() != null) {
            return '\n' +
                    "Название эпика: " + getName() + '\n' +
                    "Описание эпика: " + getDescription() + '\n' +
                    "ID эпика: " + getId() + '\n' +
                    "Статус эпика: " + getStatus() + '\n' +
                    "ID подзадач: " + subtasksIds + '\n' +
                    "Время начала выполнения эпика: " + getStartTime().format(formatter) + '\n' +
                    "Время окончания выполнения эпика: " + endTime.format(formatter) + '\n' +
                    "Продолжительность выполнения задачи: " + getDuration().toHours() + " ч. " + getDuration().toMinutesPart() +
                    " мин." + '\n' + '\n';
        } else {
            return '\n' +
                    "Название эпика: " + getName() + '\n' +
                    "Описание эпика: " + getDescription() + '\n' +
                    "ID эпика: " + getId() + '\n' +
                    "Статус эпика: " + getStatus() + '\n' +
                    "ID подзадач: " + subtasksIds + '\n' +
                    '\n';
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtasksIds, epic.subtasksIds) && Objects.equals(endTime, epic.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtasksIds, endTime);
    }
}