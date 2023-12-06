import model.Epic;
import model.Subtask;
import service.Manager;

public class Main {
    public static void main(String[] args) {
//        Task task1 = new Task("", "", "NEW");
//        Task task2 = new Task("", "", "NEW");

        Epic epic1 = new Epic("", "", "NEW");
        Subtask subtask1_1 = new Subtask("", "", "DONE");
        Subtask subtask1_2 = new Subtask("", "", "NEW");

//        Epic epic2 = new Epic("", "", "NEW");
//        Subtask subtask2 = new Subtask("", "", "DONE");

        Manager manager = new Manager();

//        int task1Id = manager.createTask(task1);
//        int task2Id = manager.createTask(task2);

        int epic1Id = manager.createEpic(epic1);
        System.out.println(manager.getEpics() + "\n");
        int subtask1_1Id = manager.createSubtask(subtask1_1, epic1Id);
        int subtask1_2Id = manager.createSubtask(subtask1_2, epic1Id);

//        int epic2Id = manager.createEpic(epic2);
//        int subtask2Id = manager.createSubtask(subtask2, epic2Id);

//        System.out.println(manager.getTasks() + "\n");
        System.out.println(manager.getEpics() + "\n");
        System.out.println(manager.getSubtasks() + "\n");
//
//        Task newTask1 = new Task("", "", "IN_PROGRESS");
//        manager.updateTask(newTask1, task1Id);
//        System.out.println(manager.getTask(task1Id) + "\n");
//
//        Subtask newSubtask1_1 = new Subtask("", "", "DONE");
//        manager.updateSubtask(newSubtask1_1, subtask1_1Id);
//        System.out.println(manager.getSubtask(subtask1_1Id) + "\n");
//        System.out.println(manager.getEpic(epic1Id) + "\n");

//        Subtask newSubtask1_2 = new Subtask("", "", "DONE");
//        manager.updateSubtask(newSubtask1_2, subtask1_2Id);
//        System.out.println(manager.getSubtask(subtask1_2Id) + "\n");
//        System.out.println(manager.getEpic(epic1Id) + "\n");
//
//        Subtask newSubtask2 = new Subtask("", "", "DONE");
//        manager.updateSubtask(newSubtask2, subtask2Id);
//        System.out.println(manager.getEpic(epic2Id) + "\n");
//
//        manager.deleteTask(task1Id);
//        System.out.println(manager.getTasks() + "\n");
//
//        manager.deleteEpic(epic1Id);
//        System.out.println(manager.getEpics() + "\n");
//        System.out.println(manager.getSubtasks() + "\n");

//        manager.deleteSubtask(subtask2Id);
//        System.out.println(manager.getEpic(epic2Id) + "\n");
//
        manager.deleteSubtasks();
        System.out.println(manager.getEpics() + "\n");
    }
}
