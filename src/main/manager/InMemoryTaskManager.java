package main.manager;

import main.tasks.Epic;
import main.tasks.StatusTask;
import main.tasks.Subtask;
import main.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    private final HistoryManager historyManager = Managers.getDefaultHistoryManager();


    private int generatedId = 1;

    // ==================== вывод списков ==============================
    @Override
    public void printAllTasks() {
        System.out.println(tasks);
    }

    @Override
    public void printAllEpics() {
        System.out.println(epics);
    }

    @Override
    public void printAllSubtasks() {
        System.out.println(subtasks);
    }

    // ======================= Удаление по индификатору =======================
    @Override
    public boolean removeTaskById(int taskId) {
        historyManager.remove(taskId); // Удаление из истории

        return tasks.remove(taskId) != null;
    }

    @Override
    public boolean removeSubtaskById(int subtaskId) {
        Subtask removed = subtasks.remove(subtaskId);

        if (removed == null) {
            return false;
        }




        // удаление из списка связанного epic
        Epic epic = epics.get(removed.getEpicId());
        if (epic != null) {
            epic.removeSubTask(subtaskId);
            recalculateEpicStatus(epic);
        }

        historyManager.remove(removed.getId()); // Удаление из истории

        return true;

    }

    @Override
    public boolean removeEpicById(int epicId) {
        Epic removed = epics.remove(epicId);

        historyManager.remove(removed.getId()); // Удаление из истории

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

    @Override
    public Task searchTaskById(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);

        return task;
    }

    @Override
    public Epic searchEpicById(int id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);

        return epic;
    }

    @Override
    public Subtask searchSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);

        return subtasks.get(id);
    }

// ======================= Удаление всех задач =======================

    @Override
    public void removeAllTasks() {
        for (Task task : tasks.values()) { // Удаление истории
            historyManager.remove(task.getId());
        }

        tasks.clear();
    }

    @Override
    public void removeAllEpics() {
        if (!epics.isEmpty()) {
            for (Epic epic : epics.values()) {  // Удаление истории
                historyManager.remove(epic.getId());
            }
            epics.clear();

            for (Subtask subtask : subtasks.values()) { // Удаление истории
                historyManager.remove(subtask.getId());
            }
            subtasks.clear();
        }

    }

    @Override
    public void removeAllSubtasks() {
        if (!subtasks.isEmpty()) {
            for (Subtask subtask : subtasks.values()) { // Удаление истории
                historyManager.remove(subtask.getId());
            }

            subtasks.clear();
            for (Epic epic : epics.values()) {
                epic.cleanSubTasks();
                epic.setStatus(StatusTask.NEW);
            }
        }

    }

// ======================= Методы создания и обновления =======================

    @Override
    public Task createTask(Task task) {
        if (task.getId() < 0) {
            return task;
        }

        int newId = generateId();
        task.setId(newId);
        tasks.put(newId, task);
        return task;
    }

    @Override
    public Task createTaskWithID(Task task, int id) {
        if (task.getId() < 0) {
            return task;
        }
        if (checkoutId(id, task)) {
            task.setId(id);
            tasks.put(id, task);
            return task;
        }

        return task;
    }

    @Override
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


    @Override
    public Epic createEpic(Epic epic) {
        if (epic.getId() < 0) {
            return epic;
        }

        int newId = generateId();
        epic.setId(newId);

        epics.put(newId, epic);
        return epic;
    }

    @Override
    public Epic createEpicWithID(Epic epic, int id) {
        if (epic.getId() < 0) {
            return epic;
        }
        if (checkoutId(id, epic)) {
            epic.setId(id);
            epics.put(id, epic);
            return epic;
        }

        return epic;
    }


    @Override
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


    @Override
    public Subtask createSubtask(Subtask subtask) {

        if (subtask.getId() < 0) {
            return subtask;
        }
        if (subtask.getEpicId() < 0) {
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

    @Override
    public Subtask createSubtaskWithID(Subtask subtask, int id) {
        if (subtask.getId() < 0) {
            return subtask;
        }
        if (subtask.getEpicId() < 0) {
            return subtask;
        }

        if (checkoutId(id, subtask)) {
            subtask.setId(id);
            subtasks.put(id, subtask);

            Epic epic = epics.get(subtask.getEpicId());
            epic.addSubTask(subtask.getId());
            recalculateEpicStatus(epic);

            return subtask;
        }

        return subtask;
    }

    @Override
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

    private boolean checkoutId(int id, Task task) {
        if (id <= 0) {
            return false;
        }
        if (task.getClass() == Task.class) {
            if (tasks.containsKey(id)) {
                task = tasks.get(id);
                return task.getId() != id;
            }
        } else if (task.getClass() == Epic.class) {
            if (epics.containsKey(id)) {
                task = epics.get(id);
                return task.getId() != id;
            }
        } else if (task.getClass() == Subtask.class) {
            if (subtasks.containsKey(id)) {
                task = subtasks.get(id);
                return task.getId() != id;
            }
        }
        return true;
    }


    private void recalculateEpicStatus(Epic epic) {
        List<Integer> subtaskIds = epic.getSubTasks();
        if (subtaskIds.isEmpty()) {
            epic.setStatus(StatusTask.NEW);
            return;
        }

        boolean allDone = true;
        boolean allNew = true;

        for (Integer id : subtaskIds) {
            if (subtasks.get(id) == null) {
                continue;
            }
            if (subtasks.get(id).getStatus() != StatusTask.DONE) {
                allDone = false;

            }
            if (subtasks.get(id).getStatus() != StatusTask.NEW) {
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

    @Override
    public ArrayList<Subtask> getSubtaskByIndexEpic(int indexEpic) {
        Epic epic = epics.get(indexEpic);
        ArrayList<Subtask> subtaskArrayList = new ArrayList<>();

        if (epic != null) {
            Subtask subtask;
            for (Integer id : epic.getSubTasks()) {
                subtask = subtaskArrayList.get(id);

                if (subtask != null) {
                    subtaskArrayList.add(subtask);
                }
            }
        }
        return subtaskArrayList;
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(historyManager.getHistory());
    }
}


