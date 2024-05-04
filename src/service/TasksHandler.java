package service;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Task;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {

    public TasksHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void delete(HttpExchange exchanger,String[] splitPath) throws IOException {
        int taskId = -1;

        if (splitPath.length == 3) {
            try {
                taskId = Integer.parseInt(splitPath[2]);

                this.manager.deleteTask(taskId);
                super.sendResponse(exchanger, 200, "The task with the id " + taskId + " was deleted");
            } catch (NoSuchElementException e) {
                super.sendResponse(exchanger, 404, "The task with the id " + taskId + "not found");
            }
        } else {
            super.sendResponse(exchanger, 400, "An id wasn`t sent");
        }
    }

    @Override
    public void post(String body, HttpExchange exchanger, String[] splitPath) throws IOException {

        if (splitPath.length == 3) {
            int taskId = Integer.parseInt(splitPath[2]);

            try {
                Task updatedTask = HttpTaskServer.getJsonParser().fromJson(body, Task.class);
                this.manager.updateTask(updatedTask, taskId);
                super.sendResponse(exchanger, 201, ("The task with id " + taskId + " was updated"));

            } catch (NoSuchElementException e) {

                super.sendResponse(exchanger, 404, ("The task with id " + taskId + " not found"));

            } catch (IllegalArgumentException e) {

                super.sendResponse(exchanger, 406, "The task time is overlapping");
            }

        } else {
            Task newTask = HttpTaskServer.getJsonParser().fromJson(body, Task.class);

            try {
                int taskId = this.manager.createTask(newTask);

                super.sendResponse(exchanger, 201, ("The task id is " + taskId));
            } catch (IllegalArgumentException e) {

                super.sendResponse(exchanger, 406, "The task time is overlapping");
            }
        }
    }

    @Override
    public void get(HttpExchange exchanger,String[] splitPath) throws IOException {

        if (splitPath.length == 3) {
            getTaskById(exchanger, splitPath);
        } else {
            List<Task> expectedList = this.manager.getTasks();

            super.sendResponse(exchanger, 200, expectedList);
        }
    }

    private void getTaskById(HttpExchange exchanger, String[] splitPath) throws IOException {
        int taskId = Integer.parseInt(splitPath[2]);
        getTask(taskId, exchanger);
    }

    private void getTask(int taskId, HttpExchange exchanger) throws IOException {
        try {
            Task expectedTask = this.manager.getTask(taskId);

            super.sendResponse(exchanger, 200, expectedTask);
        } catch (NoSuchElementException e) {
            super.sendResponse(exchanger, 404, ("The task with id " + taskId + " not found"));
        }
    }
}
