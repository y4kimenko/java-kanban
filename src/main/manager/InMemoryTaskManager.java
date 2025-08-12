package main.manager;

import main.tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();

    protected int generatedId = 1;

    protected final HistoryManager historyManager = Managers.getDefaultHistoryManager();

    // В приоритет попадают только задачи/сабтаски с валидным временем
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

    /**
     * Проверка пересечений через проход по ВСЕМ задачам в prioritized (по требованию).
     * Важно: НЕ удаляем кандидата из множества и исключаем самокасание по id.
     */
    private void ensureNoOverlap(Task candidate) {
        if (!hasTime(candidate)) return;
        boolean overlaps = prioritized.stream()
                .filter(this::hasTime)
                .filter(t -> t.getId() != candidate.getId()) // исключаем саму задачу
                .anyMatch(t -> isOverlapped(t, candidate));
        if (overlaps) {
            throw new IllegalStateException("Пересечение по времени с другой задачей");
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

        // Сброс эпиков: статус + ВСЕ временные поля, включая endTime
        for (Epic e : epics.values()) {
            e.cleanSubTasks();
            e.setStatus(StatusTask.NEW);
            // сбрасываем время через пересчёт по пустому списку
            recalcEpic(e);
        }
    }

    // ==================== создание/обновление ====================
    @Override
    public Task createTask(Task task) {
        if (task == null) return null;
        int id = generateId();
        task.setId(id);
        ensureNoOverlap(task);          // проверяем пересечение
        tasks.put(id, task);
        addToPrioritized(task);         // добавляем
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

        // 1) проверяем пересечения КАК ЕСТЬ (кандидат ещё не удалён из prioritized)
        ensureNoOverlap(updatedTask);
        // 2) удаляем старую версию и 3) добавляем новую
        removeFromPrioritized(tasks.get(updatedTask.getId()));
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
        // ВРЕМЯ И СТАТУС НЕ ПЕРЕСЧИТЫВАЕМ (по правке №3) — т.к. меняются только имя/описание
        return old;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        if (subtask == null) return null;
        if (!epics.containsKey(subtask.getEpicId())) return subtask;

        int id = generateId();
        subtask.setId(id);
        ensureNoOverlap(subtask);                // проверяем
        subtasks.put(id, subtask);
        epics.get(subtask.getEpicId()).addSubTask(id);
        addToPrioritized(subtask);               // добавляем

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

        // 1) проверяем пересечения (кандидат ещё в prioritized в старом состоянии)
        ensureNoOverlap(updatedSubtask);
        // 2) удаляем старую версию и 3) добавляем новую
        removeFromPrioritized(subtasks.get(updatedSubtask.getId()));
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
        // Собираем подзадачи эпика по его списку id
        List<Subtask> list = epic.getSubTasks().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // --- Статус ---
        if (list.isEmpty()) {
            epic.setStatus(StatusTask.NEW);
            epic.setStartTime(null);
            epic.setDuration(null);
            epic.setEndTime(null);
            return;
        }

        boolean allNew  = list.stream().allMatch(s -> s.getStatus() == StatusTask.NEW);
        boolean allDone = list.stream().allMatch(s -> s.getStatus() == StatusTask.DONE);
        if (allDone) {
            epic.setStatus(StatusTask.DONE);
        } else if (allNew) {
            epic.setStatus(StatusTask.NEW);
        } else {
            epic.setStatus(StatusTask.IN_PROGRESS);
        }

        // --- Время ---
        // duration = сумма, если у кого-то duration задана, иначе null
        boolean anyDuration = list.stream().anyMatch(s -> s.getDuration() != null);
        Duration total = list.stream()
                .map(Subtask::getDuration)
                .filter(Objects::nonNull)
                .reduce(Duration.ZERO, Duration::plus);
        epic.setDuration(anyDuration ? total : null);

        // startTime = минимум по startTime, endTime = максимум по endTime; если нигде не задано — null
        LocalDateTime start = list.stream()
                .map(Subtask::getStartTime)
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder())
                .orElse(null);
        epic.setStartTime(start);

        LocalDateTime end = list.stream()
                .map(Subtask::getEndTime)
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElse(null);
        epic.setEndTime(end);
    }


    // ==================== связи ====================
    @Override
    public ArrayList<Subtask> getSubtaskByIndexEpic(int epicId) {
        // Правка №4: идём по СПИСКУ ИД эпика, а не по всей мапе подзадач
        Epic epic = epics.get(epicId);
        if (epic == null) return new ArrayList<>();
        return epic.getSubTasks().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
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
