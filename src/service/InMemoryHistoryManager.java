package service;

import model.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final List<Task> history = new LinkedList<>();
    private final static int MAX_HISTORY_SIZE = 9;

    @Override
    public List<Task> getHistory() {
        return history;
    }

    @Override
    public void add(Task task) {

        if (task == null) {
            return;
        }

        if (isHistoryFull()) {
            trimHistory();
            history.add(task);
            return;
        }

        history.add(task);
    }

    private boolean isHistoryFull() {
        return history.size() > MAX_HISTORY_SIZE;
    }

    private void trimHistory() {
        history.remove(0);
    }
}
