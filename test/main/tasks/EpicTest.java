package main.tasks;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class EpicTest {
    @Test
    void epicEqualToEachOtherIfTheirIdIsEqual() {
        Epic epic1 = new Epic("test1", "descrption1");
        Epic epic2 = new Epic(epic1);

        boolean equal = epic1.equals(epic2);

        assertTrue(equal);
    }

    @Test
    void ImpossibilityOfAddingEpicToItselfAsASubtask() {
        Epic epic1 = new Epic("test1", "descrption1");
        Epic epic2 = new Epic(epic1);

        Boolean added = epic1.addSubTask(epic2.getId());

        assertTrue(added);
    }

}