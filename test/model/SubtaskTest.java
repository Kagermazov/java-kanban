package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SubtaskTest {
    Subtask testSubtask;

    @BeforeEach
    public void beforeEach() {
        testSubtask = new Subtask("", "", Status.NEW, TaskTypes.SUBTASK, Duration.ofMinutes(42),
                Instant.EPOCH);
    }

    @DisplayName("Should throw an IllegalArgumentException when the setEpicId method tries add a subtasks id")
    @Test
    void shouldThrowIllegalArgumentExceptionWhen_setEpicIdMethodTriesAddSubtasksId() {
        int id = this.testSubtask.getId();

        assertThrows(IllegalArgumentException.class, () -> this.testSubtask.setEpicId(id));
    }

    @DisplayName("Should subtask objects with same id be equal when the equals method called")
    @Test
    void shouldSubtaskObjectsWithSameIdBeEqualWhen_equalsMethodCalled() {
        Subtask subtaskToCompare = new Subtask("",
                "", Status.NEW, TaskTypes.SUBTASK, Duration.ofMinutes(42), Instant.EPOCH);

        subtaskToCompare.setId(this.testSubtask.getId());
        assertEquals(subtaskToCompare, this.testSubtask);
    }

    @DisplayName("Should return a negative number when getEpicId method called")
    @Test
    void shouldReturnNegativeNumberWhen_getEpicIdMethodCalled() {
        assertEquals(-1, testSubtask.getEpicId());
    }

    @DisplayName("Should set id when setEpicId method called")
    @Test
    void shouldSetIdWhen_setEpicIdMethodCalled() {
        testSubtask.setEpicId(1);
        assertEquals(1, testSubtask.getEpicId());
    }

    @DisplayName("Should return a string when toString method called")
    @Test
    void shouldReturnStringWhen_toStringMethodCalled() {
        String expected = "Subtask{epicId=-1, NAME='', description='', id=-1, status='NEW'}";

        assertEquals(expected, testSubtask.toString());
    }
}