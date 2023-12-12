package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private List<Task> tasks;
    private List<Subtask> subtasks;
    private List<Epic> epics;
    private int nextId;
    private HistoryManager historyManager;

    public InMemoryTaskManager() {
        tasks = new ArrayList<>();
        subtasks = new ArrayList<>();
        epics = new ArrayList<>();
        nextId = 1;
        historyManager = Managers.getDefaultHistory();
    }

    @Override
    public int createTask(Task task) {
/*Здесь и методах createEpic(Epic epic) и createSubtask(Subtask subtask, int epicId) проверялось, занят ли nextId,
но я решил, что, поскольку поле nextId не меняется извне, его меняют только методы этого же класса,
то эта проверка излишне*/
        task.setId(nextId);
        tasks.add(task);
        nextId++;
        return task.getId();
    }

    @Override
    public int createEpic(Epic epic) {
        epic.setId(nextId);
        epics.add(epic);
        nextId++;
        return epic.getId();
    }

    @Override
    public int createSubtask(Subtask subtask, int epicId) {
        subtask.setId(nextId);
        subtasks.add(subtask);
        nextId++;
        subtask.setEpicId(epicId);

        int subtaskId = subtask.getId();
        Epic epic = findTask(epicId, epics);

        epic.getSubtaskIds().add(subtaskId);
        changeEpicStatus(epic);
        return subtaskId;
    }

    @Override
    public List<Task> getTasks() {
        return tasks;
    }

    @Override
    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    @Override
    public List<Epic> getEpics() {
        return epics;
    }

    @Override
    public Task getTask(int taskId) {
        return getTaskFromAndSaveToHistory(taskId, tasks);
    }

    @Override
    public Subtask getSubtask(int subtaskId) {
        return getTaskFromAndSaveToHistory(subtaskId, subtasks);
    }

    @Override
    public Epic getEpic(int epicId) {
        return getTaskFromAndSaveToHistory(epicId, epics);
    }

    @Override
    public List<Subtask> getEpicSubtasks(int epicId) {
        Epic epic = findTask(epicId, epics);
        List<Subtask> subtasks = new ArrayList<>();

        for (Integer subtaskId : epic.getSubtaskIds()) {
            Subtask subtask = findTask(subtaskId, this.subtasks);
            subtasks.add(subtask);
        }

        return subtasks;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public void updateTask(Task task, int taskId) {
        Task storedTask = findTask(taskId, tasks);

        task.setId(taskId);
        tasks.set(tasks.indexOf(storedTask), task);
    }

    @Override
    public void updateSubtask(Subtask subtask, int subtaskId) {

        Subtask storedDuty = findTask(subtaskId, subtasks);
        int epicId = storedDuty.getEpicId();

        subtask.setId(storedDuty.getId());
        subtask.setEpicId(epicId);
        subtasks.set(subtasks.indexOf(storedDuty), subtask);

        Epic epic = findTask(epicId, epics);
        changeEpicStatus(epic);
    }

    @Override
    public void updateEpic(Epic epic, int epicId) {
        Epic storedEpic = findTask(epicId, epics);

        epics.set(epics.indexOf(storedEpic), epic);
        subtasks.removeIf(subtask -> subtask.getEpicId() == epicId);
    }

    @Override
    public void deleteTask(int id) {
        tasks.removeIf(task -> task.getId() == id);
    }

    @Override
    public void deleteSubtask(int subtaskId) {
        for (Epic epic : epics) {
            ArrayList<Integer> subtasksIds = epic.getSubtaskIds();
            if (subtasksIds.contains(subtaskId)) {
                epic.getSubtaskIds().remove((Integer) subtaskId);
                changeEpicStatus(epic);
            }
        }
        subtasks.removeIf(task -> task.getId() == subtaskId);
    }

    @Override
    public void deleteEpic(int epicId) {
        subtasks.removeIf(subtask -> subtask.getEpicId() == epicId);
        epics.removeIf(epic -> epic.getId() == epicId);
    }

    @Override
    public void deleteTasks() {
        tasks.clear();
    }

    @Override
    public void deleteSubtasks() {
        subtasks.clear();

        for (Epic epic : epics) {
            epic.getSubtaskIds().clear();
            changeEpicStatus(epic);
        }
    }

    @Override
    public void deleteEpics() {
        subtasks.clear();
        epics.clear();
    }

    private void changeEpicStatus(Epic epic) {
        List<Subtask> subtasks = getEpicSubtasks(epic.getId());
        if (subtasks.isEmpty()) {
            epic.setStatus(Status.NEW);
        } else {
            for (Subtask subtask : subtasks) {
                if (!subtask.getStatus().equals(Status.DONE)) {
                    epic.setStatus(Status.IN_PROGRESS);
                    break;
                } else {
                    epic.setStatus(Status.DONE);
                }
            }
        }
    }

    private <T extends Task> T findTask(int id, List<T> tasksList) {
        for (T task : tasksList) {
            if (task.getId() == id) {
                return task;
            }
        }
        return null;
    }

    private <T extends Task> T getTaskFromAndSaveToHistory(int taskId, List<T> tasksList) {
        T task = findTask(taskId, tasksList);

        if (task != null) {
            historyManager.add(task);
        }

        return task;
    }
}
