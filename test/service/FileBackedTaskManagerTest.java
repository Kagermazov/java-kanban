package service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;


class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    FileBackedTaskManager manager;
    File csv;

    @BeforeEach
    void beforeEach() throws IOException {
        this.csv = new File(
                "C:\\Users\\Sergey\\dev\\java-kanban\\src\\history.csv");

        if (!this.csv.exists()) {
            this.csv.createNewFile();
        }

        this.manager = FileBackedTaskManager.loadFromFile(this.csv);
    }
    
    @AfterEach
    void afterEach() {
        if (this.csv.exists()) {
            this.csv.delete();
        }
    }
    
    @DisplayName("Should return an integer when the createTask method called")
    @Test
    void shouldReturnIntegerWhen_createTaskMethodCalled() {
        super.shouldReturnIntegerWhen_createTaskMethodCalled(this.manager);
    }

    @DisplayName("Should return an integer when the createSubtask method called")
    @Test
    void shouldReturnIntegerWhen_createSubtaskMethodCalled() {
        super.shouldReturnIntegerWhen_createSubtaskMethodCalled(this.manager);
    }

    @DisplayName("Should return an integer when the getEpicId method called")
    @Test
    void shouldReturnIntegerWhen_getEpicIdMethodCalled() {
        super.shouldReturnIntegerWhen_getEpicIdMethodCalled(this.manager);
    }

    @DisplayName("Should return an integer when the CreateEpic method called")
    @Test
    void shouldReturnIntegerWhen_createEpicMethodCalled() {
        super.shouldReturnIntegerWhen_createEpicMethodCalled(this.manager);
    }

    @DisplayName("Should epic status be NEW when all its subtasks are new")
    @Test
    void shouldEpicStatusBeNEWWhenAllItsSubtasksAreNew() {
        super.shouldEpicStatusBeNEWWhenAllItsSubtasksAreNew(this.manager);
    }

    @DisplayName("Should epic status be DONE when all its subtasks are done")
    @Test
    void shouldEpicStatusBeDONEWhenAllItsSubtasksAreDone () {
        super.shouldEpicStatusBeDONEWhenAllItsSubtasksAreDone(this.manager);
    }

    @DisplayName("Should epic status be IN_PROGRESS when one of its subtasks is new and other is done")
    @Test
    void shouldEpicStatusBeIN_PROGRESSWhenOneOfItsSubtasksIsNewAndOtherIsDone() {
        super.shouldEpicStatusBeIN_PROGRESSWhenOneOfItsSubtasksIsNewAndOtherIsDone(this.manager);
    }

    @DisplayName("Should epic status be IN_PROGRESS when its subtasks are in progress")
    @Test
    void shouldEpicStatusBeIN_PROGRESSWhenItsSubtasksAreInProgress() {
        super.shouldEpicStatusBeIN_PROGRESSWhenItsSubtasksAreInProgress(this.manager);
    }

    @DisplayName("Should rewrite a task id when the createTask method called")
    @Test
    void shouldRewriteTaskIdWhenCreateTaskMethodCalled() {
        super.shouldRewriteTaskIdWhenCreateTaskMethodCalled(this.manager);
    }

    @DisplayName("Should return a list when the getTask method called")
    @Test
    void shouldReturnListWhen_getTasksMethodCalled() {
        super.shouldReturnListWhen_getTasksMethodCalled(this.manager);
    }

    @DisplayName("Should return a list when the getSubtask method called")
    @Test
    void shouldReturnListWhen_getSubtasksMethodCalled() {
        super.shouldReturnListWhen_getSubtasksMethodCalled(this.manager);
    }

    @DisplayName("Should return a list when the getEpics method called")
    @Test
    void shouldReturnListWhen_getEpicsMethodCalled() {
        super.shouldReturnListWhen_getEpicsMethodCalled(this.manager);
    }

    @DisplayName("Should return a task when the getTask method called")
    @Test
    void shouldReturnTaskWhen_getTaskMethodCalled() {
        super.shouldReturnTaskWhen_getTaskMethodCalled(this.manager);
    }

    @DisplayName("Should return a subtask when the getSubtask method called")
    @Test
    void shouldReturnSubtaskWhen_getSubtaskMethodCalled() {
        super.shouldReturnSubtaskWhen_getSubtaskMethodCalled(this.manager);
    }

    @DisplayName("Should return an epic when the getEpic method called")
    @Test
    void shouldReturnEpicWhen_getEpicMethodCalled() {
        super.shouldReturnEpicWhen_getEpicMethodCalled(this.manager);
    }

    @DisplayName("Should throw an exception when the getEpicSubtasks method called and the epic subtask list is empty")
    @Test
    void shouldThrowExceptionWhen_getEpicSubtasksMethodCalledAndEpicSubtaskListIsEmpty() {
        super.shouldThrowExceptionWhen_getEpicSubtasksMethodCalledAndEpicSubtaskListIsEmpty(this.manager);
    }

    @DisplayName("Should return a list when the getHistory method called")
    @Test
    void shouldReturnListWhen_getHistoryMethodCalled() {
        super.shouldReturnListWhen_getHistoryMethodCalled(this.manager);
    }

    @DisplayName("Should return a TreeSet when the getPrioritizedTasks method called")
    @Test
    void shouldReturnTreeSetWhen_getPrioritizedTasksMethodCalled() {
        super.shouldReturnTreeSetWhen_getPrioritizedTasksMethodCalled(this.manager);
    }

    @DisplayName("Should scip a task without start time when the getPrioritizedTasks method called")
    @Test
    void shouldSkipTaskWithoutStartTimeWhen_getPrioritizedTasksMethodCalled() {
        super.shouldSkipTaskWithoutStartTimeWhen_getPrioritizedTasksMethodCalled(this.manager);
    }

    @DisplayName("Should a task status be updated when the updateTask method called")
    @Test
    void shouldTaskStatusBeUpdatedWhen_updateTaskMethodCalled() {
        super.shouldTaskStatusBeUpdatedWhen_updateTaskMethodCalled(this.manager);
    }

    @DisplayName("Should a subtask status be updated when the updateSubtask method called")
    @Test
    void shouldSubtaskStatusBeUpdatedWhen_updateSubtaskMethodCalled() {
        super.shouldSubtaskStatusBeUpdatedWhen_updateSubtaskMethodCalled(this.manager);
    }

    @DisplayName("Should a epic status be updated when the updateEpic method called")
    @Test
    void shouldEpicStatusBeUpdatedWhen_updateEpicMethodCalled() {
        super.shouldEpicStatusBeUpdatedWhen_updateEpicMethodCalled(this.manager);
    }

    @DisplayName("Should delete a task when the deleteTask method called")
    @Test
    void shouldDeleteTaskWhen_deleteTaskMethodCalled() {
        super.shouldDeleteTaskWhen_deleteTaskMethodCalled(this.manager);
    }

    @DisplayName("Should delete a subtask when the deleteSubtask method called")
    @Test
    void shouldDeleteSubtaskWhen_deleteSubtaskMethodCalled() {
        super.shouldDeleteSubtaskWhen_deleteSubtaskMethodCalled(this.manager);
    }

    @DisplayName("Should delete an epic when the deleteEpic method called")
    @Test
    void shouldDeleteEpicWhen_deleteEpicMethodCalled() {
        super.shouldDeleteEpicWhen_deleteEpicMethodCalled(this.manager);
    }

    @DisplayName("Should return an empty list after the deleteTasks method called")
    @Test
    void shouldReturnEmptyListAfter_deleteTasksMethodCalled() {
        super.shouldReturnEmptyListAfter_deleteTasksMethodCalled(this.manager);
    }

    @DisplayName("Should return an empty list after the deleteSubtasks method called")
    @Test
    void shouldReturnEmptyListAfter_deleteSubtasksMethodCalled() {
        super.shouldReturnEmptyListAfter_deleteSubtasksMethodCalled(this.manager);
    }

    @DisplayName("Should return an empty list after the deleteEpics method called")
    @Test
    void shouldReturnEmptyListAfter_deleteEpicsMethodCalled() {
        super.shouldReturnEmptyListAfter_deleteEpicsMethodCalled(this.manager);
    }

    @DisplayName("Should throw a FileNotFoundException when the loadFromFile method called if a CSV file doesn`t exist")
    @Test
    void shouldThrowFileNotFoundExceptionWhen_loadFromFileMethodCalledIfCSVFileDoesNotExist() {
            assertThrows(FileNotFoundException.class, () -> FileBackedTaskManager.loadFromFile(null));
    }
}