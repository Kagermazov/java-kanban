package service;

import model.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {
    private File csv;

    public FileBackedTasksManager(File csv) {
        this.csv = csv;
    }

    @Override
    public int createTask(Task taskEntity) {
        int id = super.createTask(taskEntity);

        save();
        return id;
    }

    @Override
    public int createEpic(Epic epicEntity) {
        int id = super.createEpic(epicEntity);

        save();
        return id;
    }

    @Override
    public int createSubtask(Subtask subtaskEntity, int epicId) {
        int id = super.createSubtask(subtaskEntity, epicId);

        save();
        return id;
    }

    @Override
    public Task getTask(int taskId) {
        Task foundTask = super.getTask(taskId);

        save();
        return foundTask;
    }

    @Override
    public Subtask getSubtask(int subtaskId) {
        Subtask foundSubtask = super.getSubtask(subtaskId);

        save();
        return foundSubtask;
    }

    @Override
    public Epic getEpic(int epicId) {
        Epic foundEpic = super.getEpic(epicId);

        save();
        return foundEpic;
    }

    @Override
    public void updateTask(Task taskToUpdate, int taskId) {
        super.updateTask(taskToUpdate, taskId);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtaskToUpdate, int subtaskId) {
        super.updateSubtask(subtaskToUpdate, subtaskId);
        save();
    }

    @Override
    public void updateEpic(Epic epicToUpdate, int epicId) {
        super.updateEpic(epicToUpdate, epicId);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteSubtasks() {
        super.deleteSubtasks();
        save();
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.csv))) {
            writer.write("id,type,name,status,description,epic\n");

            for (Task taskEntity : getTasks()) {
                writer.write(toString(taskEntity) + "\n");
            }

            for (Task taskEntity : getEpics()) {
                writer.write(toString(taskEntity) + "\n");
            }

            for (Task taskEntity : getSubtasks()) {
                writer.write(toString(taskEntity) + "\n");
            }

            writer.write("\n");

            String history = historyToString(getHistoryManager());

            if (history != null) {
                writer.write(history);
            }

        } catch (IOException e) {
            throw new ManagerSaveException(e);
        }
    }

    private static FileBackedTasksManager loadFromFile(File csv) throws IOException {
        FileBackedTasksManager manager = new FileBackedTasksManager(csv);
        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(csv))) {
            while (reader.ready()) {
                lines.add(reader.readLine());
            }
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("The file isn`t found.");
        }

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);

            if (line.isEmpty()) {
                List<Integer> tasksInHistory = historyFromString(lines.get(i + 1));

                for (Integer id : tasksInHistory) {
                    Task foundTask = manager.getById(id);

                    if (foundTask != null) {
                        manager.getHistoryManager().add(foundTask);
                    } else {
                        throw new IllegalStateException("Task by id " + id + " isn`t found for history recreation");
                    }
                }
                break;
            }

            Task taskEntity = fromString(line);
            manager.addTaskDirectly(taskEntity);
        }

        return manager;
    }

    private String toString(Task taskEntity) {
        if (taskEntity.getType() == TaskTypes.SUBTASK) {
            Subtask initialTask = (Subtask) taskEntity;
            return String.format("%d,%s,%s,%s,%s,%d", initialTask.getId(), initialTask.getType(), initialTask.getName(), initialTask.getStatus(), initialTask.getDescription(), initialTask.getEpicId());
        } else {
            return String.format("%d,%s,%s,%s,%s", taskEntity.getId(), taskEntity.getType(), taskEntity.getName(), taskEntity.getStatus(), taskEntity.getDescription());
        }
    }

    private static Task fromString(String value) {
        String[] taskCharacteristics = value.split(",");
        TaskTypes type = TaskTypes.valueOf(taskCharacteristics[1]);

        if (type == TaskTypes.SUBTASK) {
            Subtask regeneratedSubtask = new Subtask(
                    taskCharacteristics[2],
                    taskCharacteristics[4],
                    Status.valueOf(taskCharacteristics[3]),
                    type
            );

            regeneratedSubtask.setEpicId(Integer.parseInt(taskCharacteristics[5]));
            regeneratedSubtask.setId(Integer.parseInt(taskCharacteristics[0]));
            return regeneratedSubtask;
        } else if (type == TaskTypes.EPIC) {
            Epic regeneratedEpic = new Epic(
                    taskCharacteristics[2],
                    taskCharacteristics[4],
                    Status.valueOf(taskCharacteristics[3]),
                    type
            );

            regeneratedEpic.setId(Integer.parseInt(taskCharacteristics[0]));
            return regeneratedEpic;
        } else {
            Task regeneratedtask = new Task(
                    taskCharacteristics[2],
                    taskCharacteristics[4],
                    Status.valueOf(taskCharacteristics[3]),
                    type
            );

            regeneratedtask.setId(Integer.parseInt(taskCharacteristics[0]));
            return regeneratedtask;
        }
    }

    private static String historyToString(HistoryManager manager) {
        if (manager.getHistory().isEmpty()) {
            return null;
        }

        List<String> ids = new ArrayList<>();

        for (Task taskEntity : manager.getHistory()) {
            ids.add(Integer.toString(taskEntity.getId()));
        }

        return String.join(",", ids);
    }

    private static List<Integer> historyFromString(String value) {
        if (value.isEmpty()) {
            return new ArrayList<>();
        }

        String[] ids = value.split(",");
        List<Integer> result = new ArrayList<>();

        for (String id : ids) {
            result.add(Integer.parseInt(id));
        }

        return result;
    }

    public static void main(String[] args) throws IOException {
        File csvFile = new File("C:\\Users\\Sergey\\dev\\java-kanban\\src\\history.csv");
        FileBackedTasksManager fileBackedTasksManager = FileBackedTasksManager.loadFromFile(csvFile);

   /*     Task task = fileBackedTasksManager.getEpic(4);

        fileBackedTasksManager.createTask(
                new Task("Task1", "Description task1", Status.NEW, TaskTypes.TASK));

        int epicId1 = fileBackedTasksManager.createEpic(
                new Epic("Epic1", "Description epic1", Status.NEW, TaskTypes.EPIC));

        fileBackedTasksManager.createSubtask(
                new Subtask("Subtask1_1", "Description subtask1_1", Status.NEW, TaskTypes.SUBTASK),
                epicId1);

        fileBackedTasksManager.createEpic(
                new Epic("Epic2", "Description epic2", Status.IN_PROGRESS, TaskTypes.EPIC));
*/
        fileBackedTasksManager.getTask(1);

        fileBackedTasksManager.getEpic(2);
        fileBackedTasksManager.getTask(1);
        fileBackedTasksManager.getEpic(4);
        fileBackedTasksManager.getEpic(2);
        fileBackedTasksManager.getEpic(2);
//        System.out.println(task.toString());

        FileBackedTasksManager fileManager = new FileBackedTasksManager(csvFile);
//        printHistory(fileManager);
    }

    private static void printHistory(TaskManager taskManager) {
        List<Task> history = taskManager.getHistory();

        if (history.isEmpty()) {
            System.out.println("History is empty.");
        } else {
            for (Task task : taskManager.getHistory()) {
                System.out.println(task);
            }
            System.out.println();
        }
    }
}
