package main.tasks;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskTest {

    @Test
    void taskEqualToEachOtherIfTheirIdIsEqual() {
        Task task1 = new Task("test1", "descrption1", StatusTask.NEW);
        Task task2 = new Task(task1);

        boolean equal = task1.equals(task2);

        assertTrue(equal);
    }


}