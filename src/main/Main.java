package main;

import main.manager.FileBackedTaskManager;
import main.manager.Managers;
import main.tasks.Epic;
import main.tasks.StatusTask;
import main.tasks.Subtask;
import main.tasks.Task;

public class Main {
    public static void main(String[] args) {
        FileBackedTaskManager fileBackedTaskManager = Managers.getDefaultFileBackedTaskManager();

        Task task1 = fileBackedTaskManager.createTask(new Task("Task 1", "description1", StatusTask.NEW));
        Task task2 = fileBackedTaskManager.createTask(new Task("Task 2", "description2", StatusTask.IN_PROGRESS));

        Epic epic1 = fileBackedTaskManager.createEpic(new Epic("Epic 1", "Description 3"));
        Subtask subtask11 = fileBackedTaskManager.createSubtask(new Subtask("subtask 1", "Description 4", StatusTask.NEW, epic1.getId()));
        Subtask subtask12 = fileBackedTaskManager.createSubtask(new Subtask("subtask 2", "Description 5", StatusTask.NEW, epic1.getId()));

        Epic epic2 = fileBackedTaskManager.createEpic(new Epic("Epic 1", "Description 6"));
        Subtask subtask21 = fileBackedTaskManager.createSubtask(new Subtask("subtask 1", "Description 7", StatusTask.NEW, epic2.getId()));
        Subtask subtask22 = fileBackedTaskManager.createSubtask(new Subtask("subtask 2", "Description 8", StatusTask.IN_PROGRESS, epic2.getId()));


        FileBackedTaskManager newFileBackedManager = Managers.getDefaultFileBackedTaskManager();

        System.out.println("1 Tasks:");
        fileBackedTaskManager.printAllTasks();

        System.out.println("2 Tasks:");
        newFileBackedManager.printAllTasks();

        System.out.println();

        System.out.println("1 Epics:");
        fileBackedTaskManager.printAllEpics();

        System.out.println("2 Epics:");
        newFileBackedManager.printAllEpics();

        System.out.println();

        System.out.println("1 Subtasks:");
        fileBackedTaskManager.printAllSubtasks();

        System.out.println("2 Subtasks:");
        newFileBackedManager.printAllSubtasks();

    }
}
