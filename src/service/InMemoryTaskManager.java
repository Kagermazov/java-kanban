package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryTaskManager<T extends Task> implements TaskManager<T> {
    private List<T> tasks;
    private List<T> subtasks;
    private List<T> epics;
    private int nextId;
    private InMemoryHistoryManager historyManager;

    public InMemoryTaskManager() {
        tasks = new ArrayList<>();
        subtasks = new ArrayList<>();
        epics = new ArrayList<>();
        nextId = 1;
        historyManager = (InMemoryHistoryManager) Managers.getDefaultHistory();
    }

    public InMemoryHistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    public int createTask(T task) {
        boolean doesIdExist = false;

        for (Task duty : tasks) {
            if (duty.getId() == nextId) {
                doesIdExist = true;
                break;
            }
        }
        if (!doesIdExist) {
            task.setId(nextId);
            tasks.add(task);
            nextId++;
            return task.getId();
        }
        return -1;
    }

    @Override
    public int createEpic(T epic) {
        boolean doesIdExist = false;

        for (T duty : epics) {
            if (duty.getId() == nextId) {
                doesIdExist = true;
                break;
            }
        }
        if (!doesIdExist) {
            epic.setId(nextId);
            epics.add(epic);
            nextId++;
            return epic.getId();
        }
        return -1;
    }

    @Override
    public int createSubtask(T subtask, Integer epicId) {
        boolean doesIdExist = false;

        for (T duty : subtasks) {
            if (duty.getId() == nextId) {
                doesIdExist = true;
                break;
            }
        }
        if (!doesIdExist) {
            subtask.setId(nextId);
            subtasks.add(subtask);
            nextId++;
            Subtask duty = (Subtask) subtask;
            duty.setEpicId(epicId);

            int subtaskId = subtask.getId();

            Epic epic = (Epic) getEpic(epicId);
            historyManager.getHistory().remove(epic);
            epic.getSubtaskIds().add(subtaskId);
            changeEpicStatus((T) epic);
            return subtaskId;
        }
        return -1;
    }

    @Override
    public List<T> getTasks() {
        return tasks;
    }

    @Override
    public List<T> getSubtasks() {
        return subtasks;
    }

    @Override
    public List<T> getEpics() {
        return epics;
    }

    @Override
    public T getTask(Integer taskId ) {

        for (T task : tasks) {
            if (task.getId() == taskId) {
                if (historyManager.isHistoryFull()) {
                    historyManager.trimHistory();
                    historyManager.add(task);
                    return task;
                }
                historyManager.add(task);
                return task;
            }
        }
        return null;
    }

    @Override
    public T getSubtask(Integer id) {

        for (T subtask : subtasks) {
            if (subtask.getId() == id) {
                if (historyManager.isHistoryFull()) {
                    historyManager.trimHistory();
                    historyManager.add(subtask);
                    return subtask;
                }

                historyManager.add(subtask);
                return subtask;
            }
        }

        return null;
    }

    @Override
    public T getEpic(Integer id) {

        for (T epic : epics) {
            if (epic.getId() == id) {
                if (historyManager.isHistoryFull()) {
                    historyManager.trimHistory();
                    historyManager.add(epic);
                    return epic;
                }

                historyManager.add(epic);
                return epic;
            }
        }

        return null;
    }

    @Override
    public List<T> getEpicSubtasks(Integer epicId) {
        List<T> subtasks = new ArrayList<>();

        for (T epic : epics) {
            if (epic.getId() == epicId) {
                Epic epicTask = (Epic) epic;
                for (Integer subtaskId : epicTask.getSubtaskIds()) {
                    for (T subtask : this.subtasks) {
                        if (subtask.getId() == subtaskId) {
                            subtasks.add(subtask);
                        }
                    }
                }
            }
        }
        return subtasks;
    }

    @Override
    public void updateTask(T task, Integer taskId) {
        task.setId(taskId);

        for (T duty : tasks) {
            if (duty.getId() == taskId) {
                tasks.set(tasks.indexOf(duty), task);
            }
        }
    }

    @Override
    public void updateSubtask(T subtask, Integer subtaskId) {
        subtask.setId(subtaskId);
        Subtask duty = (Subtask) getSubtask(subtaskId);
        historyManager.getHistory().remove(duty);
        duty.setEpicId(duty.getEpicId());
        T epic = getEpic(duty.getEpicId());
        historyManager.getHistory().remove(epic);
        changeEpicStatus(epic);

        for (T goal : subtasks) {
            if (goal.getId() == subtaskId) {
                subtasks.set(subtasks.indexOf(goal), subtask);
            }
        }
    }

    private void changeEpicStatus(T epic) {
        if (subtasks.isEmpty()) {
            epic.setStatus(Status.NEW);
        } else {
            List<T> subtasks = getEpicSubtasks(epic.getId());
            if (subtasks.isEmpty()) {
                epic.setStatus(Status.NEW);
            } else {
                for (T subtask : subtasks) {
                    if (!subtask.getStatus().equals(Status.DONE)) {
                        epic.setStatus(Status.IN_PROGRESS);
                        break;
                    } else {
                        epic.setStatus(Status.DONE);
                    }
                }
            }
        }
    }

    @Override
    public void updateEpic(T epic, Integer id) {

        for (T task : epics) {
            if (task.getId() == id) {
                epics.set(epics.indexOf(task), epic);
            }
        }
    }

    @Override
    public void deleteTask(Integer id) {
        tasks.removeIf(task -> task.getId() == id);
    }

    @Override
    public void deleteSubtask(Integer subtaskId) {
        T subtask = getSubtask(subtaskId);
        historyManager.getHistory().remove(subtask);

        Subtask duty = (Subtask) subtask;
        Epic epic = (Epic) getEpic(duty.getEpicId());
        historyManager.getHistory().remove(epic);

        epic.getSubtaskIds().remove(subtaskId);
        changeEpicStatus((T) epic);
        subtasks.removeIf(task -> task.getId() == subtaskId);
    }

    @Override
    public void deleteEpic(Integer epicId) {

        for (int i = 0; i < subtasks.size(); i++) {
            for (T epicSubtask : getEpicSubtasks(epicId)) {
                subtasks.removeIf(duty -> duty.equals(epicSubtask));
            }
        }

        epics.removeIf(epic -> epic.getId() == epicId);
    }

    @Override
    public void deleteTasks() {
        tasks.clear();
    }

    @Override
    public void deleteSubtasks() {
        subtasks.clear();

        for (T epic : epics) {
            Epic duty = (Epic) epic;
            duty.getSubtaskIds().clear();
            changeEpicStatus(epic);
        }
    }

    @Override
    public void deleteEpics() {
        subtasks.clear();
        epics.clear();
    }
}
