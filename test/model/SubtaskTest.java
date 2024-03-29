package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SubtaskTest {
    static Subtask testSubtask;

    @BeforeEach
    public void beforeEach() {
        testSubtask = new Subtask("", "", Status.NEW, TaskTypes.SUBTASK);
    }

    @Test
    public void isGetEpicIdCorrect() {
        assertEquals(-1, testSubtask.getEpicId());
    }

    @Test
    public void isSetEpicIdCorrect() {
        testSubtask.setEpicId(1);
        assertEquals(1, testSubtask.getEpicId());
    }

    @Test
    void testToString() {
        String expected = "Subtask{epicId=-1, NAME='', description='', id=-1, status='NEW'}";

        assertEquals(expected, testSubtask.toString());
    }
}