package main.tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

import static main.managers.InMemoryTaskManager.formatter;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(String name, String description, StatusTask status, int epicId) {
        super(name, description);
        this.epicId = epicId;
        setStatus(status);
    }

    public Subtask(String name, String description, StatusTask status, int epicId, int id) {
        this(name, description, status, epicId);
        setId(id);
    }

    public Subtask(String name, String description, StatusTask status, int epicId, int id, Duration duration,
                   LocalDateTime startTime) {
        this(name, description, status, epicId, id);
        setDuration(duration);
        setStartTime(startTime);
    }

    public Subtask(String name, String description, StatusTask status, int epicId, Duration duration,
                   LocalDateTime startTime) {
        this(name, description, status, epicId);
        setDuration(duration);
        setStartTime(startTime);
    }

    @Override
    public String toString() {
        if (getDuration() != null && getStartTime() != null) {
            return '\n' +
                    "Название подзадачи: " + getName() + '\n' +
                    "Описание подзадачи: " + getDescription() + '\n' +
                    "ID подзадачи: " + getId() + '\n' +
                    "Статус подзадачи: " + getStatus() + '\n' +
                    "ID эпика: " + epicId + '\n' +
                    "Время старта выполнения подзадачи: " + getStartTime().format(formatter) + '\n' +
                    "Время окончания выполнения задачи: " + getEndTime().format(formatter) + '\n' +
                    "Продолжительность выполнения задачи: " + getDuration().toHours() + " ч. " + getDuration().toMinutesPart() +
                    " мин." + '\n' + '\n';
        } else {
            return '\n' +
                    "Название подзадачи: " + getName() + '\n' +
                    "Описание подзадачи: " + getDescription() + '\n' +
                    "ID подзадачи: " + getId() + '\n' +
                    "Статус подзадачи: " + getStatus() + '\n' +
                    "ID эпика: " + epicId + '\n' +
                    '\n';
        }
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public TypeTask getType() {
        return TypeTask.SUBTASK;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return epicId == subtask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }
}