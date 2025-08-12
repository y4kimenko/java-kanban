package main.managers;


import main.exceptions.ManagerSaveException;
import main.tasks.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static main.tasks.TypeTask.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    public File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public void save() {
        try {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8,
                    false))) {
                writer.write("id,type,name,status,description,epic,startTime,endTime,duration\n");
                List<Task> allTasks = new ArrayList<>(getAllTasks());
                allTasks.addAll(getAllEpics());
                allTasks.addAll(getAllSubtasks());

                for (Task task : allTasks) {
                    writer.write(toString(task) + '\n');
                }
            } catch (IOException exp) {
                throw new ManagerSaveException("Ошибка сохранения в файл");
            }
        } catch (ManagerSaveException exp) {
            System.out.println(exp.getMessage());
        }
    }

    public <T extends Task> String toString(T task) {
        if (task.getType().equals(SUBTASK)) {
            Subtask subtask = (Subtask) task;
            if (subtask.getStartTime() != null) {
                return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%d", subtask.getId(), subtask.getType(), subtask.getName(),
                        subtask.getStatus(), subtask.getDescription(), subtask.getEpicId(),
                        subtask.getStartTime().format(formatter), subtask.getEndTime().format(formatter),
                        subtask.getDuration().toMinutes());
            } else {
                return String.format("%s,%s,%s,%s,%s,%s", subtask.getId(), subtask.getType(), subtask.getName(),
                        subtask.getStatus(), subtask.getDescription(), subtask.getEpicId());
            }
        } else {
            if (task.getStartTime() != null) {
                return String.format("%s,%s,%s,%s,%s,Нет,%s,%s,%d", task.getId(), task.getType(), task.getName(),
                        task.getStatus(), task.getDescription(), task.getStartTime().format(formatter),
                        task.getEndTime().format(formatter), task.getDuration().toMinutes());
            } else {
                return String.format("%s,%s,%s,%s,%s", task.getId(), task.getType(), task.getName(),
                        task.getStatus(), task.getDescription());
            }
        }
    }

    public Task fromString(String string) {
        String[] array = string.split(",");
        Task task;
        switch (TypeTask.valueOf(array[1])) {
            case TASK:
                if (array.length == 5) {
                    task = new Task(array[2], array[4], StatusTask.valueOf(array[3]), Integer.parseInt(array[0]));
                } else {
                    task = new Task(array[2], array[4], StatusTask.valueOf(array[3]), Integer.parseInt(array[0]),
                            Duration.ofMinutes(Long.parseLong(array[8])), LocalDateTime.parse(array[6], formatter));
                }
                break;
            case EPIC:
                task = new Epic(array[2], array[4], Integer.parseInt(array[0]));
                break;
            default:
                if (array.length == 6) {
                    task = new Subtask(array[2], array[4], StatusTask.valueOf(array[3]), Integer.parseInt(array[5]),
                            Integer.parseInt(array[0]));
                } else {
                    task = new Subtask(array[2], array[4], StatusTask.valueOf(array[3]), Integer.parseInt(array[5]),
                            Integer.parseInt(array[0]), Duration.ofMinutes(Long.parseLong(array[8])),
                            LocalDateTime.parse(array[6], formatter));
                }
        }
        return task;
    }

    public FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try {
            try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
                reader.readLine();
                while (reader.ready()) {
                    Task task = fromString(reader.readLine());
                    if (task.getType().equals(TASK)) {
                        Task copyTask = new Task(task.getName(), task.getDescription(), task.getStatus(),
                                task.getDuration(), task.getStartTime());
                        copyTask.setId(task.getId());
                        manager.tasks.put(copyTask.getId(), copyTask);
                    } else if (task.getType().equals(EPIC)) {
                        Epic epic = (Epic) task;
                        Epic copyEpic = new Epic(epic.getName(), epic.getDescription());
                        copyEpic.setId(epic.getId());
                        manager.epics.put(copyEpic.getId(), copyEpic);
                        calculateEpicStatus(copyEpic);
                        calculateEpicTime(copyEpic);
                    } else if (task.getType().equals(SUBTASK)) {
                        Subtask subtask = (Subtask) task;
                        Subtask copySubtask = new Subtask(subtask.getName(), subtask.getDescription(),
                                subtask.getStatus(), subtask.getEpicId(), subtask.getDuration(), subtask.getStartTime());
                        copySubtask.setId(subtask.getId());
                        manager.subtasks.put(copySubtask.getId(), copySubtask);
                        Epic epicToAddId = manager.epics.get(copySubtask.getEpicId());
                        epicToAddId.getSubtasksIds().add(copySubtask.getId());
                        calculateEpicStatus(epicToAddId);
                        calculateEpicTime(epicToAddId);
                    }
                }
            } catch (IOException exp) {
                throw new ManagerSaveException("Ошибка чтения файла");
            }
        } catch (ManagerSaveException exp) {
            System.out.println(exp.getMessage());
        }
        return manager;
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

}