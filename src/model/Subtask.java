package model;

import java.time.Duration;
import java.time.Instant;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, Status status, TaskTypes type, Duration subtaskDuration,
                   Instant startTime) {
        super(name, description, status, type, subtaskDuration, startTime);
        epicId = -1;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        if (epicId == this.getId()) {
            throw new IllegalArgumentException();
        }

        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "epicId=" + epicId +
                ", " + super.toString() +
                "}";
    }
}
