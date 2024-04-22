package service;

import model.Status;
import model.Task;
import model.TaskTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class InMemoryHistoryManagerTest {
    public static HistoryManager manager;

    @BeforeEach
    public void beforeAll() {
        manager = new InMemoryHistoryManager();
    }

    @DisplayName("Should return an empty list when the getHistory method called")
    @Test
    void shouldReturnEmptyListWhen_getHistoryMethodCalled() {
        List<Task> testList = new ArrayList<>();

        assertEquals(manager.getHistory(), testList);
    }

    @DisplayName("Should return a not empty list after a task was added")
    @Test
    void shouldReturnNotEmptyListAfterTaskWasAdded() {
        Task testTask = new Task("", "", Status.NEW, TaskTypes.TASK, Duration.ofMinutes(42),
                Instant.EPOCH);

        manager.add(testTask);

        assertFalse(manager.getHistory().isEmpty());
    }

    @DisplayName("Should delete duplicates")
    @Test
    void shouldDeleteDuplicates(){
        Task testTask = new Task("", "", Status.NEW, TaskTypes.TASK, Duration.ofMinutes(42),
                Instant.EPOCH);

        manager.add(testTask);
        manager.add(testTask);
        assertEquals(1, manager.getHistory().size());
    }

    @DisplayName("Should the history be empty after the remove method called and the history size was one")
    @Test
    void shouldHistoryBeEmptyAfter_removeMethodCalledAndHistorySizeWasOne() {
        List<Task> testList = new ArrayList<>();
        Task testTask = new Task("", "", Status.NEW, TaskTypes.TASK, Duration.ofMinutes(42),
                Instant.EPOCH);

        manager.add(testTask);
        manager.remove(testTask.getId());

        assertEquals(manager.getHistory(), testList);
    }

    @DisplayName("Should delete a first element")
    @Test
    void shouldDeleteFirstElement() {
        Task testTask = new Task("", "", Status.NEW, TaskTypes.TASK, Duration.ofMinutes(42),
                Instant.EPOCH);
        Task testTask2 = new Task("", "", Status.NEW, TaskTypes.TASK, Duration.ofMinutes(42),
                Instant.EPOCH);
        Task testTask3 = new Task("", "", Status.NEW, TaskTypes.TASK, Duration.ofMinutes(42),
                Instant.EPOCH);

        testTask2.setId(1);
        testTask3.setId(2);
        manager.add(testTask);
        manager.add(testTask2);
        manager.add(testTask3);
        manager.remove(testTask.getId());

        assertEquals(1, manager.getHistory().getFirst().getId());
    }

    @DisplayName("Should delete a middle element")
    @Test
    void shouldDeleteMiddleElement() {
        Task testTask = new Task("", "", Status.NEW, TaskTypes.TASK, Duration.ofMinutes(42),
                Instant.EPOCH);
        Task testTask2 = new Task("", "", Status.NEW, TaskTypes.TASK, Duration.ofMinutes(42),
                Instant.EPOCH);
        Task testTask3 = new Task("", "", Status.NEW, TaskTypes.TASK, Duration.ofMinutes(42),
                Instant.EPOCH);

        testTask2.setId(1);
        testTask3.setId(2);
        manager.add(testTask);
        manager.add(testTask2);
        manager.add(testTask3);
        manager.remove(1);

        assertEquals(-1, manager.getHistory().getFirst().getId());
    }

    @DisplayName("Should delete a last element")
    @Test
    void shouldDeleteLastElement() {
        Task testTask = new Task("", "", Status.NEW, TaskTypes.TASK, Duration.ofMinutes(42),
                Instant.EPOCH);
        Task testTask2 = new Task("", "", Status.NEW, TaskTypes.TASK, Duration.ofMinutes(42),
                Instant.EPOCH);
        Task testTask3 = new Task("", "", Status.NEW, TaskTypes.TASK, Duration.ofMinutes(42),
                Instant.EPOCH);

        testTask2.setId(1);
        testTask3.setId(2);
        manager.add(testTask);
        manager.add(testTask2);
        manager.add(testTask3);
        manager.remove(2);

        assertEquals(1, manager.getHistory().getLast().getId());
    }
}