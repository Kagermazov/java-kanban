package service;

import model.*;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

abstract class TaskManagerTest<T extends TaskManager> {

    void shouldReturnIntegerWhen_createTaskMethodCalled(T manager) {
        Task testTask = new Task("", "", Status.NEW, TaskTypes.TASK, Duration.ofMinutes(42),
                Instant.EPOCH);

        int actualId = manager.createTask(testTask);

        assertEquals(1, actualId);
    }

    void shouldReturnIntegerWhen_createSubtaskMethodCalled(T manager) {
        Epic testEpic = new Epic("", "", Status.NEW, TaskTypes.EPIC, null,
                null);
        Subtask testSubtask = new Subtask("",
                "", Status.NEW, TaskTypes.SUBTASK, Duration.ofMinutes(42), Instant.EPOCH);

        manager.createEpic(testEpic);

        int actual = manager.createSubtask(testSubtask, testEpic.getId());

        assertEquals(2, actual);
    }

    void shouldReturnIntegerWhen_getEpicIdMethodCalled(T manager) {
        Epic testEpic = new Epic("", "", Status.NEW, TaskTypes.EPIC, null,
                null);
        Subtask testSubtask = new Subtask("",
                "", Status.NEW, TaskTypes.SUBTASK, Duration.ofMinutes(42), Instant.EPOCH);

        manager.createEpic(testEpic);
        manager.createSubtask(testSubtask, testEpic.getId());

        int actual = testSubtask.getEpicId();

        assertEquals(1, actual);
    }

    void shouldReturnIntegerWhen_createEpicMethodCalled(T manager) {
        Epic testEpic = new Epic("", "", Status.NEW, TaskTypes.TASK, null,
                null);

        int actual = manager.createEpic(testEpic);

        assertEquals(1, actual);
    }

    void shouldEpicStatusBeNEWWhenAllItsSubtasksAreNew(T manager) {
        Epic testEpic = new Epic("", "", Status.NEW, TaskTypes.EPIC, null,
                null);
        Subtask testSubtask = new Subtask("", "", Status.NEW, TaskTypes.SUBTASK, Duration.ofSeconds(1),
                Instant.EPOCH);
        Subtask testSubtask2 = new Subtask("", "", Status.NEW, TaskTypes.SUBTASK, Duration.ofSeconds(1),
                Instant.ofEpochSecond(2));

        manager.createEpic(testEpic);

        int testEpicId = testEpic.getId();

        manager.createSubtask(testSubtask, testEpicId);
        manager.createSubtask(testSubtask2, testEpicId);

        assertEquals(Status.IN_PROGRESS, testEpic.getTaskStatus());
    }

    void shouldEpicStatusBeDONEWhenAllItsSubtasksAreDone (T manager) {
        Epic testEpic = new Epic("", "", Status.NEW, TaskTypes.EPIC, null,
                null);
        Subtask testSubtask = new Subtask("", "", Status.DONE, TaskTypes.SUBTASK, Duration.ofSeconds(1),
                Instant.EPOCH);
        Subtask testSubtask2 = new Subtask("", "", Status.DONE, TaskTypes.SUBTASK, Duration.ofSeconds(1),
                Instant.ofEpochSecond(2));

        manager.createEpic(testEpic);

        int testEpicId = testEpic.getId();

        manager.createSubtask(testSubtask, testEpicId);
        manager.createSubtask(testSubtask2, testEpicId);

        assertEquals(Status.DONE, testEpic.getTaskStatus());
    }

    void shouldEpicStatusBeIN_PROGRESSWhenOneOfItsSubtasksIsNewAndOtherIsDone(T manager) {
        Epic testEpic = new Epic("", "", Status.NEW, TaskTypes.EPIC, null,
                null);
        Subtask testSubtask = new Subtask("", "", Status.NEW, TaskTypes.SUBTASK, Duration.ofSeconds(1),
                Instant.EPOCH);
        Subtask testSubtask2 = new Subtask("", "", Status.DONE, TaskTypes.SUBTASK, Duration.ofSeconds(1),
                Instant.ofEpochSecond(2));

        manager.createEpic(testEpic);

        int testEpicId = testEpic.getId();

        manager.createSubtask(testSubtask, testEpicId);
        manager.createSubtask(testSubtask2, testEpicId);

        assertEquals(Status.IN_PROGRESS, testEpic.getTaskStatus());
    }

    void shouldEpicStatusBeIN_PROGRESSWhenItsSubtasksAreInProgress(T manager) {
        Epic testEpic = new Epic("", "", Status.NEW, TaskTypes.EPIC, null,
                null);
        Subtask testSubtask = new Subtask("", "", Status.IN_PROGRESS, TaskTypes.SUBTASK, Duration.ofSeconds(1),
                Instant.EPOCH);
        Subtask testSubtask2 = new Subtask("", "", Status.IN_PROGRESS, TaskTypes.SUBTASK, Duration.ofSeconds(1),
                Instant.ofEpochSecond(2));

        manager.createEpic(testEpic);

        int testEpicId = testEpic.getId();

        manager.createSubtask(testSubtask, testEpicId);
        manager.createSubtask(testSubtask2, testEpicId);

        assertEquals(Status.IN_PROGRESS, testEpic.getTaskStatus());
    }

