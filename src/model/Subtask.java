package model;

import java.time.Duration;
import java.time.Instant;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, Status status, TaskTypes type, Duration subtaskDuration,
                   Instant startTime) {
        super(name, description, status, type, subtaskDuration, startTime);
        this.epicId = -1;
    }

    public int getEpicId() {
        return this.epicId;
    }

    public void setEpicId(int epicId) {
        if (epicId == this.getId()) {
            throw new IllegalArgumentException();
        }

        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "epicId = " + this.epicId +
                ", " + super.toString();
    }
}
