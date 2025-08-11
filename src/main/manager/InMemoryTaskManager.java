package main.manager;

import main.tasks.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();

    protected int generatedId = 1;

    protected final HistoryManager historyManager = Managers.getDefaultHistoryManager();

    // В приоритет попадают только задачи/сабтаски с ненулевым startTime/duration
    private final NavigableSet<Task> prioritized = new TreeSet<>(
            Comparator.comparing(Task::getStartTime)
                    .thenComparingInt(Task::getId)
    );

    private boolean hasTime(Task t) {
        return t != null && t.getStartTime() != null && t.getDuration() != null && t.getEndTime() != null;
    }

    private void addToPrioritized(Task t) {
        if (t.getTypeTask() == TypeTask.EPIC) return;
        if (hasTime(t)) prioritized.add(t);
    }

    private void removeFromPrioritized(Task t) {
        if (t.getTypeTask() == TypeTask.EPIC) return;
        prioritized.remove(t);
    }

    // Пересечение полузакрытых интервалов [start, end)
    private boolean isOverlapped(Task a, Task b) {
        LocalDateTime aStart = a.getStartTime();
        LocalDateTime aEnd = a.getEndTime();
        LocalDateTime bStart = b.getStartTime();
        LocalDateTime bEnd = b.getEndTime();
        if (aStart == null || aEnd == null || bStart == null || bEnd == null) return false;
        return aStart.isBefore(bEnd) && bStart.isBefore(aEnd);
    }

    // Проверяем соседей в TreeSet
    private void ensureNoOverlap(Task candidate) {
        if (!hasTime(candidate)) return;
        // временно убрать старое состояние (на случай update)
        prioritized.remove(candidate);
        Task prev = prioritized.lower(candidate);
        Task next = prioritized.higher(candidate);
        if (prev != null && isOverlapped(prev, candidate)) {
            throw new IllegalStateException("Пересечение по времени с задачей id=" + prev.getId());
        }
        if (next != null && isOverlapped(candidate, next)) {
            throw new IllegalStateException("Пересечение по времени с задачей id=" + next.getId());
        }
    }

    // ==================== вывод ====================
    @Override
    public void printAllTasks() {
        System.out.println(tasks.values());
    }

    @Override
    public void printAllEpics() {
        System.out.println(epics.values());
    }

    @Override
    public void printAllSubtasks() {
        System.out.println(subtasks.values());
    }

    // ==================== удаление по id ====================
    @Override
    public boolean removeTaskById(int taskId) {
        Task removed = tasks.remove(taskId);
        if (removed == null) return false;
        historyManager.remove(taskId);
        removeFromPrioritized(removed);
        return true;
    }

    @Override
    public boolean removeSubtaskById(int subtaskId) {
        Subtask removed = subtasks.remove(subtaskId);
        if (removed == null) return false;

        historyManager.remove(subtaskId);
        removeFromPrioritized(removed);

        Epic epic = epics.get(removed.getEpicId());
        if (epic != null) {
            epic.removeSubTask(subtaskId);
            recalcEpic(epic);
        }
        return true;
    }

    @Override
    public boolean removeEpicById(int epicId) {
        Epic removed = epics.remove(epicId);
        if (removed == null) return false;

        historyManager.remove(removed.getId());

        // удаляем сабтаски эпика
        for (Integer sid : new ArrayList<>(removed.getSubTasks())) {
            Subtask st = subtasks.remove(sid);
            if (st != null) {
                historyManager.remove(sid);
                removeFromPrioritized(st);
            }
        }
        return true;
    }

    // ==================== поиск по id ====================
    @Override
    public Task searchTaskById(int id) {
        Task t = tasks.get(id);
        if (t != null) historyManager.add(t);
        return t;
    }

    @Override
    public Epic searchEpicById(int id) {
        Epic e = epics.get(id);
        if (e != null) historyManager.add(e);
        return e;
    }

    @Override
    public Subtask searchSubtaskById(int id) {
        Subtask s = subtasks.get(id);
        if (s != null) historyManager.add(s);
        return s;
    }

    // ==================== удаление всех ====================
    @Override
    public void removeAllTasks() {
        tasks.values().forEach(this::removeFromPrioritized);
        tasks.clear();
        // подчистим историю от задач
        for (Task t : new ArrayList<>(historyManager.getHistory())) {
            if (t.getTypeTask() == TypeTask.TASK) historyManager.remove(t.getId());
        }
    }

    @Override
    public void removeAllEpics() {
        // сначала сабтаски
        for (Subtask s : subtasks.values()) {
            historyManager.remove(s.getId());
            removeFromPrioritized(s);
        }
        subtasks.clear();

        for (Epic e : epics.values()) {
            historyManager.remove(e.getId());
        }
        epics.clear();
    }

    @Override
    public void removeAllSubtasks() {
        for (Subtask s : subtasks.values()) {
            historyManager.remove(s.getId());
            removeFromPrioritized(s);
        }
        subtasks.clear();

        // сброс эпиков
        for (Epic e : epics.values()) {
            e.cleanSubTasks();
            e.setStatus(StatusTask.NEW);
            e.setDuration(null);
            e.setStartTime(null);
        }
    }

    // ==================== создание/обновление ====================
    @Override
    public Task createTask(Task task) {
        if (task == null) return null;
        int id = generateId();
        task.setId(id);
        ensureNoOverlap(task);
        tasks.put(id, task);
        addToPrioritized(task);
        return task;
    }

    @Override
    public Task createTaskWithID(Task task, int id) {
        if (task == null) return null;
        if (id <= 0 || tasks.containsKey(id)) return task;
        task.setId(id);
        ensureNoOverlap(task);
        tasks.put(id, task);
        addToPrioritized(task);
        if (id >= generatedId) generatedId = id + 1;
        return task;
    }

    @Override
    public Task updateTask(Task updatedTask) {
        if (updatedTask == null) return null;
        if (!tasks.containsKey(updatedTask.getId())) return updatedTask;
        removeFromPrioritized(tasks.get(updatedTask.getId()));
        ensureNoOverlap(updatedTask);
        tasks.put(updatedTask.getId(), updatedTask);
        addToPrioritized(updatedTask);
        return updatedTask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        if (epic == null) return null;
        int id = generateId();
        epic.setId(id);
        epics.put(id, epic);
        return epic;
    }

    @Override
    public Epic createEpicWithID(Epic epic, int id) {
        if (epic == null) return null;
        if (id <= 0 || epics.containsKey(id)) return epic;
        epic.setId(id);
        epics.put(id, epic);
        if (id >= generatedId) generatedId = id + 1;
        return epic;
    }

    @Override
    public Epic updateEpic(Epic updatedEpic) {
        if (updatedEpic == null) return null;
        if (!epics.containsKey(updatedEpic.getId())) return updatedEpic;
        Epic old = epics.get(updatedEpic.getId());
        old.setName(updatedEpic.getName());
        old.setDescription(updatedEpic.getDescription());
        // статус/время эпика считаются из сабтасков
        recalcEpic(old);
        return old;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        if (subtask == null) return null;
        if (!epics.containsKey(subtask.getEpicId())) return subtask;

        int id = generateId();
        subtask.setId(id);
        ensureNoOverlap(subtask);
        subtasks.put(id, subtask);
        epics.get(subtask.getEpicId()).addSubTask(id);
        addToPrioritized(subtask);

        recalcEpic(epics.get(subtask.getEpicId()));
        return subtask;
    }

    @Override
    public Subtask createSubtaskWithID(Subtask subtask, int id) {
        if (subtask == null) return null;
        if (!epics.containsKey(subtask.getEpicId())) return subtask;
        if (id <= 0 || subtasks.containsKey(id)) return subtask;

        subtask.setId(id);
        ensureNoOverlap(subtask);
        subtasks.put(id, subtask);
        epics.get(subtask.getEpicId()).addSubTask(id);
        addToPrioritized(subtask);
        if (id >= generatedId) generatedId = id + 1;

        recalcEpic(epics.get(subtask.getEpicId()));
        return subtask;
    }

    @Override
    public Subtask updateSubtask(Subtask updatedSubtask) {
        if (updatedSubtask == null) return null;
        if (!subtasks.containsKey(updatedSubtask.getId())) return updatedSubtask;

        removeFromPrioritized(subtasks.get(updatedSubtask.getId()));
        ensureNoOverlap(updatedSubtask);
        subtasks.put(updatedSubtask.getId(), updatedSubtask);
        addToPrioritized(updatedSubtask);

        Epic epic = epics.get(updatedSubtask.getEpicId());
        if (epic != null) recalcEpic(epic);
        return updatedSubtask;
    }

    private int generateId() {
        return generatedId++;
    }

    protected void recalcEpic(Epic epic) {
        List<Subtask> list = epic.getSubTasks().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // статус эпика
        boolean allNew = list.stream().allMatch(s -> s.getStatus() == StatusTask.NEW);
        boolean allDone = !list.isEmpty() && list.stream().allMatch(s -> s.getStatus() == StatusTask.DONE);
        if (list.isEmpty() || allNew) {
            epic.setStatus(StatusTask.NEW);
        } else if (allDone) {
            epic.setStatus(StatusTask.DONE);
        } else {
            epic.setStatus(StatusTask.IN_PROGRESS);
        }

        // временные поля
        epic.recalcFromSubtasks(list);
    }

    // ==================== связи ====================
    @Override
    public ArrayList<Subtask> getSubtaskByIndexEpic(int epicId) {
        return subtasks.values().stream()
                .filter(s -> s.getEpicId() == epicId)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    // ==================== история ====================
    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(historyManager.getHistory());
    }

    // ==================== приоритизация ====================
    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritized);
    }
}
