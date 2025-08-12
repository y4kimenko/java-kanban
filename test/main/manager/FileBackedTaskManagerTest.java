package main.manager;

import main.exception.ManagerSaveException;
import main.tasks.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    @TempDir
    Path tempDir;

    private File file;

    @Override
    protected FileBackedTaskManager createManager() {
        file = tempDir.resolve("tasks.csv").toFile();
        return FileBackedTaskManager.loadFromFile(file);
    }

    // --- Специфичные тесты для FileBackedTaskManager остаются ---

    @Test
    void saveAndReload_keepsAllFieldsAndPriority() {
        FileBackedTaskManager m1 = createManager();

        Epic e1 = m1.createEpic(new Epic("E","desc"));
        Task t1 = m1.createTask(new Task("T","t-desc", StatusTask.NEW,
                LocalDateTime.parse("2025-08-11T09:00"), Duration.ofMinutes(30)));
        Subtask s1 = m1.createSubtask(new Subtask("S","s-desc", StatusTask.NEW,
                LocalDateTime.parse("2025-08-11T08:00"), Duration.ofMinutes(15), e1.getId()));

        // reload
        FileBackedTaskManager m2 = FileBackedTaskManager.loadFromFile(file);

        Epic e2 = m2.searchEpicById(e1.getId());
        Task t2 = m2.searchTaskById(t1.getId());
        Subtask s2 = m2.searchSubtaskById(s1.getId());

        // ---- сравнение Task (все поля)
        assertNotNull(t2);
        assertEquals(t1.getId(), t2.getId());
        assertEquals(t1.getName(), t2.getName());
        assertEquals(t1.getDescription(), t2.getDescription());
        assertEquals(t1.getStatus(), t2.getStatus());
        assertEquals(t1.getStartTime(), t2.getStartTime());
        assertEquals(t1.getDuration(), t2.getDuration());
        assertEquals(t1.getEndTime(), t2.getEndTime());

        // ---- сравнение Epic (+ endTime)
        assertNotNull(e2);
        assertEquals(e1.getId(), e2.getId());
        assertEquals(e1.getName(), e2.getName());
        assertEquals(e1.getDescription(), e2.getDescription());
        assertEquals(e1.getStatus(), e2.getStatus());
        assertEquals(e1.getStartTime(), e2.getStartTime());
        assertEquals(e1.getDuration(), e2.getDuration());
        assertEquals(e1.getEndTime(), e2.getEndTime());

        // ---- сравнение Subtask (+ epicId)
        assertNotNull(s2);
        assertEquals(s1.getId(), s2.getId());
        assertEquals(s1.getName(), s2.getName());
        assertEquals(s1.getDescription(), s2.getDescription());
        assertEquals(s1.getStatus(), s2.getStatus());
        assertEquals(s1.getStartTime(), s2.getStartTime());
        assertEquals(s1.getDuration(), s2.getDuration());
        assertEquals(s1.getEndTime(), s2.getEndTime());
        assertEquals(s1.getEpicId(), s2.getEpicId());

        // проверка порядка приоритета: S (08:00) перед T (09:00)
        List<Task> prio = m2.getPrioritizedTasks();
        assertEquals(2, prio.size());
        assertEquals(s1.getId(), prio.get(0).getId());
        assertEquals(t1.getId(), prio.get(1).getId());
    }

    @Test
    void brokenLine_throwsManagerSaveException() throws Exception {
        File bad = tempDir.resolve("bad.csv").toFile();
        Files.writeString(bad.toPath(),
                FileBackedTaskManager.CSV_HEADER + "\n" +
                        "oops,this,is,not,valid\n");
        assertThrows(ManagerSaveException.class, () -> FileBackedTaskManager.loadFromFile(bad));
    }
}
