package service;

import java.io.File;
import java.io.IOException;

public class Managers {

    private Managers() {
    }

    public static TaskManager getDefault(File csv) throws IOException {
        return FileBackedTaskManager.loadFromFile(csv);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
