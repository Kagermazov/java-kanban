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
        task.setId(nextId);
        tasks.add(task);
        nextId++;
        return task.getId();
    }

    public int createSubtask(Subtask subtask) {
        subtask.setId(nextId);
        subtasks.add(subtask);
        nextId++;
        return subtask.getId();
    }

    public int createEpic(Epic epic) {
        epic.setId(nextId);
        for (Subtask subtask : subtasks) {
            if (subtask.getEpicId() == -1) {
                subtask.setEpicId(epic.getId());
                epic.getSubtaskIds().add(subtask.getId());
            }
        }
        epics.add(epic);
        nextId++;
        return epic.getId();
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

    public void updateSubtask(Subtask duty, int dutyId) {
        duty.setId(dutyId);
        duty.setEpicId(getSubtask(dutyId).getEpicId());
        for (Subtask subtask : subtasks) {
            if (subtask.getId() == dutyId) {
                duty.setEpicId(subtask.getEpicId());
                subtasks.set(subtasks.indexOf(subtask), duty);
                changeEpicStatus(duty);
            }
        }
    }

    private void changeEpicStatus(Subtask subtask) {
        Epic epic = null;

        for (Epic duty : epics) {
            if (duty.getId() == subtask.getEpicId()) {
                epic = duty;
            }
        }

        ArrayList<Subtask> subtasks = getEpicSubtasks(epic.getId());

        for (Subtask duty : subtasks) {
            if (!duty.getStatus().equals("DONE")) {
                epic.setStatus("IN_PROGRESS");
                break;
            } else {
                epic.setStatus("DONE");
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

    public void deleteSubtask(int id) {
        subtasks.removeIf(task -> task.getId() == id);
    }

    public void deleteEpic(int id) {
        epics.removeIf(task -> task.getId() == id);
    }

    public void deleteTasks() {
        tasks.clear();
    }

    public void deleteSubtasks() {
        subtasks.clear();
    }

    public void deleteEpics() {
        epics.clear();
    }
}
