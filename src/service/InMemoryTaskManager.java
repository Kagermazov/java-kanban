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
        this.tasks = new ArrayList<>();
        this.subtasks = new ArrayList<>();
        this.epics = new ArrayList<>();
        this.nextId = 1;
        this.historyManager = Managers.getDefaultHistory();
    }

    @Override
    public int createTask(Task task) {
/*Здесь и методах createEpic(Epic epic) и createSubtask(Subtask subtask, int epicId) проверялось, занят ли nextId,
но я решил, что, поскольку поле nextId не меняется извне, его меняют только методы этого же класса,
то эта проверка излишне*/
        task.setId(this.nextId);
        this.tasks.add(task);
        this.nextId++;
        return task.getId();
    }

    @Override
    public int createEpic(Epic epic) {
        epic.setId(this.nextId);
        this.epics.add(epic);
        this.nextId++;
        return epic.getId();
    }

    @Override
    public int createSubtask(Subtask subtask, int epicId) {
        subtask.setId(this.nextId);
        this.subtasks.add(subtask);
        this.nextId++;
        subtask.setEpicId(epicId);

        int subtaskId = subtask.getId();
        Epic epic = findTask(epicId, this.epics);

        epic.getSubtaskIds().add(subtaskId);
        changeEpicStatus(epic);
        return subtaskId;
    }

    @Override
    public List<Task> getTasks() {
        return this.tasks;
    }

    @Override
    public List<Subtask> getSubtasks() {
        return this.subtasks;
    }

    @Override
    public List<Epic> getEpics() {
        return this.epics;
    }

    @Override
    public Task getTask(int taskId) {
        return getTaskFromAndSaveToHistory(taskId, this.tasks);
    }

    @Override
    public Subtask getSubtask(int subtaskId) {
        return getTaskFromAndSaveToHistory(subtaskId, this.subtasks);
    }

    @Override
    public Epic getEpic(int epicId) {
        return getTaskFromAndSaveToHistory(epicId, this.epics);
    }

    @Override
    public List<Subtask> getEpicSubtasks(int epicId) {
        Epic epic = findTask(epicId, this.epics);
        List<Subtask> subtasks = new ArrayList<>();

        for (Integer subtaskId : epic.getSubtaskIds()) {
            Subtask subtask = findTask(subtaskId, this.subtasks);
            subtasks.add(subtask);
        }

        return subtasks;
    }

    @Override
    public List<Task> getHistory() {
        return this.historyManager.getHistory();
    }

    @Override
    public void updateTask(Task task, int taskId) {
        Task storedTask = findTask(taskId, this.tasks);

        task.setId(taskId);
        this.tasks.set(this.tasks.indexOf(storedTask), task);
    }

    @Override
    public void updateSubtask(Subtask subtask, int subtaskId) {

        Subtask storedDuty = findTask(subtaskId, this.subtasks);
        int epicId = storedDuty.getEpicId();

        subtask.setId(storedDuty.getId());
        subtask.setEpicId(epicId);
        this.subtasks.set(this.subtasks.indexOf(storedDuty), subtask);

        Epic epic = findTask(epicId, this.epics);
        changeEpicStatus(epic);
    }

    @Override
    public void updateEpic(Epic epic, int epicId) {
        Epic storedEpic = findTask(epicId, this.epics);

        this.epics.set(this.epics.indexOf(storedEpic), epic);
        this.subtasks.removeIf(subtask -> subtask.getEpicId() == epicId);
    }

    @Override
    public void deleteTask(int id) {
        this.tasks.removeIf(task -> task.getId() == id);
        this.historyManager.remove(id);
    }

    @Override
    public void deleteSubtask(int id) {
        for (Epic epic : this.epics) {
            ArrayList<Integer> subtasksIds = epic.getSubtaskIds();
            if (subtasksIds.contains(id)) {
                subtasksIds.remove((Integer) id);
                changeEpicStatus(epic);
            }
        }
        this.subtasks.removeIf(task -> task.getId() == id);
        this.historyManager.remove(id);
    }

    @Override
    public void deleteEpic(int id) {
        List<Integer> subtaskIds = getEpic(id).getSubtaskIds();

        for (Integer subtaskId : subtaskIds) {
            for (Task task : this.historyManager.getHistory()) {
                int taskId = task.getId();

                if (taskId == subtaskId)
                    this.historyManager.remove(taskId);
            }
        }

        this.historyManager.remove(id);
        this.subtasks.removeIf(subtask -> subtask.getEpicId() == id);
        this.epics.removeIf(epic -> epic.getId() == id);
    }

    @Override
    public void deleteTasks() {
        this.tasks.forEach(task -> this.historyManager.remove(task.getId()));
        this.tasks.clear();
    }

    @Override
    public void deleteSubtasks() {
        clearSubtasks();
        this.epics.forEach(epic -> {
            epic.getSubtaskIds().clear();
            changeEpicStatus(epic);
        });
    }

    @Override
    public void deleteEpics() {
        //убрал вызов публичного метода тут. А где можно почитать про этот антипаттерн?
        clearSubtasks();
        this.epics.forEach(epic -> this.historyManager.remove(epic.getId()));
        this.epics.clear();
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

    private void clearSubtasks() {
        this.subtasks.forEach(subtask -> this.historyManager.remove(subtask.getId()));
        this.subtasks.clear();
    }
}
