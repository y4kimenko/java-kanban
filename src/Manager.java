import tasks.*;
import java.util.*;


public class Manager {
    private static final HashMap<Integer, Task> tasks = new HashMap<>();
    private static final HashMap<Integer, Epic> epics = new HashMap<>();
    private static final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    private int GeneratedId = 1;

// ==================== вывод списков ==============================
    public void printAllTasks(){
        System.out.println(tasks);
    }

    public void printAllEpics(){
        System.out.println(epics);
    }

    public void printAllSubtasks(){
        System.out.println(subtasks);
    }
// ======================= Удаление по индификатору =======================
    public boolean removeTaskById(int taskId) {
        return tasks.remove(taskId) != null;
    }

    public boolean removeSubtaskById(int subtaskId) {
        Subtask removed = subtasks.remove(subtaskId);

        if (removed != null) {
            // удаление из списка связанного epic

            // Я до конца не понял как сделать то, что вы предложили кроме того, как присвоить индификаторы public HashMap или добавить им геттеры, но как-то уже не очень
            // Замечание, которые вы оставляли: всё нормально сделано, но надо добавить в эпик соответствующие методы и будет еще красивее
            Epic epic = epics.get(subtasks.get(subtaskId).getEpicId());
            if (epic != null) {
                epic.getSubTasks().remove(subtaskId);
                recalculateEpicStatus(epic);
            }

            return true;
        } else {
            return false;
        }
    }

    public boolean removeEpicById(int epicId) {
        Epic removed = epics.remove(epicId);
        if (removed != null) {
            for (Integer subtaskId : removed.getSubTasks()) {
                subtasks.remove(subtaskId);
            }

            return true;
        } else {
            return false;
        }
    }


// ======================= Получение по индификатору =======================

    public Task searchTaskById(int id) {
        return tasks.get(id);
    }

    public Epic searchEpicById(int id) {
        return epics.get(id);
    }

    public Subtask searchSubtaskById(int id) {
        return subtasks.get(id);
    }

// ======================= Удаление всех задач =======================

    public void removeAllTasks() {
        tasks.clear();
    }

    public void removeAllEpics() {
        if (!epics.isEmpty()) {
            epics.clear();
            subtasks.clear();
        }

    }

    public void removeAllSubtasks() {
        if (!subtasks.isEmpty()) {
            subtasks.clear();
            for (Epic epic : epics.values()) {
                epic.getSubTasks().clear();
                epic.setStatus(StatusTask.NEW);
            }
        }

    }

// ======================= Методы создания и обновления =======================

    public Task createTask(Task task) {

        int newId = generateId();
        task.setId(newId);
        // Вы имеете в виду, чтобы я реализовал заполнение полей объекта через консоль (тот же самый вопрос и про изменение)
        tasks.put(newId, task);
        return task;
    }

    public Task updateTask(Task updatedTask) {

        if (updatedTask.getId() < 0){
            return updatedTask;
        }
// Как мне объяснил преподаватель: что это и есть вариант ничего не делать
//
        if (!tasks.containsKey(updatedTask.getId())) {
            return updatedTask;
        }

        Scanner scanner = new Scanner(System.in);

        while (true) {

            System.out.println("\t\tЧто вы бы хотели изменить в вашем Task?");
            System.out.println("1) name: " + updatedTask.getName() + ";" );
            System.out.println("2) status: " + updatedTask.getStatus() + ";");
            System.out.println("3) description: " + updatedTask.getDescription() + ";");
            System.out.println("\t\t0 - выход.");
            System.out.print("Ввод: ");

            switch (scanner.nextInt()) {
                case 1:
                    System.out.print("Новое имя: ");
                    scanner.nextLine();
                    updatedTask.setName(scanner.nextLine());
                    break;
                case 2:
                    System.out.println("Выберите какой статус вы бы хотели присвоить");
                    System.out.println("1) " + StatusTask.NEW);
                    System.out.println("2) " + StatusTask.IN_PROGRESS);
                    System.out.println("3) " + StatusTask.DONE);
                    System.out.print("Ввод: ");
                    switch (scanner.nextInt()){
                        case 1:
                            updatedTask.setStatus(StatusTask.NEW);
                            break;
                        case 2:
                            updatedTask.setStatus(StatusTask.IN_PROGRESS);
                            break;
                        case 3:
                            updatedTask.setStatus(StatusTask.DONE);
                            break;
                        default:
                            System.out.println("Неверное значение.");
                    }
                    break;
                case 3:
                    System.out.print("Новое описание: ");
                    scanner.nextLine();
                    updatedTask.setDescription(scanner.nextLine());
                    break;
                case 0:

                    tasks.put(updatedTask.getId(), updatedTask);
                    return updatedTask;

                default:
                    System.out.println("Введено неверное значение!");

            }
        }




    }

    public Epic createEpic(Epic epic) {
        if (epic.getId() < 0) {
            return epic;
        }

        int newId = generateId();
        epic.setId(newId);

        epics.put(newId, epic);
        return epic;
    }

