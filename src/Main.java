import model.*;
import service.Managers;
import service.TaskManager;

import java.io.File;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault(new File(
                "C:\\Users\\Sergey\\dev\\java-kanban\\src\\history.csv"));

        int task1Id = taskManager.createTask(new Task("", "", Status.NEW, TaskTypes.TASK));
        int task2Id = taskManager.createTask(new Task("", "", Status.NEW, TaskTypes.TASK));

        int epic1Id = taskManager.createEpic(new Epic("", "", Status.NEW, TaskTypes.EPIC));
        int subtask1_1Id = taskManager.createSubtask(new Subtask("", "", Status.DONE, TaskTypes.SUBTASK), epic1Id);
        int subtask1_2Id = taskManager.createSubtask(new Subtask("", "", Status.NEW, TaskTypes.SUBTASK), epic1Id);
        int subtask1_3Id = taskManager.createSubtask(new Subtask("", "", Status.DONE, TaskTypes.SUBTASK), epic1Id);

        int epic2Id = taskManager.createEpic(new Epic("", "", Status.NEW, TaskTypes.EPIC));

        taskManager.getTask(task1Id);
        printHistory(taskManager);

        taskManager.getSubtask(subtask1_3Id);
        printHistory(taskManager);

        taskManager.getEpic(epic1Id);
        printHistory(taskManager);

        taskManager.getTask(task1Id);
        printHistory(taskManager);

        taskManager.deleteTask(task1Id);
        printHistory(taskManager);

        taskManager.deleteEpic(epic1Id);
        printHistory(taskManager);
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
