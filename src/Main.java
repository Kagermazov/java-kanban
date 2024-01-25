import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import service.Managers;
import service.TaskManager;

import java.util.List;

public class Main {
    public static void main(String[] args) {

        Task task1 = new Task("", "", Status.NEW);
        Task task2 = new Task("", "", Status.NEW);

        Epic epic1 = new Epic("", "", Status.NEW);
        Subtask subtask1_1 = new Subtask("", "", Status.DONE);
        Subtask subtask1_2 = new Subtask("", "", Status.NEW);
        Subtask subtask1_3 = new Subtask("", "", Status.DONE);

        Epic epic2= new Epic("", "", Status.NEW);

        TaskManager taskManager = Managers.getDefault();

        int task1Id = taskManager.createTask(task1);
        int task2Id = taskManager.createTask(task2);

        int epic1Id = taskManager.createEpic(epic1);
        int subtask1_1Id = taskManager.createSubtask(subtask1_1, epic1Id);
        int subtask1_2Id = taskManager.createSubtask(subtask1_2, epic1Id);
        int subtask1_3Id = taskManager.createSubtask(subtask1_3, epic1Id);

        int epic2Id = taskManager.createEpic(epic2);

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
