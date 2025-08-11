package main.manager;

import main.tasks.Epic;
import main.tasks.Subtask;
import main.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    // ===== списки =====
    void printAllTasks();
    void printAllEpics();
    void printAllSubtasks();

    // ===== удаление по id =====
    boolean removeTaskById(int taskId);
    boolean removeSubtaskById(int subtaskId);
    boolean removeEpicById(int epicId);

    // ===== поиск по id =====
    Task searchTaskById(int id);
    Epic searchEpicById(int id);
    Subtask searchSubtaskById(int id);

    // ===== удаление всех =====
    void removeAllTasks();
    void removeAllEpics();
    void removeAllSubtasks();

    // ===== создание/обновление =====
    Task createTask(Task task);
    Task createTaskWithID(Task task, int id);
    Task updateTask(Task updatedTask);

    Epic createEpic(Epic epic);
    Epic createEpicWithID(Epic epic, int id);
    Epic updateEpic(Epic updatedEpic);

    Subtask createSubtask(Subtask subtask);
    Subtask createSubtaskWithID(Subtask subtask, int id);
    Subtask updateSubtask(Subtask updatedSubtask);

    // ===== связи =====
    ArrayList<Subtask> getSubtaskByIndexEpic(int epicId);

    // ===== история =====
    List<Task> getHistory();

    // ===== приоритизация =====
    List<Task> getPrioritizedTasks();
}
