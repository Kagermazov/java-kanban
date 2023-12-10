package service;

import java.util.List;

public interface TaskManager<T> {

    int createTask(T task);

    int createEpic(T epic);

    int createSubtask(T subtask, Integer epicId);

    List<T> getTasks();

    List<T> getSubtasks();

    List<T> getEpics();

    T getTask(Integer taskId);

    T getSubtask(Integer id);

    T getEpic(Integer id);

    List<T> getEpicSubtasks(Integer epicId);

    void updateTask(T task, Integer taskId);

    void updateSubtask(T subtask, Integer subtaskId);

    void updateEpic(T epic, Integer id);

    void deleteTask(Integer id);

    void deleteSubtask(Integer subtaskId);

    void deleteEpic(Integer epicId);

    void deleteTasks();

    void deleteSubtasks();

    void deleteEpics();
}
