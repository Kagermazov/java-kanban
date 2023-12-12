import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import service.Managers;
import service.TaskManager;

public class Main {
    public static void main(String[] args) {

        Task task1 = new Task("", "", Status.NEW);
        Task task2 = new Task("", "", Status.NEW);

        Epic epic1 = new Epic("", "", Status.NEW);
        Subtask subtask1_1 = new Subtask("", "", Status.DONE);
        Subtask subtask1_2 = new Subtask("", "", Status.NEW);

        Epic epic2 = new Epic("", "", Status.NEW);
        Subtask subtask2 = new Subtask("", "", Status.DONE);

        Epic epic3 = new Epic("", "", Status.NEW);

        TaskManager taskManager = Managers.getDefault();

        int task1Id = taskManager.createTask(task1);
        int task2Id = taskManager.createTask(task2);

        int epic1Id = taskManager.createEpic(epic1);
        System.out.println(taskManager.getEpics() + "\n");
        int subtask1_1Id = taskManager.createSubtask(subtask1_1, epic1Id);
        int subtask1_2Id = taskManager.createSubtask(subtask1_2, epic1Id);

        int epic2Id = taskManager.createEpic(epic2);
        int subtask2Id = taskManager.createSubtask(subtask2, epic2Id);

        int epic3Id = taskManager.createEpic(epic3);
//
//        System.out.println(taskManager.getTasks() + "\n");
        System.out.println(taskManager.getEpics() + "\n");
        System.out.println(taskManager.getSubtasks() + "\n");

//        Task newTask1 = new Task("", "", Status.IN_PROGRESS);
//        taskManager.updateTask(newTask1, task1Id);
//        System.out.println(taskManager.getTask(task1Id) + "\n");
//        System.out.println(taskManager.getTasks() + "\n");
//
//        Subtask newSubtask1_1 = new Subtask("", "", Status.DONE);
//        taskManager.updateSubtask(newSubtask1_1, subtask1_1Id);
//        System.out.println(taskManager.getSubtask(subtask1_1Id) + "\n");
//        System.out.println(taskManager.getEpic(epic1Id) + "\n");
//
//        Subtask newSubtask1_2 = new Subtask("", "", Status.DONE);
//        taskManager.updateSubtask(newSubtask1_2, subtask1_2Id);
//        System.out.println(taskManager.getSubtask(subtask1_2Id) + "\n");
//        System.out.println(taskManager.getEpic(epic1Id) + "\n");
//
//        Subtask newSubtask2 = new Subtask("", "", Status.DONE);
//        taskManager.updateSubtask(newSubtask2, subtask2Id);
//        System.out.println(taskManager.getEpic(epic2Id) + "\n");

//        taskManager.deleteTask(task1Id);
//        System.out.println(taskManager.getTasks() + "\n");
//
//        taskManager.deleteEpic(epic1Id);
//        System.out.println(taskManager.getEpics() + "\n");
//        System.out.println(taskManager.getSubtasks() + "\n");

//        taskManager.deleteSubtask(subtask2Id);
//        System.out.println(taskManager.getEpic(epic2Id) + "\n");
//
//        taskManager.deleteSubtasks();
//        System.out.println(taskManager.getEpics() + "\n");

        taskManager.getTask(task1Id);
        taskManager.getTask(task2Id);
        taskManager.getEpic(epic1Id);
        taskManager.getSubtask(subtask2Id);
        taskManager.getTask(task1Id);
        taskManager.getTask(task1Id);
        taskManager.getTask(task1Id);
        taskManager.getTask(task1Id);
        taskManager.getTask(task1Id);
        taskManager.getTask(task1Id);
        taskManager.getTask(task1Id);

        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
    }
}
