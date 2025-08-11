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

class FileBackedTaskManagerTest {

    @TempDir
    Path tempDir;

    @Test
    void saveAndReload_keepsDataAndPriority() throws Exception {
        File file = tempDir.resolve("tasks.csv").toFile();
        FileBackedTaskManager m1 = FileBackedTaskManager.loadFromFile(file);

        Epic e = m1.createEpic(new Epic("E",""));
        Task t = m1.createTask(new Task("T","", StatusTask.NEW,
                LocalDateTime.parse("2025-08-11T09:00"), Duration.ofMinutes(30)));
        Subtask s = m1.createSubtask(new Subtask("S","", StatusTask.NEW,
                LocalDateTime.parse("2025-08-11T08:00"), Duration.ofMinutes(15), e.getId()));

        // reload
        FileBackedTaskManager m2 = FileBackedTaskManager.loadFromFile(file);

        assertNotNull(m2.searchEpicById(e.getId()));
        assertNotNull(m2.searchTaskById(t.getId()));
        assertNotNull(m2.searchSubtaskById(s.getId()));

        List<Task> prio = m2.getPrioritizedTasks();
        assertEquals(s.getId(), prio.get(0).getId());
        assertEquals(t.getId(), prio.get(1).getId());
    }

    @Test
    void brokenLine_throwsManagerSaveException() throws Exception {
        File bad = tempDir.resolve("bad.csv").toFile();
        Files.writeString(bad.toPath(),
                "id,type,name,status,description,epicId,startTime,durationMinutes\n" +
                        "oops,this,is,not,valid\n");
        assertThrows(ManagerSaveException.class, () -> FileBackedTaskManager.loadFromFile(bad));
    }
}
