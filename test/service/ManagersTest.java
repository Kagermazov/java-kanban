package service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ManagersTest {
    @DisplayName("Should return a TaskManger object when the getDefault method called")
    @Test
    void shouldReturnTaskMangerObjectWhen_getDefaultMethodCalled() throws IOException {
        File csv = new File("C:\\Users\\Sergey\\dev\\java-kanban\\src\\history.csv");

        csv.createNewFile();

        TaskManager testTaskManager = FileBackedTaskManager.loadFromFile(csv);
        TaskManager fileBackedTasksManager = Managers.getDefault(csv);

        assertEquals(testTaskManager.getClass(), fileBackedTasksManager.getClass());
        csv.delete();
    }

    @DisplayName("Should return an InMemoryHistoryManager object when the getDefaultHistory method called")
    @Test
    void shouldReturnInMemoryHistoryManagerObjectWhen_getDefaultHistoryMethodCalled() {
        HistoryManager testHistoryManager = new InMemoryHistoryManager();
        HistoryManager inMemoryHistoryManager = Managers.getDefaultHistory();

        assertEquals(testHistoryManager.getClass(), inMemoryHistoryManager.getClass());
    }
}