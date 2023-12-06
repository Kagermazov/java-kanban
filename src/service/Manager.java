package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.ArrayList;

public class Manager {
    private ArrayList<Task> tasks;
    private ArrayList<Subtask> subtasks;
    private ArrayList<Epic> epics;
    private int nextId;

    public Manager() {
        tasks = new ArrayList<>();
        subtasks = new ArrayList<>();
        epics = new ArrayList<>();
        nextId = 1;
    }

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
            epic.getSubtaskIds().add(subtaskId);
            changeEpicStatus(epic);
            return subtaskId;
        }
        return -1;
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    public ArrayList<Epic> getEpics() {
        return epics;
    }

    public Task getTask(int taskId) {
        for (Task task : tasks) {
            if (task.getId() == taskId) {
                return task;
            }
        }
        return null;
    }

    public Subtask getSubtask(int id) {
        for (Subtask subtask : subtasks) {
            if (subtask.getId() == id) {
                return subtask;
            }
        }
        return null;
    }

    public Epic getEpic(int id) {
        for (Epic epic : epics) {
            if (epic.getId() == id) {
                return epic;
            }
        }
        return null;
    }

    public ArrayList<Subtask> getEpicSubtasks(int epicId) {
        ArrayList<Subtask> subtasks = new ArrayList<>();

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

    public void updateTask(Task task, int taskId) {
        task.setId(taskId);
        for (Task duty : tasks) {
            if (duty.getId() == taskId) {
                tasks.set(tasks.indexOf(duty), task);
            }
        }
    }

    public void updateSubtask(Subtask subtask, int subtaskId) {
        subtask.setId(subtaskId);
        subtask.setEpicId(getSubtask(subtaskId).getEpicId());
        for (Subtask duty : subtasks) {
            if (duty.getId() == subtaskId) {
                subtask.setEpicId(duty.getEpicId());
                subtasks.set(subtasks.indexOf(duty), subtask);
                changeEpicStatus(getEpic(duty.getEpicId()));
            }
        }
    }

    private void changeEpicStatus(Epic epic) {
        if (subtasks.isEmpty()) {
            epic.setStatus("NEW");
        } else {
            ArrayList<Subtask> subtasks = getEpicSubtasks(epic.getId());
            if (subtasks.isEmpty()) {
                epic.setStatus("NEW");
            } else {
                for (Subtask subtask : subtasks) {
                    if (!subtask.getStatus().equals("DONE")) {
                        epic.setStatus("IN_PROGRESS");
                        break;
                    } else {
                        epic.setStatus("DONE");
                    }
                }
            }
        }
    }

    public void updateEpic(Epic epic, int id) {
        for (Epic task : epics) {
            if (task.getId() == id) {
                epics.set(epics.indexOf(task), epic);
            }
        }
    }

    public void deleteTask(int id) {
        tasks.removeIf(task -> task.getId() == id);
    }

    public void deleteSubtask(int subtaskId) {
        Epic epic = getEpic(getSubtask(subtaskId).getEpicId());

        epic.getSubtaskIds().remove((Integer) subtaskId);
        changeEpicStatus(epic);
        subtasks.removeIf(task -> task.getId() == subtaskId);
    }

    public void deleteEpic(int epicId) {
        subtasks.removeIf(subtask -> subtask.getEpicId() == epicId);
        epics.removeIf(task -> task.getId() == epicId);
    }

    public void deleteTasks() {
        tasks.clear();
    }

    public void deleteSubtasks() {
        subtasks.clear();
        for (Epic epic : epics) {
            epic.getSubtaskIds().clear();
            changeEpicStatus(epic);
        }
    }

    public void deleteEpics() {
        subtasks.clear();
        epics.clear();
    }
}
