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
    private int size = 0;

    @Override
    public List<Task> getHistory() {
        return this.getTasks();
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
            if (this.size == 0) {
                this.head = new Node(task);
                this.tail = this.head;
            } else {
                Node node = new Node(task);
                this.tail.next = node;
                node.prev = this.tail;
                this.tail = node;
            }
            this.size++;
        }
    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();

        if (this.size != 0) {
            for (Node node : idToTask.values()) {
                tasks.add(node.data);
            }
        }

        return tasks;
    }

    private void removeNode(Node node) {
        if (node == this.head) {
            this.head = node.next;
        } else {
            Node prev = node.prev;
            Node next = node.next;
            if (next != null) {
                prev.next = next;
                next.prev = prev;
            } else {
                this.tail = prev;
            }
        }
    }
}
