package service;

import model.Task;

 class Node {
    Task data;
    Node next;
    Node prev;

    Node(Task data) {
        this.data = data;
    }
}