    void shouldRewriteTaskIdWhenCreateTaskMethodCalled(T manager) {
        Task testTask = new Task("", "", Status.NEW, TaskTypes.TASK, Duration.ofMinutes(42),
                Instant.EPOCH);

        testTask.setId(42);

        int actual = manager.createTask(testTask);

        assertEquals(1, actual);
    }

    void shouldReturnListWhen_getTasksMethodCalled(T manager) {
        List<Task> expectedList = new ArrayList<>();
        List<Task> actualList = manager.getTasks();

        assertEquals(expectedList, actualList);
    }

    void shouldReturnListWhen_getSubtasksMethodCalled(T manager) {
        List<Subtask> expectedList = new ArrayList<>();
        List<Subtask> actualList = manager.getSubtasks();

        assertEquals(expectedList, actualList);
    }

    void shouldReturnListWhen_getEpicsMethodCalled(T manager) {
        List<Epic> expectedList = new ArrayList<>();
        List<Epic> actualList = manager.getEpics();

        assertEquals(expectedList, actualList);
    }

    void shouldReturnTaskWhen_getTaskMethodCalled(T manager) {
        Task expectedTask = new Task("", "", Status.NEW, TaskTypes.TASK, Duration.ofMinutes(42),
                Instant.EPOCH);

        expectedTask.setId(1);
        manager.createTask(expectedTask);

        Task actualTask = manager.getTask(1);

        assertEquals(expectedTask, actualTask);
    }

    void shouldReturnSubtaskWhen_getSubtaskMethodCalled(T manager) {
        Epic epic = new Epic("", "", Status.NEW, TaskTypes.EPIC, null, null);
        Subtask testSubtask = new Subtask("",
                "", Status.NEW, TaskTypes.SUBTASK, Duration.ofMinutes(42), Instant.EPOCH);

        manager.createEpic(epic);
        manager.createSubtask(testSubtask, epic.getId());

        Task actualSubtask = manager.getSubtask(testSubtask.getId());

        assertEquals(testSubtask, actualSubtask);
    }

    void shouldReturnEpicWhen_getEpicMethodCalled(T manager) {
        Epic expectedEpic = new Epic("", "", Status.NEW, TaskTypes.EPIC, null,
                null);

        expectedEpic.setId(1);
        manager.createEpic(expectedEpic);

        Task actualEpic = manager.getEpic(1);

        assertEquals(expectedEpic, actualEpic);
    }

    void shouldThrowExceptionWhen_getEpicSubtasksMethodCalledAndEpicSubtaskListIsEmpty(T manager) {
        assertThrows(NoSuchElementException.class, () -> manager.getEpicSubtasks(42));
    }

    void shouldReturnListWhen_getHistoryMethodCalled(T manager) {
        List<Task> expected = new ArrayList<>();
        List<Task> actual = manager.getHistory();

        assertEquals(expected, actual);
    }

    void shouldReturnTreeSetWhen_getPrioritizedTasksMethodCalled(T manager) {
        Task testTask = new Task("Task", "", Status.NEW, TaskTypes.TASK, Duration.ofSeconds(1),
                Instant.EPOCH);
        Epic testEpic = new Epic("Epic", "", Status.NEW, TaskTypes.EPIC, Duration.ofMinutes(0),
                null);
        Subtask testSubtask = new Subtask("Subtask", "", Status.NEW, TaskTypes.SUBTASK, Duration.ofSeconds(1),
                Instant.ofEpochSecond(2));
        TreeSet<Task> expected = new TreeSet<>(Comparator.comparing(Task::getStartTime));

        manager.createTask(testTask);
        manager.createEpic(testEpic);
        manager.createSubtask(testSubtask, testEpic.getId());
        expected.add(testTask);
        expected.add(testEpic);
        expected.add(testSubtask);

        TreeSet<Task> actual = manager.getPrioritizedTasks();

        assertEquals(expected, actual);
    }

    void shouldSkipTaskWithoutStartTimeWhen_getPrioritizedTasksMethodCalled(T manager) {
        Task testTask = new Task("Task", "", Status.NEW, TaskTypes.TASK, Duration.ofMinutes(42),
                null);
        Epic testEpic = new Epic("Epic", "", Status.NEW, TaskTypes.EPIC, null,
                null);
        Subtask testSubtask = new Subtask("Subtask", "", Status.NEW, TaskTypes.SUBTASK, Duration.ofMinutes(42),
                Instant.ofEpochSecond(2));
        TreeSet<Task> expected = new TreeSet<>(Comparator.comparing(Task::getStartTime));

        manager.createTask(testTask);
        manager.createEpic(testEpic);
        manager.createSubtask(testSubtask, testEpic.getId());
        expected.add(testEpic);
        expected.add(testSubtask);

        TreeSet<Task> actual = manager.getPrioritizedTasks();

        assertEquals(expected, actual);
    }

