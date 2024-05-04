package service;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Subtask;
import model.Task;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {

    public SubtasksHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void delete(HttpExchange exchanger,String[] splitPath) throws IOException {
        int subtaskId = -1;

        if (splitPath.length == 3) {
            try {
                subtaskId = Integer.parseInt(splitPath[2]);

                this.manager.deleteSubtask(subtaskId);
                super.sendResponse(exchanger, 200, "Subtask with id " + subtaskId + " was deleted");
            } catch (NoSuchElementException e) {
                super.sendResponse(exchanger, 404, "Subtask with id " + subtaskId + "not found");
            }
        } else {
            super.sendResponse(exchanger, 400, "An id wasn`t sent");
        }
    }

    @Override
    public void post(String body, HttpExchange exchanger, String[] splitPath) throws IOException {
        if (splitPath.length == 3) {
            int subtaskId = Integer.parseInt(splitPath[2]);

            try {
                Subtask updatedSubtask = HttpTaskServer.getJsonParser().fromJson(body, Subtask.class);
                this.manager.updateSubtask(updatedSubtask, subtaskId);
                super.sendResponse(exchanger, 201, ("Subtask with id " + subtaskId + " was updated"));

            } catch (NoSuchElementException e) {

                super.sendResponse(exchanger, 404, ("Subtask with id " + subtaskId + " not found"));

            } catch (IllegalArgumentException e) {

                super.sendResponse(exchanger, 406, "Subtask time is overlapping");
            }

        } else {
            Subtask newSubtask = HttpTaskServer.getJsonParser().fromJson(body, Subtask.class);

            try {
                int taskId = this.manager.createSubtask(newSubtask, newSubtask.getEpicId());

                super.sendResponse(exchanger, 201, ("The Subtask id is " + taskId));
            } catch (IllegalArgumentException e) {

                super.sendResponse(exchanger, 406, "Subtask time is overlapping");
            }
        }
    }

    @Override
    public void get(HttpExchange exchanger,String[] splitPath) throws IOException {
        if (splitPath.length == 3) {
            getTaskById(exchanger, splitPath);
        } else {
            List<Subtask> expectedList = this.manager.getSubtasks();

            super.sendResponse(exchanger, 200, expectedList);
        }
    }

    private void getTaskById(HttpExchange exchanger, String[] splitPath) throws IOException {
        int taskId = Integer.parseInt(splitPath[2]);
        getTask(taskId, exchanger);
    }

    private void getTask(int taskId, HttpExchange exchanger) throws IOException {
        try {
            Task expectedtask = this.manager.getSubtask(taskId);

            super.sendResponse(exchanger, 200, expectedtask);
        } catch (NoSuchElementException e) {
            super.sendResponse(exchanger, 404, ("The subtask with id " + taskId + " not found"));
        }
    }
}
