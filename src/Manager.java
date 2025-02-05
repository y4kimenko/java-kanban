import Tasks.Epic;
import Tasks.StatusTask;
import Tasks.Subtask;
import Tasks.Task;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


public class Manager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    public int GeneratedId = 0;


// ======================= Удаление по индификатору =======================
    public boolean removeTaskById(int taskId) {
        if (tasks.containsKey(taskId)) {
            tasks.remove(taskId);
            return true;
        } else {
            return false;
        }

    }

    public boolean removeSubtaskById(int subtaskId) {
        Subtask removed = subtasks.remove(subtaskId);

        if (removed != null) {
            // удаление из списка свзанного epic
            Epic epic = epics.get(subtasks.get(subtaskId).getIndexEpic());
            if (epic != null) {
                epic.getSubTasks().remove(subtaskId);
            }

            return true;
        } else {
            return false;
        }
    }

    public boolean removeEpicById(int epicId) {
        Epic removed = epics.remove(epicId);
        if (removed != null) {
            if (removed.getSubTasks() != null) {
                ArrayList<Integer> removedSubtasks = removed.getSubTasks();
                for (Integer subtaskId : removedSubtasks) {
                    removeSubtaskById(subtaskId);
                }
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
            for (Subtask subtask : subtasks.values()) {
               subtask.setIndexEpic(null);
            }
            epics.clear();
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
        if (Objects.nonNull(task.getId())) {
            return task;
        }

        int newId = generateId();
        task.setId(newId);

        tasks.put(newId, task);
        return task;
    }

    public Task updateTask(Task updatedTask) {
        if (Objects.nonNull(updatedTask.getId())) {
            return updatedTask;
        }

        if (!tasks.containsKey(updatedTask.getId())) {
            return updatedTask;
        }

        tasks.put(updatedTask.getId(), updatedTask);
        return updatedTask;

    }

    public Epic createEpic(Epic epic) {
        if (Objects.nonNull(epic.getId())) {
            return epic;
        }

        int newId = generateId();
        epic.setId(newId);

        epics.put(newId, epic);
        return epic;
    }

    public Epic updateEpic(Epic updatedEpic) {
        if (Objects.nonNull(updatedEpic.getId())) {
            return updatedEpic;
        }

        if (!epics.containsKey(updatedEpic.getId())) {
            return updatedEpic;
        }

        epics.put(updatedEpic.getId(), updatedEpic);
        return updatedEpic;
    }

    public Subtask createSubtask(Subtask subtask) {
        if (Objects.nonNull(subtask.getId())) {
            return subtask;
        }
        if (Objects.nonNull(subtask.getIndexEpic())){
            return subtask;
        }
        if (!epics.containsKey(subtask.getIndexEpic())) {
            return subtask;
        }

        int newId = generateId();
        subtask.setId(newId);
        subtasks.put(newId, subtask);

        Epic epic = epics.get(subtask.getIndexEpic());
        epic.getSubTasks().add(newId);

        return subtask;
    }

    public Subtask updateSubtask(Subtask updatedSubtask) {
        if (Objects.nonNull(updatedSubtask.getId())) {
            return updatedSubtask;
        }

        if (!subtasks.containsKey(updatedSubtask.getId())) {
            return updatedSubtask;
        }

        if (!subtasks.containsKey(updatedSubtask.getIndexEpic())) {
            return updatedSubtask;
        }

        recalculateEpicStatus(epics.get(updatedSubtask.getIndexEpic()));

        subtasks.put(updatedSubtask.getId(), updatedSubtask);
        return updatedSubtask;

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
        ArrayList<Subtask> subtasks = new ArrayList<>();

        if (epic != null){
            for (Integer id : epic.getSubTasks()) {
                if (subtasks.get(id) != null){
                    subtasks.add(subtasks.get(id));
                }
            }
        }
        return subtasks;
    }

}


