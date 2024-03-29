package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {
    Task testTask;

    @BeforeEach
    public void beforeAll() {
        this.testTask = new Task("Name", "Description", Status.NEW, TaskTypes.TASK);
    }

    @Test
    public void IsEqualsCorrect() {
        Task taskForTest = new Task("Name", "Description", Status.NEW, TaskTypes.TASK);

        assertEquals(taskForTest, testTask);
    }

    @Test
    public void isHashCodeCorrect() {
        Task taskForTest = new Task("Name", "Description", Status.NEW, TaskTypes.TASK);

        assertEquals(testTask.hashCode(), taskForTest.hashCode());
    }

    @Test
    public void isGetNameCorrect() {
        assertEquals("Name", testTask.getName());
    }

    @Test
    public void isGetDescriptionCorrect() {
        assertEquals("Description", testTask.getDescription());
    }

    @Test
    public void IsGetIdCorrect() {
        assertEquals(-1, testTask.getId());
    }

    @Test
    public void isGetStatusCorrect() {
        assertEquals(Status.NEW, testTask.getStatus());
    }

    @Test
    public void isGetTypeCorect() {
        assertEquals(TaskTypes.TASK, testTask.getType());
    }

    @Test
    public void IsSetIdCorrect() {
        testTask.setId(1);
        assertEquals(1, testTask.getId());
    }

    @Test
    public void isSetStatusCorrect() {
        testTask.setStatus(Status.IN_PROGRESS);
        assertEquals(Status.IN_PROGRESS, testTask.getStatus());
    }

    @Test
    public void IsToStringCorrect() {
        String expected = "NAME='Name', description='Description', id=-1, status='NEW'";

        assertEquals(expected, testTask.toString());
    }
}