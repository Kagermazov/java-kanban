package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {
    Task testTask;

    @BeforeEach
    public void beforeAll() {
        this.testTask = new Task("Name", "Description", Status.NEW, TaskTypes.TASK,
                Duration.ofMinutes(42), Instant.EPOCH);
    }

    @DisplayName("Should task objects with same id be equal when the equals method called")
    @Test
    void shouldTaskObjectsWithSameIdBeEqualWhen_equalsMethodCalled() {
        Task taskForTest = new Task("Name", "Description", Status.NEW, TaskTypes.TASK,
                Duration.ofMinutes(42), Instant.EPOCH);

        taskForTest.setId(this.testTask.getId());
        assertEquals(taskForTest, testTask);
    }

    @DisplayName("Should the hashCode method work correctly")
    @Test
    void should_hashCodeMethodWorkCorrectly() {
        Task taskForTest = new Task("Name", "Description", Status.NEW, TaskTypes.TASK,
                Duration.ofMinutes(42), Instant.EPOCH);

        assertEquals(testTask.hashCode(), taskForTest.hashCode());
    }

    @DisplayName("Should return a Duration object when the getDuration method called")
    @Test
    void shouldReturnDurationObjectWhen_getDurationMethodCalled(){
        Duration actual = this.testTask.getDuration();
        Duration expected = Duration.ofMinutes(42);

        assertEquals(expected, actual);
    }

    @DisplayName("Should return a Instant object when the getStartTime method called")
    @Test
    void shouldReturnInstantObjectWhen_getStartTimeMethodCalled(){
        Instant actual = this.testTask.getStartTime();
        Instant expected = Instant.EPOCH;

        assertEquals(expected, actual);
    }

    @DisplayName("Should return a string when the getName method called")
    @Test
    void shouldReturnStringWhen_getNameMethodCalled() {
        assertEquals("Name", testTask.getName());
    }

    @DisplayName("Should return a string when the getDescription method called")
    @Test
    void shouldReturnStringWhen_getDescriptionMethodCalled() {
        assertEquals("Description", testTask.getDescription());
    }

    @DisplayName("Should return an integer when the getId method called")
    @Test
    void shouldReturnIntegerWhen_getIdMethodCalled() {
        assertEquals(-1, testTask.getId());
    }

    @DisplayName("Should return a status object when the getStatus method called")
    @Test
    void shouldReturnStatusObjectWhen_getStatusMethodCalled() {
        assertEquals(Status.NEW, testTask.getTaskStatus());
    }

    @DisplayName("Should return a type object when the getType method called")
    @Test
    void shouldReturnTypeObjectWhen_getTypeMethodCalled() {
        assertEquals(TaskTypes.TASK, testTask.getType());
    }

    @DisplayName("Should change an id when the setId method called")
    @Test
    void shouldChangeIdWhen_setIdMethodCalled() {
        testTask.setId(1);
        assertEquals(1, testTask.getId());
    }

    @DisplayName("Should change a status when the setStatus method called")
    @Test
    void shouldChangeStatusWhen_setStatusMethodCalled() {
        testTask.setTaskStatus(Status.IN_PROGRESS);
        assertEquals(Status.IN_PROGRESS, testTask.getTaskStatus());
    }

    @DisplayName("Should return an Instant object when the getEndTime method called")
    @Test
    void shouldReturnInstantObjectWhen_getEndTimeMethodCalled() {
        Instant actual = this.testTask.getEndTime().orElseThrow();
        Instant expected = Instant.EPOCH.plus(Duration.ofMinutes(42));

        assertEquals(expected, actual);
    }

    @DisplayName("Should return a string when the toString method called")
    @Test
    void shouldReturnStringWhen_toStringMethodCalled() {
        String expected = "name = Name, description = Description, id = -1, taskStatus = NEW, type = TASK, " +
                "duration = PT42M, startTime = 1970-01-01T00:00:00Z";

        assertEquals(expected, testTask.toString());
    }
}