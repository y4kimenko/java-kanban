import tasks.*;
import java.util.*;


public class TaskManager {
    private static final HashMap<Integer, Task> tasks = new HashMap<>();
    private static final HashMap<Integer, Epic> epics = new HashMap<>();
    private static final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    private int generatedId = 1;

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
            Epic epic = epics.get(subtasks.get(subtaskId).getEpicId());
            if (epic != null) {
                epic.removeSubTask(subtaskId);
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
                epic.cleanSubTasks();
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


        if (!tasks.containsKey(updatedTask.getId())) {
            return updatedTask;
        }

        Task oldTask = tasks.get(updatedTask.getId());

        oldTask.setName(updatedTask.getName());
        oldTask.setDescription(updatedTask.getDescription());
        oldTask.setStatus(updatedTask.getStatus());

        tasks.put(updatedTask.getId(), updatedTask);
        return updatedTask;

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

        if (!epics.containsKey(updatedEpic.getId())) {
            return updatedEpic;
        }

        Epic oldEpic = epics.get(updatedEpic.getId());
        oldEpic.setName(updatedEpic.getName());
        oldEpic.setDescription(updatedEpic.getDescription());

        epics.put(updatedEpic.getId(), updatedEpic);
        return updatedEpic;
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

        if (!subtasks.containsKey(updatedSubtask.getId())) {
            return updatedSubtask;
        }

        if (!epics.containsKey(updatedSubtask.getEpicId())) {
            return updatedSubtask;
        }

        Subtask oldSubtask = subtasks.get(updatedSubtask.getId());

        oldSubtask.setName(updatedSubtask.getName());
        oldSubtask.setDescription(updatedSubtask.getDescription());
        oldSubtask.setStatus(updatedSubtask.getStatus());

        subtasks.put(updatedSubtask.getId(), updatedSubtask);
        return updatedSubtask;
    }

// ======================= Дополнительные методы =======================

    private int generateId() {
        return generatedId++;
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


