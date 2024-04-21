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
    public void add(Task newTask) {
        if (newTask == null) {
            return;
        }

        int taskId = newTask.getId();

        if (this.idToTask.containsKey(taskId)) {
            this.remove(taskId);
        }

        this.linkLast(newTask);
        this.idToTask.put(taskId, this.tail);
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
        Node savedNode = this.head;

        while (savedNode != null) {
            tasks.add(savedNode.data);
            savedNode = savedNode.next;
        }

        return tasks;
    }

    private void removeNode(Node nodeToRemove) {
        if (nodeToRemove == null) {
            return;
        }

        if (nodeToRemove == this.head) {
            this.head = nodeToRemove.next;
        } else if (nodeToRemove == this.tail) {
            this.tail = nodeToRemove.prev;
            this.tail.next = null;
        } else {
            Node prev = nodeToRemove.prev;
            Node next = nodeToRemove.next;
            prev.next = next;
            next.prev = prev;
        }
    }
}
