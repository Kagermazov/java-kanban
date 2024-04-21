package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;
import java.util.TreeSet;

public interface TaskManager {

    int createTask(Task newTask);

    int createSubtask(Subtask newSubtask, int epicId);

    int createEpic(Epic newEpic);

    List<Task> getTasks();

    List<Subtask> getSubtasks();

    List<Epic> getEpics();

    Task getTask(int taskId);

    Subtask getSubtask(int id);

    Epic getEpic(int id);

    List<Subtask> getEpicSubtasks(int epicId);

    List<Task> getHistory();

    TreeSet<Task> getPrioritizedTasks();

    void updateTask(Task updatedTask, int taskId);

    void updateSubtask(Subtask updatedSubtask, int subtaskId);

    void updateEpic(Epic updatedEpic, int id);

    void deleteTask(int id);

    void deleteSubtask(int subtaskId);

    void deleteEpic(int epicId);

    void deleteTasks();

    void deleteSubtasks();

    void deleteEpics();
}
