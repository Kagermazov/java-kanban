package service;

import model.*;

import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private final File csv;

    public FileBackedTaskManager(File csv) {
        this.csv = csv;
    }

    @Override
    public int createTask(Task newTask) {
        int id = super.createTask(newTask);

        save();
        return id;
    }

    @Override
    public int createEpic(Epic newEpic) {
        int id = super.createTask(newEpic);

        save();
        return id;
    }

    @Override
    public int createSubtask(Subtask newSubtask, int epicId) {
        int id = super.createSubtask(newSubtask, epicId);

        save();
        return id;
    }

    @Override
    public Task getTask(int taskId) {
        save();
        return super.getTask(taskId);
    }

    @Override
    public Subtask getSubtask(int subtaskId) {
        save();
        return super.getSubtask(subtaskId);
    }

    @Override
    public Epic getEpic(int epicId) {
        save();
        return super.getEpic(epicId);
    }

    @Override
    public void updateTask(Task updatedtask, int taskId) {
        super.updateTask(updatedtask, taskId);
        save();
    }

    @Override
    public void updateSubtask(Subtask updatedSubtask, int subtaskId) {
        super.updateSubtask(updatedSubtask, subtaskId);
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

            for (Task epic : getEpics()) {
                writer.write(toString(epic) + "\n");
            }

            for (Task subtask : getSubtasks()) {
                writer.write(toString(subtask) + "\n");
            }

            writer.write("\n");

            Optional<String> history = FileBackedTaskManager.historyToString();

            if (history.isPresent()) {
                writer.write(history.get());
            }

        } catch (IOException e) {
            throw new ManagerSaveException(e);
        }
    }

    public static FileBackedTaskManager loadFromFile(File csv) throws IOException {
        FileBackedTaskManager manager = new FileBackedTaskManager(csv);
        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(csv))) {
            while (reader.ready()) {
                lines.add(reader.readLine());
            }
        } catch (FileNotFoundException | NullPointerException e) {
            throw new FileNotFoundException("The file isn`t found.");
        }

        if (lines.isEmpty()) {
            return manager;
        }

        int historySeparatorIndex = lines.size() - 2;

        if (lines.get(historySeparatorIndex).isEmpty()) {
            List<Integer> tasksInHistory = FileBackedTaskManager.historyFromString(lines.getLast());

            IntStream.range(1, historySeparatorIndex)
                    .mapToObj(lines::get)
                    .map(FileBackedTaskManager::fromString)
                    .forEach(manager::addTaskToMemory);

            tasksInHistory.forEach(id -> {
                Optional<Task> foundTask = manager.getById(id);

                if (foundTask.isPresent()) {
                    manager.addTaskToMemory(foundTask.get());
                } else {
                    throw new IllegalStateException("Task by id " + id + " isn`t found for history recreation");
                }
            });
        } else {
            IntStream.range(1, lines.size())
                    .mapToObj(lines::get)
                    .filter(line -> !line.isEmpty())
                    .map(FileBackedTaskManager::fromString)
                    .forEach(manager::addTaskToMemory);
        }

        return manager;
    }

    private String toString(Task taskEntity) {
        if (taskEntity.getType() == TaskTypes.SUBTASK) {
            Subtask initialTask = (Subtask) taskEntity;
            return String.format("%d,%s,%s,%s,%s,%d", initialTask.getId(), initialTask.getType(), initialTask.getName(), initialTask.getTaskStatus(), initialTask.getDescription(), initialTask.getEpicId());
        } else {
            return String.format("%d,%s,%s,%s,%s", taskEntity.getId(), taskEntity.getType(), taskEntity.getName(), taskEntity.getTaskStatus(), taskEntity.getDescription());
        }
    }

    private static Task fromString(String value) {
        String[] taskCharacteristics = value.split(",");
        final TaskTypes type = TaskTypes.valueOf(taskCharacteristics[1]);
        final String name = taskCharacteristics[2];
        final String description = taskCharacteristics[4];
        final String status = taskCharacteristics[3];
        final String epicId = taskCharacteristics[5];
        final String subtaskId = taskCharacteristics[0];
        final String duration = taskCharacteristics[6];
        final String startTime = taskCharacteristics[7];

        switch (type) {
            case SUBTASK -> {
                Subtask regeneratedSubtask = new Subtask(
                        name,
                        description,
                        Status.valueOf(status),
                        type,
                        Duration.ofMinutes(Long.parseLong(duration)),
                        Instant.ofEpochSecond(Long.parseLong(startTime))
                );

                regeneratedSubtask.setEpicId(Integer.parseInt(epicId));
                regeneratedSubtask.setId(Integer.parseInt(subtaskId));
                return regeneratedSubtask;
            }
            case EPIC -> {
                Epic regeneratedEpic = new Epic(
                        name,
                        description,
                        Status.valueOf(status),
                        type,
                        Duration.ofMinutes(Long.parseLong(duration)),
                        Instant.ofEpochSecond(Long.parseLong(startTime))
                );

                regeneratedEpic.setId(Integer.parseInt(subtaskId));
                return regeneratedEpic;
            }
            default -> {
                Task regeneratedtask = new Task(
                        name,
                        description,
                        Status.valueOf(status),
                        type,
                        Duration.ofMinutes(Long.parseLong(duration)),
                        Instant.ofEpochSecond(Long.parseLong(startTime))
                );

                regeneratedtask.setId(Integer.parseInt(subtaskId));
                return regeneratedtask;
            }
        }
    }

    private static Optional<String> historyToString() {
        List<Task> history = InMemoryTaskManager.historyManager.getHistory();

        if (history.isEmpty()) {
            return Optional.empty();
        }

        List<String> ids = history.stream()
                        .map(taskEntity -> Integer.toString(taskEntity.getId()))
                .collect(Collectors.toList());

        return Optional.of(String.join(",", ids));
    }

    private static List<Integer> historyFromString(String value) {
        if (value.isEmpty()) {
            return new ArrayList<>();
        }

        String[] ids = value.split(",");

        return Arrays.stream(ids)
                        .map(Integer::parseInt)
                                .collect(Collectors.toList());
    }
}
