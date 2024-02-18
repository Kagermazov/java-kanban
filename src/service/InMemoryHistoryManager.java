package service;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> idToTask = new HashMap<>();
    private Node head;
    private Node tail;

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }

        int taskId = task.getId();

        if (idToTask.containsKey(taskId)) {
            this.remove(taskId);
        }

        this.linkLast(task);

        idToTask.put(taskId, this.tail);
    }

    @Override
    public void remove(int id) {
        removeNode(this.idToTask.get(id));
        this.idToTask.remove(id);
    }

    private void linkLast(Task task) {
        if (task != null) {
            if (this.head == null) {
                this.head = new Node(task, null, null);
                this.tail = this.head;
            } else {
                Node node = new Node(task, this.tail, null);
                this.tail.next = node;
                this.tail = node;
            }
        }
    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node node = this.head;

        while (node != null) {
            tasks.add(node.data);
            node = node.next;
        }

        return tasks;
    }

    private void removeNode(Node node) {
        if (node == this.head) {
            this.head = node.next;
        } else if (node == this.tail) {
            this.tail = node.prev;
            this.tail.next = null;
        } else {
            Node prev = node.prev;
            Node next = node.next;
            prev.next = next;
            next.prev = prev;
        }
    }
}
