package main.server;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import main.adapters.DurationAdapter;
import main.adapters.LocalDateTimeAdapter;
import main.httphandlers.*;
import main.managers.InMemoryTaskManager;
import main.managers.TaskManager;
import main.tasks.Epic;
import main.tasks.StatusTask;
import main.tasks.Subtask;
import main.tasks.Task;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

public class HttpTaskServer {
    private final TaskManager taskManager;
    private final HttpServer httpServer;
    private final Gson gson;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
        httpServer = HttpServer.create(new InetSocketAddress(8080), 0);
        setContexts();
    }

    private void setContexts() {
        try {
            httpServer.createContext("/tasks", new TaskHandler(taskManager, gson));
            httpServer.createContext("/subtasks", new SubtaskHandler(taskManager, gson));
            httpServer.createContext("/epics", new EpicHandler(taskManager, gson));
            httpServer.createContext("/history", new HistoryHandler(taskManager, gson));
            httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager, gson));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void start() {
        System.out.println("HTTP-сервер запущен на 8080 порту!");
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(0);
    }

    public static void main(String[] args) {
        try {
            HttpTaskServer taskServer = new HttpTaskServer(createTestManager());
            taskServer.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static TaskManager createTestManager() {
//        File file = new File("src" + File.separator + "file.csv");
//        //File.createTempFile("file", ".csv");
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Task task1 = new Task("Переезд", "В новую квартиру", StatusTask.NEW, Duration.ofMinutes(20),
                LocalDateTime.of(2025, Month.JULY, 11, 14, 50));
        Task task2 = new Task("Переезд1", "В новый дом", StatusTask.NEW, Duration.ofMinutes(20),
                LocalDateTime.of(2025, Month.JULY, 12, 15, 0));
        Epic epic1 = new Epic("Перевод денег", "Перевести деньги другу");
        Subtask subtask1 = new Subtask("Приложение банка", "Открыть приложение банка",
                StatusTask.IN_PROGRESS, 3, Duration.ofMinutes(90),
                LocalDateTime.of(2025, Month.JULY, 11, 15, 20));
        Subtask subtask2 = new Subtask("Открыть вкладку расходов", "Открытие вкладки расходов",
                StatusTask.NEW, 3, Duration.ofMinutes(15),
                LocalDateTime.of(2025, Month.JULY, 11, 16, 50));
        Subtask subtask3 = new Subtask("Отправка", "Отправка денег другу", StatusTask.DONE, 3,
                Duration.ofMinutes(60), LocalDateTime.of(2025, Month.JULY, 11, 17, 5));
        Epic epic2 = new Epic("Пройти курс", "Пройти курс от ЯП");
        Subtask subtask4 = new Subtask("Тренировка", "Провести тренировку в зале", StatusTask.DONE, 7,
                Duration.ofMinutes(40), LocalDateTime.of(2025, Month.JULY, 12, 17, 45));

        manager.addTask(task1);
        manager.addTask(task2);
        manager.addEpic(epic1);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);
        manager.addEpic(epic2);
        manager.addSubtask(subtask4);
        manager.getSubtaskById(4);
        manager.getEpicById(3);
        return manager;
    }

    public Gson getGson() {
        return gson;
    }
}