    void shouldTaskStatusBeUpdatedWhen_updateTaskMethodCalled(T manager) {
        Task testTask = new Task("", "", Status.NEW, TaskTypes.TASK, Duration.ofMinutes(42),
                Instant.EPOCH);
        Task updatedTask = new Task("", "", Status.IN_PROGRESS, TaskTypes.TASK, Duration.ofMinutes(42),
                Instant.EPOCH);

        manager.createTask(testTask);
        manager.updateTask(updatedTask, 1);

        assertEquals(Status.IN_PROGRESS, manager.getTask(1).getTaskStatus());
    }

    void shouldSubtaskStatusBeUpdatedWhen_updateSubtaskMethodCalled(T manager) {
        Epic testEpic = new Epic("", "", Status.NEW, TaskTypes.EPIC, null,
                null);
        Subtask testSubtask = new Subtask("",
                "", Status.NEW, TaskTypes.SUBTASK, Duration.ofMinutes(42), Instant.EPOCH);
        Subtask updatedSubtask = new Subtask("",
                "", Status.IN_PROGRESS, TaskTypes.SUBTASK, Duration.ofMinutes(42), Instant.EPOCH);

        manager.createEpic(testEpic);
        manager.createSubtask(testSubtask, testEpic.getId());

        int testSubtaskId = testSubtask.getId();

        manager.updateSubtask(updatedSubtask, testSubtaskId);

        assertEquals(Status.IN_PROGRESS, manager.getSubtask(testSubtaskId).getTaskStatus());
    }

    void shouldEpicStatusBeUpdatedWhen_updateEpicMethodCalled(T manager) {
        Epic testEpic = new Epic("", "", Status.NEW, TaskTypes.EPIC, null,
                null);
        Epic updatedEpic = new Epic("", "", Status.IN_PROGRESS, TaskTypes.EPIC, null,
                null);

        manager.createEpic(testEpic);
        manager.updateEpic(updatedEpic, testEpic.getId());

        assertEquals(Status.IN_PROGRESS, manager.getEpic(updatedEpic.getId()).getTaskStatus());
    }

    void shouldDeleteTaskWhen_deleteTaskMethodCalled(T manager) {
        Task testTask = new Task("", "", Status.NEW, TaskTypes.TASK, Duration.ofMinutes(42),
                Instant.EPOCH);

        manager.createTask(testTask);
        manager.deleteTask(testTask.getId());

        assertThrows(NoSuchElementException.class, () -> manager.getTask(1));
    }

    void shouldDeleteSubtaskWhen_deleteSubtaskMethodCalled(T manager) {
        Task testSubtask = new Subtask("", "", Status.NEW, TaskTypes.SUBTASK, Duration.ofMinutes(42),
                Instant.EPOCH);

        manager.createTask(testSubtask);
        manager.deleteSubtask(testSubtask.getId());

        assertThrows(NoSuchElementException.class, () -> manager.getSubtask(1));
    }

    void shouldDeleteEpicWhen_deleteEpicMethodCalled(T manager) {
        Epic testEpic = new Epic("", "", Status.NEW, TaskTypes.EPIC, null,
                null);

        manager.createEpic(testEpic);
        manager.deleteEpic(testEpic.getId());

        assertThrows(NoSuchElementException.class, () -> manager.getTask(1));
    }

    void shouldReturnEmptyListAfter_deleteTasksMethodCalled(T manager) {
        List<Task> expected = new ArrayList<>();
        Task testTask = new Task("", "", Status.NEW, TaskTypes.TASK, Duration.ofMinutes(42),
                Instant.EPOCH);

        manager.createTask(testTask);
        manager.deleteTasks();

        assertEquals(expected, manager.getTasks());
    }
    void shouldReturnEmptyListAfter_deleteSubtasksMethodCalled(T manager) {
        List<Task> expected = new ArrayList<>();
        Epic testEpic = new Epic("", "", Status.NEW, TaskTypes.EPIC, null,
                null);
        Subtask testSubtask = new Subtask("",
                "", Status.NEW, TaskTypes.SUBTASK, Duration.ofMinutes(42), Instant.ofEpochSecond(1));

        manager.createEpic(testEpic);
        manager.createSubtask(testSubtask, testEpic.getId());
        manager.deleteSubtasks();

        assertEquals(expected, manager.getSubtasks());
    }

    void shouldReturnEmptyListAfter_deleteEpicsMethodCalled(T manager) {
        List<Task> expected = new ArrayList<>();
        Epic testEpic = new Epic("", "", Status.NEW, TaskTypes.EPIC, null,
                null);

        manager.createEpic(testEpic);
        manager.deleteEpics();

        assertEquals(expected, manager.getEpics());
    }

    void shouldThrowRuntimeExceptionIfTasksDoNotOverlap(T manager) {
        Task testTask = new Task("", "", Status.NEW, TaskTypes.TASK, Duration.ofSeconds(1),
                Instant.EPOCH);
        Task testTask2 = new Task("", "", Status.NEW, TaskTypes.TASK, Duration.ofSeconds(1),
                Instant.EPOCH);

        manager.createTask(testTask);
        assertThrows(RuntimeException.class, () -> manager.createTask(testTask2));
    }
}