    public Epic updateEpic(Epic updatedEpic) {
        if (updatedEpic.getId() < 0) {
            return updatedEpic;
        }

        if (!epics.containsKey(updatedEpic.getId())) {
            return updatedEpic;
        }
        Scanner scanner = new Scanner(System.in);
        while (true) {

            System.out.println("\t\tЧто вы бы хотели изменить в вашем Epic?");
            System.out.println("1) name: " + updatedEpic.getName() + ";");
            System.out.println("2) description: " + updatedEpic.getDescription() + ";");
            System.out.println("\t\t0 - выход.");
            System.out.print("Ввод: ");

            switch (scanner.nextInt()) {
                case 1:

                    System.out.print("Новое имя: ");
                    scanner.nextLine();
                    updatedEpic.setName(scanner.nextLine());
                    break;

                case 2:

                    System.out.print("Новое описание: ");
                    scanner.nextLine();
                    updatedEpic.setDescription(scanner.nextLine());
                    break;
                case 0:

                    epics.put(updatedEpic.getId(), updatedEpic);
                    return updatedEpic;

                default:
                    System.out.println("Введено неверное значение!");

            }
        }
    }




    public Subtask createSubtask(Subtask subtask) {

        if (subtask.getId() < 0) {
            return subtask;
        }
        if (subtask.getEpicId() < 0){
            return subtask;
        }

        if (!epics.containsKey(subtask.getEpicId())) {
            return subtask;
        }

        int newId = generateId();
        subtask.setId(newId);
        subtasks.put(newId, subtask);

        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubTask(subtask.getId());
        recalculateEpicStatus(epic);

        return subtask;
    }

    public Subtask updateSubtask(Subtask updatedSubtask) {
        if (updatedSubtask.getId() < 0) {
            return updatedSubtask;
        }

        if (!subtasks.containsKey(updatedSubtask.getId())) {
            return updatedSubtask;
        }

        if (!epics.containsKey(updatedSubtask.getEpicId())) {
            return updatedSubtask;
        }


        Scanner scanner = new Scanner(System.in);

        while (true) {

            System.out.println("\t\tЧто вы бы хотели изменить в вашем Subtask?");
            System.out.println("1) name: " + updatedSubtask.getName() + ";");
            System.out.println("2) status: " + updatedSubtask.getStatus() + ";");
            System.out.println("3) description: " + updatedSubtask.getDescription() + ";");
            System.out.println("\t\t0 - выход.");
            System.out.print("Ввод: ");

            switch (scanner.nextInt()) {
                case 1:
                    System.out.print("Новое имя: ");
                    scanner.nextLine();
                    updatedSubtask.setName(scanner.nextLine());
                    break;
                case 2:
                    System.out.println("Выберите какой статус вы бы хотели присвоить");
                    System.out.println("1) " + StatusTask.NEW);
                    System.out.println("2) " + StatusTask.IN_PROGRESS);
                    System.out.println("3) " + StatusTask.DONE);
                    System.out.print("Ввод: ");
                    switch (scanner.nextInt()) {
                        case 1:
                            updatedSubtask.setStatus(StatusTask.NEW);
                            break;
                        case 2:
                            updatedSubtask.setStatus(StatusTask.IN_PROGRESS);
                            break;
                        case 3:
                            updatedSubtask.setStatus(StatusTask.DONE);
                            break;
                        default:
                            System.out.println("Неверное значение.");
                    }
                    break;
                case 3:
                    System.out.print("Новое описание: ");
                    scanner.nextLine();
                    updatedSubtask.setDescription(scanner.nextLine());
                    break;
                case 0:
                    recalculateEpicStatus(epics.get(updatedSubtask.getEpicId()));
                    subtasks.put(updatedSubtask.getId(), updatedSubtask);
                    return updatedSubtask;

                default:
                    System.out.println("Введено неверное значение!");

            }

        }
    }

// ======================= Дополнительные методы =======================

    private int generateId() {
        return GeneratedId++;
    }

    private void recalculateEpicStatus(Epic epic) {
        List<Integer> subtaskIds = epic.getSubTasks();
        if (subtaskIds.isEmpty()) {
            epic.setStatus(StatusTask.NEW);
            return;
        }

        boolean allDone = true;
        boolean allNew = true;

        for (Integer id : subtaskIds){
            if (subtasks.get(id) == null){
                continue;
            }
            if (subtasks.get(id).getStatus() != StatusTask.DONE) {
                allDone = false;

            }
            if (subtasks.get(id).getStatus() != StatusTask.NEW){
                allNew = false;
            }
        }

        if (allNew) {
            epic.setStatus(StatusTask.NEW);
        } else if (allDone) {
            epic.setStatus(StatusTask.DONE);
        } else {
            epic.setStatus(StatusTask.IN_PROGRESS);
        }

    }

    public ArrayList<Subtask> getSubtaskByIndexEpic(int indexEpic) {
        Epic epic = epics.get(indexEpic);
        ArrayList<Subtask> subtaskArrayList = new ArrayList<>();

        if (epic != null){
            Subtask subtask;
            for (Integer id : epic.getSubTasks()) {
                subtask = subtaskArrayList.get(id);

                if (subtask != null){
                    subtaskArrayList.add(subtask);
                }
            }
        }
        return subtaskArrayList;
    }


}


