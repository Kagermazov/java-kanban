package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    private final List<Task> tasks;
    private final List<Subtask> subtasks;
    private final List<Epic> epics;
    private int nextId;
    static final HistoryManager historyManager = Managers.getDefaultHistory();

    public InMemoryTaskManager() {
        this.tasks = new ArrayList<>();
        this.subtasks = new ArrayList<>();
        this.epics = new ArrayList<>();
        this.nextId = 1;
    }

    @Override
    public int createTask(Task newTask) {
        if (newTask.getStartTime() == null) {
            return getTaskId(newTask);
        }

        if (isTaskOverlapping(newTask.getStartTime(), newTask.getEndTime().orElseThrow())) {
          throw new RuntimeException("Task time is wrong");
        }

        return getTaskId(newTask);
    }

    private int getTaskId(Task newTask) {
        newTask.setId(this.nextId);
        addTaskToMemory(newTask);
        this.nextId++;
        return newTask.getId();
    }

    @Override
    public int createEpic(Epic newEpic) {
        return getTaskId(newEpic);
    }

    @Override
    public int createSubtask(Subtask newSubtask, int epicId) {
        if (isTaskOverlapping(newSubtask.getStartTime(), newSubtask.getEndTime().orElseThrow())) {
            throw new RuntimeException("Task time is wrong");
        }
        newSubtask.setId(this.nextId);
        addTaskToMemory(newSubtask);
        this.nextId++;
        newSubtask.setEpicId(epicId);

        int subtaskId = newSubtask.getId();
        Epic subtaskEpic = findTask(epicId, this.epics).orElseThrow();

        subtaskEpic.addIdToSubtaskIds(subtaskId);
        changeEpicStatus(subtaskEpic);
        changeEpicTimeFrame(subtaskEpic);
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
        Epic targetEpic = findTask(epicId, this.epics).orElseThrow();

        return targetEpic.getSubtaskIds()
                .stream()
                .map(subtaskId -> findTask(subtaskId, this.subtasks).orElseThrow())
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        TreeSet<Task> sortedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

        this.tasks.stream()
                .filter(task -> task.getStartTime() != null)
                .forEach(sortedTasks::add);
        this.subtasks.stream()
                .filter(subtask -> subtask.getStartTime() != null)
                .forEach(sortedTasks::add);
        this.epics.stream()
                .filter(epic -> epic.getStartTime() != null)
                .forEach(sortedTasks::add);

        return sortedTasks;
    }

    @Override
    public void updateTask(Task updatedtask, int taskId) {
        Task expectedTask = findTask(taskId, this.tasks).orElseThrow();

        updatedtask.setId(taskId);
        this.tasks.remove(expectedTask);

        if (isTaskOverlapping(updatedtask.getStartTime(), updatedtask.getEndTime().orElseThrow())) {
            throw new RuntimeException("Task time is wrong");
        }

        this.tasks.add(updatedtask);
    }

    @Override
    public void updateSubtask(Subtask updatedSubtask, int subtaskId) {
        Subtask expectedSubtask = findTask(subtaskId, this.subtasks).orElseThrow();
        int epicId = expectedSubtask.getEpicId();

        updatedSubtask.setId(expectedSubtask.getId());
        updatedSubtask.setEpicId(epicId);
        this.subtasks.remove(expectedSubtask);

        Epic targetEpic = findTask(epicId, this.epics).orElseThrow();

        targetEpic.setStartTime(null);
        targetEpic.setEndTime(null);

        if (isTaskOverlapping(updatedSubtask.getStartTime(), updatedSubtask.getEndTime().orElseThrow())) {
            throw new RuntimeException("Task time is wrong");
        }

        this.subtasks.add(updatedSubtask);
        changeEpicStatus(targetEpic);
        changeEpicTimeFrame(targetEpic);
    }

    @Override
    public void updateEpic(Epic updatedEpic, int epicId) {
        Task expectedEpic = findTask(epicId, this.epics).orElseThrow();

        updatedEpic.setId(epicId);
        this.epics.set(this.epics.indexOf(expectedEpic), updatedEpic);
        this.subtasks.removeIf(task -> task.getEpicId() == epicId);
        changeEpicTimeFrame(updatedEpic);
    }

    @Override
    public void deleteTask(int id) {
        this.tasks.removeIf(task -> task.getId() == id);
        InMemoryTaskManager.historyManager.remove(id);
    }

    @Override
    public void deleteSubtask(int id) {
        this.epics.forEach(epic -> {
            List<Integer> subtasksIds = epic.getSubtaskIds();

            if (subtasksIds.contains(id)) {
                epic.removeIdFromSubtasksIds(id);
                changeEpicStatus(epic);
                changeEpicTimeFrame(epic);
            }
        });

        this.subtasks.removeIf(subtask -> subtask.getId() == id);
        InMemoryTaskManager.historyManager.remove(id);
    }

    @Override
    public void deleteEpic(int id) {
        List<Integer> subtaskIds = getEpic(id).getSubtaskIds();

        subtaskIds.forEach(subtaskId ->
            InMemoryTaskManager.historyManager.getHistory().stream().
                    mapToInt(Task::getId)
                    .filter(taskId -> taskId == subtaskId)
                    .forEach(InMemoryTaskManager.historyManager::remove));

        InMemoryTaskManager.historyManager.remove(id);

        this.subtasks.removeIf(task -> task.getEpicId() == id);

        this.epics.removeIf(epic -> epic.getId() == id);
    }

    @Override
    public void deleteTasks() {
        this.tasks.forEach(task -> InMemoryTaskManager.historyManager.remove(task.getId()));
        this.tasks.clear();
    }

    @Override
    public void deleteSubtasks() {
        clearSubtasks();

        this.epics.forEach(savedEpic -> {
            savedEpic.clearSubtasksIds();
            changeEpicStatus(savedEpic);
        });
    }

    @Override
    public void deleteEpics() {
        clearSubtasks();
        this.epics.forEach(epic -> InMemoryTaskManager.historyManager.remove(epic.getId()));
        this.epics.clear();
    }

    void addTaskToMemory(Task taskToSave) {
        switch (taskToSave.getType()) {
            case TASK:
                this.tasks.add(taskToSave);
                break;
            case EPIC:
                this.epics.add((Epic) taskToSave);
                break;
            case SUBTASK:
                this.subtasks.add((Subtask) taskToSave);
                break;
        }
    }

    private void changeEpicStatus(Epic epicToChange) {
        List<Subtask> epicSubtasks = getEpicSubtasks(epicToChange.getId());

        if (epicSubtasks.isEmpty()) {
            epicToChange.setTaskStatus(Status.NEW);
        } else {
            for (Subtask subtask : epicSubtasks) {
                if (!subtask.getTaskStatus().equals(Status.DONE)) {
                    epicToChange.setTaskStatus(Status.IN_PROGRESS);
                    break;
                } else {
                    epicToChange.setTaskStatus(Status.DONE);
                }
            }
        }
    }

    private <T extends Task, V extends T> Optional<T> findTask(int id, List<V> tasksSet) {

        for (T task : tasksSet) {
            if (task.getId() == id) {
                return Optional.of(task);
            }
        }

        return Optional.empty();
    }

    Optional<Task> getById(int id) {
        return findTask(id, this.tasks).
                or(() -> findTask(id, this.epics)).
                or(() -> findTask(id, this.subtasks));
    }

    private <T extends Task> T getTaskFromAndSaveToHistory(int taskId, List<T> tasksList) {
        T task = findTask(taskId, tasksList).orElseThrow();

        InMemoryTaskManager.historyManager.add(task);

        return task;
    }

    private void clearSubtasks() {
        this.subtasks.forEach(subtask -> InMemoryTaskManager.historyManager.remove(subtask.getId()));
        this.subtasks.clear();
    }

    private void changeEpicTimeFrame(Epic epicToChange) {
        Optional<Instant> startTime = this.subtasks.stream()
                .filter(subtask -> subtask.getEpicId() == epicToChange.getId())
                .map(Task::getStartTime)
                .min(Instant::compareTo);

        Optional<Instant> endTime = this.subtasks.stream()
                .filter(subtask -> subtask.getEpicId() == epicToChange.getId())
                .map(Task::getEndTime)
                .map(Optional::orElseThrow)
                .min(Instant::compareTo);

        if (startTime.isEmpty() || endTime.isEmpty()) {
            return;
        }

        epicToChange.setStartTime(startTime.get());
        epicToChange.setEndTime(endTime.get());
    }

    private boolean isTaskOverlapping(Instant taskStartTime, Instant taskEndTime) {
        if (taskStartTime == null) {
            return false;
        }

        TreeSet<Task> sortedTasks = getPrioritizedTasks();

        return sortedTasks.stream()
                .anyMatch(storedTask -> isOverlapping(taskStartTime, taskEndTime, storedTask));
    }

    private static boolean isOverlapping(Instant taskStartTime, Instant taskEndTime, Task storedTask) {
        Instant storedTaskEndTime = storedTask.getEndTime().orElseThrow();

        return taskEndTime == storedTaskEndTime
                || taskStartTime == storedTask.getStartTime()
                || (taskStartTime.isBefore(storedTask.getStartTime())
                && taskStartTime.isBefore(storedTaskEndTime))
                || taskStartTime.isAfter(storedTask.getStartTime())
                && taskStartTime.isBefore(storedTaskEndTime);
    }
}
