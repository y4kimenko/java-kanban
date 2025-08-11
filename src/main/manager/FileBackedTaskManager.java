package main.manager;

import main.exception.ManagerSaveException;
import main.tasks.*;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    private FileBackedTaskManager(File file) {
        super();
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        if (!file.exists()) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write("id,type,name,status,description,epicId,startTime,durationMinutes\n");
            } catch (IOException ignored) {
            }
            return manager;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank() || line.startsWith("id,")) continue;
                manager.fromStringSafe(line);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения файла: " + e.getMessage());
        }
        return manager;
    }

    private void save() {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("id,type,name,status,description,epicId,startTime,durationMinutes\n");
            for (Task task : tasks.values()) {
                writer.write(toString(task));
                writer.write('\n');
            }
            for (Epic epic : epics.values()) {
                writer.write(toString(epic));
                writer.write('\n');
            }
            for (Subtask subtask : subtasks.values()) {
                writer.write(toString(subtask));
                writer.write('\n');
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка при сохранении");
        }
    }

    private void fromStringSafe(String line) {
        try {
            fromString(line);
        } catch (RuntimeException ex) {
            throw new ManagerSaveException("Ошибка парсинга строки: \"" + line + "\". " + ex.getMessage());
        }
    }

    private Task fromString(String value) {
        // CSV: id,type,name,status,description,epicId,startTime,durationMinutes
        String[] split = value.split(",", -1);
        int id = Integer.parseInt(split[0]);
        TypeTask type = TypeTask.valueOf(split[1]);
        String name = unescape(split[2]);
        String statusStr = split[3];
        String description = unescape(split[4]);
        String epicIdStr = split[5];
        String startStr = split.length > 6 ? split[6] : "";
        String durStr = split.length > 7 ? split[7] : "";

        LocalDateTime start = startStr.isEmpty() ? null : LocalDateTime.parse(startStr);
        Duration duration = durStr.isEmpty() ? null : Duration.ofMinutes(Long.parseLong(durStr));

        Task result;
        switch (type) {
            case TASK -> {
                Task t = new Task(name, description, StatusTask.valueOf(statusStr), start, duration);
                result = super.createTaskWithID(t, id);
            }
            case EPIC -> {
                Epic e = new Epic(name, description);
                e.setStatus(StatusTask.valueOf(statusStr)); // при загрузке допустимо
                result = super.createEpicWithID(e, id);
            }
            case SUBTASK -> {
                int epicId = epicIdStr.isEmpty() ? -1 : Integer.parseInt(epicIdStr);
                Subtask s = new Subtask(name, description, StatusTask.valueOf(statusStr), start, duration, epicId);
                result = super.createSubtaskWithID(s, id);
            }
            default -> throw new IllegalStateException("Unknown type: " + type);
        }

        if (id >= generatedId) generatedId = id + 1;
        return result;
    }

    private String toString(Task task) {
        String epicId = "";
        if (task instanceof Subtask s) {
            epicId = String.valueOf(s.getEpicId());
        }
        String start = task.getStartTime() == null ? "" : task.getStartTime().toString();
        String dur = task.getDuration() == null ? "" : String.valueOf(task.getDuration().toMinutes());

        return String.join(",",
                String.valueOf(task.getId()),
                task.getTypeTask().name(),
                escape(task.getName()),
                task.getStatus().name(),
                escape(task.getDescription()),
                epicId,
                start,
                dur
        );
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace(",", "\\,");
    }

    private String unescape(String s) {
        if (s == null) return "";
        return s.replace("\\,", ",");
    }

    // ===== персист-оверрайды =====
    @Override
    public Task createTask(Task task) {
        Task t = super.createTask(task);
        save();
        return t;
    }

    @Override
    public Task createTaskWithID(Task task, int id) {
        Task t = super.createTaskWithID(task, id);
        save();
        return t;
    }

    @Override
    public Task updateTask(Task updatedTask) {
        Task t = super.updateTask(updatedTask);
        save();
        return t;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic e = super.createEpic(epic);
        save();
        return e;
    }

    @Override
    public Epic createEpicWithID(Epic epic, int id) {
        Epic e = super.createEpicWithID(epic, id);
        save();
        return e;
    }

    @Override
    public Epic updateEpic(Epic updatedEpic) {
        Epic e = super.updateEpic(updatedEpic);
        save();
        return e;
    }

    @Override
    public boolean removeTaskById(int taskId) {
        boolean ok = super.removeTaskById(taskId);
        save();
        return ok;
    }

    @Override
    public boolean removeSubtaskById(int subtaskId) {
        boolean ok = super.removeSubtaskById(subtaskId);
        save();
        return ok;
    }

    @Override
    public boolean removeEpicById(int epicId) {
        boolean ok = super.removeEpicById(epicId);
        save();
        return ok;
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        Subtask s = super.createSubtask(subtask);
        save();
        return s;
    }

    @Override
    public Subtask createSubtaskWithID(Subtask subtask, int id) {
        Subtask s = super.createSubtaskWithID(subtask, id);
        save();
        return s;
    }

    @Override
    public Subtask updateSubtask(Subtask updatedSubtask) {
        Subtask s = super.updateSubtask(updatedSubtask);
        save();
        return s;
    }
}
