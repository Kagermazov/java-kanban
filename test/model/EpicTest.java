package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class EpicTest {
    Epic testEpic;

    @BeforeEach
    public void beforeEach() {
        this.testEpic = new Epic("", "", Status.NEW, TaskTypes.EPIC, Duration.ofMinutes(42),
                Instant.EPOCH);
    }

    @DisplayName("Should epic objects with same id be equal when the equals method called")
    @Test
    void shouldEpicObjectsWithSameIdBeEqualWhen_equalsMethodCalled() {
        Task epic = new Epic("", "", Status.NEW, TaskTypes.EPIC, Duration.ofMinutes(42), Instant.EPOCH);

        epic.setId(this.testEpic.getId());
        assertEquals(epic, this.testEpic);
    }

    @DisplayName("Should throw a IllegalArgumentException when an epics id added to an epics field")
    @Test
    void shouldTrowIllegalArgumentExceptionWhenEpicsIdAddedToEpicsField(){
        int expected = this.testEpic.getId();

        assertThrows(IllegalArgumentException.class, () -> this.testEpic.addIdToSubtaskIds(expected));
    }

    @DisplayName("Should return an empty subtasks id list when the getSubtaskIds method called")
    @Test
    void shouldSubtasksIdListBeEmptyWhen_getSubtaskIdsMethodCalled() {
        List<Integer> testList = new ArrayList<>();
        List<Integer> subtaskIds = this.testEpic.getSubtaskIds();

        assertEquals(subtaskIds, testList);
    }

    @DisplayName("Should subtask id list be not empty when it has one element")
    @Test
    void shouldSubtaskIdsListBeNotEmptyWhenItHasOneElement() {
        List<Integer> testList = List.of(42);
        List<Integer> subtaskIds = this.testEpic.getSubtaskIds();

        subtaskIds.add(42);
        assertEquals(subtaskIds, testList);
    }

    @DisplayName("Should remove a subtask id from the SubtaskIds when the removeIdFromSubtasksIds called")
    @Test
    void shouldRemoveSubtaskIdFromSubtaskIdsWhen_removeIdFromSubtasksIdsCalled() {
        this.testEpic.addIdToSubtaskIds(42);
        this.testEpic.removeIdFromSubtasksIds(42);
        assertFalse(this.testEpic.getSubtaskIds().contains(42));
    }

    @DisplayName("Should clear the SubtasksIds list when the clearSubtasksIds called")
    @Test
    void shouldClearSubtasksIdsListWhen_clearSubtasksIdsCalled() {
        this.testEpic.addIdToSubtaskIds(42);
        this.testEpic.clearSubtasksIds();
        assertTrue(this.testEpic.getSubtaskIds().isEmpty());
    }

    @DisplayName("Should return correct string when toString method called")
    @Test
    void shouldReturnCorrectStringWhen_toStringMethodCalled() {
        String expected = "subtaskIds = [], name = , description = , id = -1, taskStatus = NEW, type = EPIC, " +
                "duration = PT42M, startTime = 1970-01-01T00:00:00Z";

        assertEquals(expected, this.testEpic.toString());
    }
}