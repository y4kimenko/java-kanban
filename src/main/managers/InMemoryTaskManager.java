package main.managers;


import main.exceptions.AddTaskException;
import main.exceptions.NotFoundException;
import main.tasks.Epic;
import main.tasks.StatusTask;
import main.tasks.Subtask;
import main.tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected int id = 1;
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistoryManager();
    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");

    @Override
    public void addTask(Task task) throws AddTaskException {
        if (hasNoIntersections(task)) {
            Task copyTask = new Task(task.getName(), task.getDescription(), task.getStatus(), task.getDuration(),
                    task.getStartTime());
            copyTask.setId(id);
            tasks.put(copyTask.getId(), copyTask);
        } else {
            throw new AddTaskException(String.format("Задача %s не может быть добавлена," +
                    " так как накладывается по времени на уже существующую задачу", task.getName()));
        }
        id++;
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public void deleteTaskById(int id) {
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task == null) {
            throw new NotFoundException("Задача не найдена: id=" + id);
        }
        historyManager.add(task);
        return task;
    }



    @Override
    public void updateTask(Task task) throws AddTaskException {
        boolean isIntersect = false;

        for (Task task1 : getAllTasks()) {
            if (task1.getId() == task.getId()) {
                continue;
            }
            if (task1.checkIntersection(task)) {
                isIntersect = true;
                break;
            }
        }

        if (!isIntersect) {
            Task copyTask = new Task(task.getName(), task.getDescription(), task.getStatus(), task.getId(),
                    task.getDuration(), task.getStartTime());
            if (tasks.containsKey(copyTask.getId())) {
                tasks.put(copyTask.getId(), copyTask);
            } else {
                throw new NotFoundException("Нет такой задачи");
            }
        } else {
            throw new AddTaskException(String.format("Задача %s не может быть добавлена," +
                    " так как накладывается по времени на уже существующую задачу", task.getName()));
        }
    }

    @Override
    public void addEpic(Epic epic) {
        Epic copyEpic = new Epic(epic.getName(), epic.getDescription());
        copyEpic.setId(id);
        epics.put(copyEpic.getId(), copyEpic);
        id++;
        calculateEpicStatus(copyEpic);
        calculateEpicTime(copyEpic);
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteEpicById(int id) {
        epics.get(id).getSubtasksIds()
                .stream()
                .peek(subtaskId -> {
                    subtasks.remove(subtaskId);
                    historyManager.remove(subtaskId);
                })
                .toList();
        epics.remove(id);
        historyManager.remove(id);
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            throw new NotFoundException("Эпик не найден: id=" + id);
        }
        historyManager.add(epic);
        return epic;
    }

    @Override
    public void updateEpic(Epic epic) {
        Epic copyEpic = new Epic(epic.getName(), epic.getDescription(), epic.getId());
        if (epics.containsKey(copyEpic.getId())) {
            epics.get(copyEpic.getId()).getSubtasksIds()
                    .stream()
                    .peek(subtasks::remove)
                    .toList();
            epics.put(copyEpic.getId(), copyEpic);
            calculateEpicStatus(copyEpic);
            calculateEpicTime(copyEpic);
        } else {
            throw new NotFoundException("Эпик не найден");
        }
    }

    @Override
    public void addSubtask(Subtask subtask) throws AddTaskException {
        if (hasNoIntersections(subtask)) {
            Subtask copySubtask = new Subtask(subtask.getName(), subtask.getDescription(), subtask.getStatus(),
                    subtask.getEpicId(), subtask.getDuration(), subtask.getStartTime());
            copySubtask.setId(id);
            if (epics.containsKey(copySubtask.getEpicId())) {
                subtasks.put(copySubtask.getId(), copySubtask);

                Epic epicToAddId = epics.get(copySubtask.getEpicId());
                epicToAddId.getSubtasksIds().add(copySubtask.getId());

                calculateEpicStatus(epicToAddId);
                calculateEpicTime(epicToAddId);
            } else {
                throw new AddTaskException("Нет такого эпика, чтобы добавить ыв него подзадачу");
            }
        } else {
            throw new AddTaskException(String.format("Подзадача %s не может быть добавлена," +
                    " так как накладывается по времени на уже существующую задачу", subtask.getName()));
        }
        id++;
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.clear();
        getAllEpics()
                .stream()
                .peek(epic -> {
                    epic.getSubtasksIds().clear();
                    calculateEpicStatus(epic);
                    calculateEpicTime(epic);
                })
                .toList();
    }

    @Override
    public void deleteSubtaskById(int id) {
        Epic epicToDeleteSubtask = epics.get(subtasks.get(id).getEpicId());
        epicToDeleteSubtask.getSubtasksIds().removeIf(number -> number == id);
        subtasks.remove(id);
        calculateEpicStatus(epicToDeleteSubtask);
        calculateEpicTime(epicToDeleteSubtask);
        historyManager.remove(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask == null) {
            throw new NotFoundException("Не найдена такая подзадача: id=" + id);
        }
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public void updateSubtask(Subtask subtask) throws AddTaskException {
        boolean isIntersect = false;

        for (Subtask subtask1 : getAllSubtasks()) {
            if (subtask1.getId() == subtask.getId()) {
                continue;
            }
            if (subtask1.checkIntersection(subtask)) {
                isIntersect = true;
                break;
            }
        }

        if (!isIntersect) {
            Subtask copySubtask = new Subtask(subtask.getName(), subtask.getDescription(), subtask.getStatus(),
                    subtask.getEpicId(), subtask.getId(), subtask.getDuration(), subtask.getStartTime());
            if (subtasks.containsKey(copySubtask.getId()) && epics.containsKey(copySubtask.getEpicId()) &&
                    epics.get(copySubtask.getEpicId()).getSubtasksIds().contains(copySubtask.getId())) {
                subtasks.put(copySubtask.getId(), copySubtask);
                calculateEpicStatus(epics.get(copySubtask.getEpicId()));
                calculateEpicTime(epics.get(copySubtask.getEpicId()));
            } else {
                throw new NotFoundException("Нет такой подзадачи или эпика");
            }
        } else {
            throw new AddTaskException(String.format("Подзадача %s не может быть добавлена," +
                    " так как накладывается по времени на уже существующую задачу", subtask.getName()));
        }
    }

    @Override
    public List<Subtask> getSubtasksOfEpic(int epicId) {
        if (epics.containsKey(epicId)) {
            Epic epicWeNeed = epics.get(epicId);
            ArrayList<Subtask> subtasksWeNeed = new ArrayList<>();
            List<Integer> subtasksWeNeedIds = epicWeNeed.getSubtasksIds()
                    .stream()
                    .peek(id -> subtasksWeNeed.add(subtasks.get(id)))
                    .toList();
            return subtasksWeNeed;
        } else {
            throw new NotFoundException("Нет такого эпика");
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    public boolean hasNoIntersections(Task task) {
        return getPrioritizedTasks().stream()
                .noneMatch(existingTask -> existingTask.checkIntersection(task));
    }

    public Set<Task> getPrioritizedTasks() {
        Set<Task> sortedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
        getAllTasks().stream()
                .filter(task -> task.getStartTime() != null)
                .forEach(sortedTasks::add);

        getAllSubtasks().stream()
                .filter(task -> task.getStartTime() != null)
                .forEach(sortedTasks::add);
        return sortedTasks;
    }

    protected void calculateEpicStatus(Epic epic) {
        List<Subtask> subtasksInEpic = new ArrayList<>();
        epic.getSubtasksIds()
                .stream()
                .peek(id -> subtasksInEpic.add(subtasks.get(id)))
                .toList();

        List<StatusTask> statusesOfSubtasks = new ArrayList<>();

        subtasksInEpic.stream()
                .peek(subtask -> statusesOfSubtasks.add(subtask.getStatus()))
                .toList();

        if (subtasksInEpic.isEmpty()) {
            epic.setStatus(StatusTask.NEW);
        }

        int statusesSum = 0;

        for (StatusTask status : statusesOfSubtasks) {
            if (status.equals(StatusTask.NEW)) {
                statusesSum += 0;
            } else if (status.equals(StatusTask.IN_PROGRESS)) {
                statusesSum += 1;
                break;
            } else if (status.equals((StatusTask.DONE))) {
                statusesSum += 2;
            }
        }

        if (statusesSum == 0) {
            epic.setStatus(StatusTask.NEW);
        } else if (statusesSum == 2 * statusesOfSubtasks.size()) {
            epic.setStatus(StatusTask.DONE);
        } else {
            epic.setStatus(StatusTask.IN_PROGRESS);
        }
    }

    protected void calculateEpicTime(Epic epic) {
        List<Subtask> epicSubtasks = getSubtasksOfEpic(epic.getId()).stream()
                .filter(subtask -> subtask.getStartTime() != null)
                .toList();

        if (epicSubtasks.isEmpty()) {
            epic.setStartTime(null);
            epic.setEndTime(null);
            epic.setDuration(null);
            return;
        }

        LocalDateTime start = epicSubtasks.stream()
                .map(Subtask::getStartTime)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        LocalDateTime end = epicSubtasks.stream()
                .map(Subtask::getEndTime)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        epic.setStartTime(start);
        epic.setEndTime(end);
        epic.setDuration(start != null && end != null ? Duration.between(start, end) : null);
    }
}