import tasks.*;

public class Main {
    public static void main(String[] args) {
        Manager manager = new Manager();

        Task task1 = manager.createTask(new Task("Task 1", "description1", StatusTask.NEW));
        Task task2 = manager.createTask(new Task("Task 2", "description2", StatusTask.IN_PROGRESS));

        Epic epic1 = manager.createEpic(new Epic("Epic 1", "Description 3"));
        Subtask subtask1_1 = manager.createSubtask(new Subtask("subtask 1","Description 4",StatusTask.NEW, epic1.getId()));
        Subtask subtask1_2 = manager.createSubtask(new Subtask("subtask 2","Description 5",StatusTask.NEW, epic1.getId()));

        Epic epic2 = manager.createEpic(new Epic("Epic 1", "Description 6"));
        Subtask subtask2_1 = manager.createSubtask(new Subtask("subtask 1","Description 7",StatusTask.NEW, epic2.getId()));
        Subtask subtask2_2 = manager.createSubtask(new Subtask("subtask 2","Description 8",StatusTask.IN_PROGRESS, epic2.getId()));


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
        manager.updateTask(task1);
        manager.updateEpic(epic1);
        System.out.println();

        System.out.println("Обновление сабтаска");
        manager.updateSubtask(subtask1_1);
        System.out.println(subtask1_1);
        System.out.println(epic1);

        System.out.println("Удаление элементов по индексу");
        manager.printAllEpics();
        System.out.println("Удаление эпика 1");
        manager.removeEpicById(epic1.getId());
        manager.printAllEpics();

    }
}
