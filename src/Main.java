import model.Epic;
import model.Subtask;
import model.Task;
import service.Manager;

public class Main {
    public static void main(String[] args) {
        Task task1 = new Task("", "", "NEW");
        Task task2 = new Task("", "", "NEW");

        Subtask subtask1_1 = new Subtask("", "", "NEW");
        Subtask subtask1_2 = new Subtask("", "", "NEW");

        Epic epic1 = new Epic("", "", "NEW");

        Subtask subtask2 = new Subtask("", "", "NEW");

        Epic epic2 = new Epic("", "", "NEW");

        Manager manager = new Manager();

        int task1Id = manager.createTask(task1);
        int task2Id = manager.createTask(task2);

        int subtask1_1Id = manager.createSubtask(subtask1_1);
        int subtask1_2Id = manager.createSubtask(subtask1_2);

        int epic1Id = manager.createEpic(epic1);

        int subtask2Id = manager.createSubtask(subtask2);
        int epic2Id = manager.createEpic(epic2);

        System.out.println(manager.getTasks());
        System.out.println(manager.getSubtasks());
        System.out.println(manager.getEpics());

        Task newTask1 = new Task("", "", "IN_PROGRESS");
        manager.updateTask(newTask1, task1Id);
        System.out.println(manager.getTask(task1Id));

        Subtask newSubtask1_1 = new Subtask("", "", "IN_PROGRESS");
        manager.updateSubtask(newSubtask1_1, subtask1_1Id);
        System.out.println(manager.getSubtask(subtask1_1Id));
        System.out.println(manager.getEpic(epic1Id));

        Subtask newSubtask1_2 = new Subtask("", "", "IN_PROGRESS");
        manager.updateSubtask(newSubtask1_2, subtask1_2Id);
        System.out.println(manager.getSubtask(subtask1_2Id));
        System.out.println(manager.getEpic(epic1Id));

        Subtask newSubtask2 = new Subtask("", "", "DONE");
        manager.updateSubtask(newSubtask2, subtask2Id);
        System.out.println(manager.getEpic(epic2Id));

        manager.deleteTask(task1Id);
        System.out.println(manager.getTasks());

        manager.deleteEpic(epic1Id);
        System.out.println(manager.getEpics());
    }
}
