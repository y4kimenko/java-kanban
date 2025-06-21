package main.tasks;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SubtaskTest {
    @Test
    void subtaskEqualToEachOtherIfTheirIdIsEqual() {
        Subtask subtask1 = new Subtask("test1", "descrption1", StatusTask.NEW, 1);
        Subtask subtask2 = new Subtask(subtask1);

        boolean equal = subtask1.equals(subtask2);

        assertTrue(equal);
    }

    @Test
    void testSubtaskCannotHaveItselfAsEpic() {
        Subtask subtask = new Subtask("Subtask 1", "A subtask", StatusTask.NEW, 1);

        assertNotEquals(subtask.getId(), subtask.getEpicId(),
                "Subtask should not have itself as its own epic");
    }


}