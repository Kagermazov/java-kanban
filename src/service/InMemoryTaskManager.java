package service;

import model.*;

import java.time.Instant;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private final List<Task> tasks;
    private final List<Subtask> subtasks;
    private final List<Epic> epics;
    private int nextId;
    static final HistoryManager historyManager =  Managers.getDefaultHistory();
    private final Set<Task> prioritizedTasks;

    public InMemoryTaskManager() {
        this.tasks = new ArrayList<>();
        this.subtasks = new ArrayList<>();
        this.epics = new ArrayList<>();
        this.nextId = 1;
        this.prioritizedTasks = new TreeSet<>(Comparator
                .comparing(Task::getStartTime, Comparator
                        .nullsLast(Comparator.naturalOrder())).thenComparing(Task::getId));
    }

    @Override
    public int createTask(Task newTask) {
        if (newTask.getStartTime() == null) {
            return getTaskId(newTask);
        }

        checkOverlapping(newTask);
        this.prioritizedTasks.add(newTask);
        return getTaskId(newTask);
    }

    private void checkOverlapping(Task taskToCheck) {
        if (isTaskOverlapping(taskToCheck)) {
          throw new IllegalArgumentException("Task time is wrong");
        }
    }

    private int getTaskId(Task newTask) {
        newTask.setId(this.nextId);
        addTaskToMemory(newTask);
        this.nextId++;
        return newTask.getId();
    }

    @Override
    public int createEpic(Epic newEpic) {
        this.prioritizedTasks.add(newEpic);
        return getTaskId(newEpic);
    }

    @Override
    public int createSubtask(Subtask newSubtask, int epicId) {
        checkOverlapping(newSubtask);
        newSubtask.setId(this.nextId);
        this.nextId++;
        newSubtask.setEpicId(epicId);
        addTaskToMemory(newSubtask);
        this.prioritizedTasks.add(newSubtask);

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
                .toList();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(this.prioritizedTasks);
    }

    @Override
    public void updateTask(Task updatedtask, int taskId) {
        updatedtask.setId(taskId);
        checkOverlapping(updatedtask);

        Task expectedTask = findTask(taskId, this.tasks).orElseThrow();

        this.tasks.remove(expectedTask);
        this.prioritizedTasks.removeIf(task -> task.equals(expectedTask));
        this.tasks.add(updatedtask);
        this.prioritizedTasks.add(updatedtask);
    }

    @Override
    public void updateSubtask(Subtask updatedSubtask, int subtaskId) {
        Subtask expectedSubtask = findTask(subtaskId, this.subtasks).orElseThrow();
        int epicId = expectedSubtask.getEpicId();

        updatedSubtask.setId(expectedSubtask.getId());
        updatedSubtask.setEpicId(epicId);
        this.subtasks.remove(expectedSubtask);
        this.prioritizedTasks.removeIf(task -> task.equals(expectedSubtask));
        this.prioritizedTasks.removeIf(task -> task.getId() == epicId);
        checkOverlapping(updatedSubtask);

        Epic targetEpic = findTask(epicId, this.epics).orElseThrow();

        targetEpic.setStartTime(null);
        targetEpic.setEndTime(null);
        this.prioritizedTasks.add(updatedSubtask);
        this.prioritizedTasks.add(targetEpic);
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
        this.prioritizedTasks.removeIf(task -> task.equals(expectedEpic));
        this.prioritizedTasks.add(updatedEpic);
    }

    @Override
    public void deleteTask(int id) {
        if (!this.prioritizedTasks.removeIf(task -> task.equals(getTask(id)))
                || !this.tasks.removeIf(task -> task.getId() == id)) {
            throw new NoSuchElementException("Task with id " + id + " not found");
        }

        InMemoryTaskManager.historyManager.remove(id);
    }

    @Override
    public void deleteSubtask(int id) {
        if (this.subtasks.stream().noneMatch(subtask -> subtask.getId() == id)) {
            throw new NoSuchElementException(id + " not found");
        }

        this.epics.forEach(epic -> {
            List<Integer> subtasksIds = epic.getSubtaskIds();

            if (subtasksIds.contains(id)) {
                epic.removeIdFromSubtasksIds(id);
                changeEpicStatus(epic);
                changeEpicTimeFrame(epic);
            }
        });

        this.prioritizedTasks.removeIf(task -> task.equals(getSubtask(id)));
        this.subtasks.removeIf(subtask -> subtask.getId() == id);
        InMemoryTaskManager.historyManager.remove(id);
    }

    @Override
    public void deleteEpic(int id) {
        Epic epicToDelete = getEpic(id);
        List<Integer> subtaskIds = epicToDelete.getSubtaskIds();

        subtaskIds.forEach(subtaskId ->
            InMemoryTaskManager.historyManager.getHistory().stream()
                            .mapToInt(Task::getId)
                    .filter(taskId -> taskId == subtaskId)
                    .forEach(InMemoryTaskManager.historyManager::remove));

        InMemoryTaskManager.historyManager.remove(id);
        this.subtasks.removeIf(task -> task.getEpicId() == id);
        this.epics.removeIf(epic -> epic.getId() == id);
        this.prioritizedTasks.removeIf(task -> task.equals(epicToDelete));
    }

    @Override
    public void deleteTasks() {
        this.tasks.forEach(task -> InMemoryTaskManager.historyManager.remove(task.getId()));
        this.tasks.clear();
        this.prioritizedTasks.removeIf(task -> task.getType() == TaskTypes.TASK);
    }

    @Override
    public void deleteSubtasks() {
        clearSubtasks();

        this.epics.forEach(savedEpic -> {
            savedEpic.clearSubtasksIds();
            changeEpicStatus(savedEpic);
        });
        this.prioritizedTasks.removeIf(task -> task.getType() == TaskTypes.SUBTASK);
    }

    @Override
    public void deleteEpics() {
        clearSubtasks();
        this.epics.forEach(epic -> InMemoryTaskManager.historyManager.remove(epic.getId()));
        this.epics.clear();
        this.prioritizedTasks.removeIf(task -> task.getType() == TaskTypes.EPIC);
    }

    void addTaskToMemory(Task taskToSave) {
        switch (taskToSave.getType()) {
            case TASK -> this.tasks.add(taskToSave);
            case EPIC -> this.epics.add((Epic) taskToSave);
            case SUBTASK -> this.subtasks.add((Subtask) taskToSave);
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
        return findTask(id, this.tasks)
                .or(() -> findTask(id, this.epics))
                .or(() -> findTask(id, this.subtasks));
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

    private boolean isTaskOverlapping(Task taskToCheck) {
        Instant taskStartTime = taskToCheck.getStartTime();
        Instant taskEndTime = taskToCheck.getEndTime().orElseThrow();

        if (taskStartTime == null) {
            return false;
        }

        List<Task> sortedTasks = getPrioritizedTasks();

        return sortedTasks.stream()
                .filter(storedTask -> storedTask.getStartTime() != null && storedTask.getId() != taskToCheck.getId())
                .anyMatch(storedTask -> isOverlapping(taskStartTime, taskEndTime, storedTask));
    }

    private static boolean isOverlapping(Instant taskStartTime, Instant taskEndTime, Task storedTask) {
        Instant storedTaskEndTime = storedTask.getEndTime().orElseThrow();
        boolean isTaskEndTimeEqualStoredEndTime = taskEndTime == storedTaskEndTime;
        boolean isTaskStartTimeEqualStoredStartTime = taskStartTime == storedTask.getStartTime();
        boolean isTaskStartTimeBeforeStoredTaskStartAndEndTime = taskStartTime.isBefore(storedTask.getStartTime())
                && taskStartTime.isBefore(storedTaskEndTime);
        boolean isTaskStartTimeAfterStoredTaskStartTimeAndBeforeStoredTaskEndTime
                = taskStartTime.isAfter(storedTask.getStartTime())
                && taskStartTime.isBefore(storedTaskEndTime);
        
        return isTaskEndTimeEqualStoredEndTime
                || isTaskStartTimeEqualStoredStartTime
                || isTaskStartTimeBeforeStoredTaskStartAndEndTime
                || isTaskStartTimeAfterStoredTaskStartTimeAndBeforeStoredTaskEndTime;
    }
}
