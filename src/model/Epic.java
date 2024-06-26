package model;

import java.util.ArrayList;

public class Epic extends Task{
    private ArrayList<Integer> subtaskIds;

    public Epic(String name, String description, Status status, TaskTypes type) {
        super(name, description, status, type);
        subtaskIds = new ArrayList<>();
    }

    public ArrayList<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtaskIds=" + subtaskIds +
                ", " + super.toString() +
                "}";
    }
}
