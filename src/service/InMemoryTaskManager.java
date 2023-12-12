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
    public int createEpic(Epic epic) {
        boolean doesIdExist = false;

        for (Epic duty : epics) {
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
    public int createSubtask(Subtask subtask, int epicId) {
        boolean doesIdExist = false;

        for (Subtask duty : subtasks) {
            if (duty.getId() == nextId) {
                doesIdExist = true;
                break;
            }
        }
        if (!doesIdExist) {
            subtask.setId(nextId);
            subtasks.add(subtask);
            nextId++;
            subtask.setEpicId(epicId);

            int subtaskId = subtask.getId();
            Epic epic = getEpic(epicId);

            historyManager.getHistory().remove(epic);
            epic.getSubtaskIds().add(subtaskId);
            changeEpicStatus(epic);
            return subtaskId;
        }
        return -1;
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
    public Task getTask(int taskId ) {

        for (Task task : tasks) {
            if (task.getId() == taskId) {
                historyManager.add(task);
                return task;
            }
        }

        return null;
    }

    @Override
    public Subtask getSubtask(int id) {

        for (Subtask subtask : subtasks) {
            if (subtask.getId() == id) {
                historyManager.add(subtask);
                    return subtask;
                }
            }
        return null;
    }

    @Override
    public Epic getEpic(int id) {

        for (Epic epic : epics) {
            if (epic.getId() == id) {
                historyManager.add(epic);
                return epic;
            }
        }

        return null;
    }

    @Override
    public List<Subtask> getEpicSubtasks(int epicId) {
        List<Subtask> subtasks = new ArrayList<>();

        for (Epic epic : epics) {
            if (epic.getId() == epicId) {
                for (Integer subtaskId : epic.getSubtaskIds()) {
                    for (Subtask subtask : this.subtasks) {
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
    public List<Task> getHistory(){
        return historyManager.getHistory();
    }

    @Override
    public void updateTask(Task task, int taskId) {
        task.setId(taskId);

        for (Task duty : tasks) {
            if (duty.getId() == taskId) {
                tasks.set(tasks.indexOf(duty), task);
            }
        }
    }

    @Override
    public void updateSubtask(Subtask subtask, int subtaskId) {

        for (Subtask duty : subtasks) {
            if (duty.getId() == subtaskId) {
                subtask.setId(subtaskId);
                subtask.setEpicId(duty.getEpicId());
                subtasks.set(subtasks.indexOf(duty), subtask);
            }
        }

        for (Epic epic : epics) {
            if (epic.getSubtaskIds().contains(subtaskId)) {
                changeEpicStatus(epic);
            }
        }
    }

    @Override
    public void updateEpic(Epic epic, int epicId) {

        for (Epic task : epics) {
            if (task.getId() == epicId) {
                epics.set(epics.indexOf(task), epic);
            }
        }

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
                epic.getSubtaskIds().remove(subtaskId);
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
        if (subtasks.isEmpty()) {
            epic.setStatus(Status.NEW);
        } else {
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
    }
}
