package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;

public interface TaskManager {

    int createTask(Task task);

    int createEpic(Epic epic);

    int createSubtask(Subtask subtask, int epicId);

    List<Task> getTasks();

    List<Subtask> getSubtasks();

    List<Epic> getEpics();

    Task getTask(int taskId);

    Subtask getSubtask(int id);

    Epic getEpic(int id);

    List<Subtask> getEpicSubtasks(int epicId);

    List<Task> getHistory();

    void updateTask(Task task, int taskId);

    void updateSubtask(Subtask subtask, int subtaskId);

    void updateEpic(Epic epic, int id);

    void deleteTask(int id);

    void deleteSubtask(int subtaskId);

    void deleteEpic(int epicId);

    void deleteTasks();

    void deleteSubtasks();

    void deleteEpics();
}
