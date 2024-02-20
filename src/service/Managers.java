package service;

import java.io.File;

public class Managers {

    private Managers() {
    }

    public static TaskManager getDefault(File csv) {
        return new FileBackedTasksManager(csv);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
