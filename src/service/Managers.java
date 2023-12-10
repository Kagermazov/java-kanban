package service;

import model.Task;

public class Managers<T extends Task> {

    public TaskManager<T> getDefault() {
        return new InMemoryTaskManager<>();
    }

    static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
