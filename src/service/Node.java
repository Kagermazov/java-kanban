package service;

import model.Task;

 class Node {
    Task data;
    Node next;
    Node prev;

     public Node(Task task, Node prev, Node next) {
         this.data = task;
         this.prev = prev;
         this.next = next;
     }
}