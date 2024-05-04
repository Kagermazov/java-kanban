package service;

import com.sun.net.httpserver.HttpExchange;
import model.Epic;
import model.Subtask;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

public class EpicsHandler extends BaseHttpHandler {
    protected EpicsHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void delete(HttpExchange exchanger, String[] splitPath) throws IOException {
        int epicId = -1;

        if (splitPath.length == 3) {
            try {
                epicId = Integer.parseInt(splitPath[2]);

                this.manager.deleteEpic(epicId);
                super.sendResponse(exchanger, 200, "The epic with the id " + epicId + " was deleted");
            } catch (NoSuchElementException e) {
                super.sendResponse(exchanger, 404, "Task with the id " + epicId + "not found");
            }
        } else {
            super.sendResponse(exchanger, 400, "An id wasn`t sent");
        }
    }

    @Override
    public void post(String body, HttpExchange exchanger, String[] splitPath) throws IOException {
        if (splitPath.length == 2) {
            Epic newEpic = HttpTaskServer.getJsonParser().fromJson(body, Epic.class);
            int taskId = this.manager.createEpic(newEpic);

            super.sendResponse(exchanger, 201, ("The epic id is " + taskId));
        } else {
            super.sendResponse(exchanger, 400, ("Wrong URL"));
        }
    }

    @Override
    public void get(HttpExchange exchanger, String[] splitPath) throws IOException {
        int slipPathLength = splitPath.length;

        switch (slipPathLength) {
            case 2 -> {
                List<Epic> expectedList = this.manager.getEpics();

                super.sendResponse(exchanger, 200, expectedList);
            }
            case 3 -> getTaskById(exchanger, splitPath);
            case 4 -> {
                int epicId = Integer.parseInt(splitPath[2]);

                try {
                    List<Subtask> epicSubtasks = this.manager.getEpicSubtasks(epicId);

                    super.sendResponse(exchanger, 200, epicSubtasks);
                } catch (NoSuchElementException e) {
                    super.sendResponse(exchanger, 404, ("The epic with id " + epicId + " not found"));
                }
            }
            default -> super.sendResponse(exchanger, 400, ("Wrong URL"));
        }
    }

    private void getTaskById(HttpExchange exchanger, String[] splitPath) throws IOException {
        int epicId = Integer.parseInt(splitPath[2]);
        getTask(epicId, exchanger);
    }

    private void getTask(int epicId, HttpExchange exchanger) throws IOException {
        try {
            Epic expectedEpic = this.manager.getEpic(epicId);

            super.sendResponse(exchanger, 200, expectedEpic);
        } catch (NoSuchElementException e) {
            super.sendResponse(exchanger, 404, ("The epic with id " + epicId + " not found"));
        }
    }
}
