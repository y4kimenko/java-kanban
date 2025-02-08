import tasks.*;

import java.util.Objects;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        Task task1 = taskManager.createTask(new Task("Task 1", "description1", StatusTask.NEW));
        Task task2 = taskManager.createTask(new Task("Task 2", "description2", StatusTask.IN_PROGRESS));

        Epic epic1 = taskManager.createEpic(new Epic("Epic 1", "Description 3"));
        Subtask subtask1_1 = taskManager.createSubtask(new Subtask("subtask 1","Description 4",StatusTask.NEW, epic1.getId()));
        Subtask subtask1_2 = taskManager.createSubtask(new Subtask("subtask 2","Description 5",StatusTask.NEW, epic1.getId()));

        Epic epic2 = taskManager.createEpic(new Epic("Epic 1", "Description 6"));
        Subtask subtask2_1 = taskManager.createSubtask(new Subtask("subtask 1","Description 7",StatusTask.NEW, epic2.getId()));
        Subtask subtask2_2 = taskManager.createSubtask(new Subtask("subtask 2","Description 8",StatusTask.IN_PROGRESS, epic2.getId()));


        System.out.println(task1);
        System.out.println(task2);
        System.out.println();

        System.out.println(epic1);
        System.out.println(subtask1_1);
        System.out.println(subtask1_2);

        System.out.println();
        System.out.println(epic2);
        System.out.println(subtask2_1);
        System.out.println(subtask2_2);

        System.out.println();
        System.out.println("Изменение Task:");
        Task updatedTask =new Task(task1);
        updatedTask.setStatus(StatusTask.IN_PROGRESS);
        taskManager.updateTask(updatedTask);
        System.out.println(task1);
        System.out.println();

        System.out.println("Изменение Epic:");

        Epic updatedEpic = new Epic(epic1);
        updatedEpic.setName("New name");
        taskManager.updateEpic(updatedEpic);
        System.out.println(epic1);
        System.out.println();

        System.out.println("Обновление сабтаска");
        Subtask updatedSubtask = new Subtask(subtask1_1);
        updatedSubtask.setName("New name2");
        taskManager.updateSubtask(updatedSubtask);
        System.out.println(subtask1_1);
        System.out.println(epic1);

        System.out.println("Удаление элементов по индексу");
        taskManager.printAllEpics();
        System.out.println("Удаление эпика 1");
        taskManager.removeEpicById(epic1.getId());
        taskManager.printAllEpics();

    }
}
