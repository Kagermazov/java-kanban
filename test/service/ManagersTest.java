package service;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ManagersTest {

    @Test
    public void IsGetDefaultCorrect() {
        TaskManager testTaskManager = new FileBackedTasksManager(new File(
                "/Users/sergei/IdeaProjects/java-kanban/history"));
        TaskManager fileBackedTasksManager = Managers.getDefault(new File(
                "/Users/sergei/IdeaProjects/java-kanban/history"));

        assertEquals(testTaskManager.getClass(), fileBackedTasksManager.getClass());
    }

    @Test
    public void isGetDefaultHistoryCorrect() {
        HistoryManager testHistoryManager = new InMemoryHistoryManager();
        HistoryManager inMemoryHistoryManager = Managers.getDefaultHistory();

        assertEquals(testHistoryManager.getClass(), inMemoryHistoryManager.getClass());
    }
}