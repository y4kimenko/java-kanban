package main.manager;

import main.tasks.*;

import java.util.ArrayList;

public interface TaskManager {
    // ==================== вывод списков ==============================
    void printAllTasks();

    void printAllEpics();

    void printAllSubtasks();

    // ======================= Удаление по индификатору =======================
    boolean removeTaskById(int taskId);

    boolean removeSubtaskById(int subtaskId);

    boolean removeEpicById(int epicId);

    // ======================= Получение по индификатору =======================
    Task searchTaskById(int id);

    Epic searchEpicById(int id);

    Subtask searchSubtaskById(int id);

    // ======================= Удаление всех задач =======================
    void removeAllTasks();

    void removeAllEpics();

    void removeAllSubtasks();

    // ======================= Методы создания и обновления =======================
    Task createTask(Task task);

    Task createTaskWithID(Task task, int id);

    Task updateTask(Task updatedTask);


    Epic createEpic(Epic epic);

    Epic createEpicWithID(Epic epic, int id);

    Epic updateEpic(Epic updatedEpic);


    Subtask createSubtask(Subtask subtask);

    Subtask createSubtaskWithID(Subtask subtask, int id);

    Subtask updateSubtask(Subtask updatedSubtask);


    ArrayList<Task> getHistory();

}
