package main.manager;

import main.exception.ManagerSaveException;
import main.tasks.*;

import java.io.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    private FileBackedTaskManager(File file) {
        super();
        this.file = file;
    }

    private void save() {
        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(file))) {
            fileWriter.write("id,type,name,status,description,epic\n");
            fileWriter.newLine();

            for (Task task: super.tasks.values()) {
                fileWriter.write(toString(task));
                fileWriter.newLine();
            }
            for (Epic epic: super.epics.values()) {
                fileWriter.write(toString(epic));
                fileWriter.newLine();
            }
            for (Subtask subtask: super.subtasks.values()) {
                fileWriter.write(toString(subtask));
                fileWriter.newLine();
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка при сохранении");
        }

    }

    protected static FileBackedTaskManager loadFromFile (File file) {
        FileBackedTaskManager taskManager = new FileBackedTaskManager(file);

        try (FileReader reader = new FileReader(file); BufferedReader br = new BufferedReader(reader)) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank() || line.startsWith("id,")) continue; // пропускаем шапку/пустые
                taskManager.fromString(line);
            }
        } catch (IOException ex) {
            ex.getMessage();
        }
        return taskManager;
    }

    private Task fromString(String value) {
        Task task;
        String[] split = value.split(",");

        task = switch (TypeTask.valueOf(split[1])) {
            case TypeTask.TASK -> super.createTaskWithID(new Task(split[2], split[4], StatusTask.valueOf(split[3])), Integer.parseInt(split[0]));
            case TypeTask.EPIC -> super.createEpicWithID(new Epic(split[2], split[4]), Integer.parseInt(split[0]));
            case TypeTask.SUBTASK -> super.createSubtaskWithID(new Subtask(split[2], split[4], StatusTask.valueOf(split[3]), Integer.parseInt(split[5])), Integer.parseInt(split[0]));
        };

        if (Integer.parseInt(split[0]) > super.generatedId){
            generatedId = Integer.parseInt(split[0]);
        }
        return task;
    }

    private String toString(Task task) {
        StringBuilder builder = new StringBuilder();

        builder.append(task.getId()).append(",");

        switch (task) {
            case Epic epic -> builder.append(TypeTask.EPIC.name()).append(",");
            case Subtask subtask -> builder.append(TypeTask.SUBTASK.name()).append(",");
            case Task task1 -> builder.append(TypeTask.TASK.name()).append(",");
        }
        builder.append(task.getName()).append(",");
        builder.append(task.getStatus()).append(",");
        builder.append(task.getDescription()).append(",");

        if (task instanceof Subtask){
            builder.append(((Subtask) task).getEpicId());
        }

        return builder.toString();
    }

    // ======================= Удаление по идентификатору =======================
    @Override
    public boolean removeTaskById(int taskId){
        boolean result = super.removeTaskById(taskId);
        save();
        return result;
    }

    @Override
    public boolean removeSubtaskById(int subtaskId){
        boolean result = super.removeSubtaskById(subtaskId);
        save();
        return result;
    }

    @Override
    public boolean removeEpicById(int epicId){
        boolean result = super.removeEpicById(epicId);
        save();
        return result;
    }

    // ======================= Удаление всех задач =======================
    @Override
    public void removeAllTasks(){
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllEpics(){
        super.removeAllEpics();
        save();
    }

    @Override
    public void removeAllSubtasks(){
        super.removeAllSubtasks();
        save();
    }

    // ======================= Методы создания и обновления =======================
    @Override
    public Task createTask(Task task){
        Task returnTask = super.createTask(task);
        save();
        return returnTask;
    }

    @Override
    public Task createTaskWithID(Task task, int id) {
        Task returnTask = super.createTaskWithID(task, id);
        save();
        return returnTask;
    }

    @Override
    public Task updateTask(Task updatedTask) {
        Task returnTask = super.updateTask(updatedTask);
        save();
        return returnTask;
    }


    @Override
    public Epic createEpic(Epic epic) {
        Epic returnEpic = super.createEpic(epic);
        save();
        return returnEpic;
    }

    @Override
    public Epic createEpicWithID(Epic epic, int id) {
        Epic returnEpic = super.createEpicWithID(epic, id);
        save();
        return returnEpic;
    }

    @Override
    public Epic updateEpic(Epic updatedEpic) {
        Epic returnEpic = super.updateEpic(updatedEpic);
        save();
        return returnEpic;
    }


    @Override
    public Subtask createSubtask(Subtask subtask) {
        Subtask returnSubtask = super.createSubtask(subtask);
        save();
        return returnSubtask;
    }

    @Override
    public Subtask createSubtaskWithID(Subtask subtask, int id) {
        Subtask returnSubtask = super.createSubtaskWithID(subtask, id);
        save();
        return returnSubtask;
    }

    @Override
    public Subtask updateSubtask(Subtask updatedSubtask) {
        Subtask returnSubtask = super.updateSubtask(updatedSubtask);
        save();
        return returnSubtask;
    }


}
