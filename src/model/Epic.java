package model;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Epic extends Task {
    private final List<Integer> subtaskIds;
    private Instant endTime;

    public Epic(String name, String description, Status currentStatus, TaskTypes type, Duration epicDuration,
                Instant startTime) {
        super(name, description, currentStatus, type, epicDuration, startTime);
        this.subtaskIds = new ArrayList<>();
        this.endTime = null;
    }

    public List<Integer> getSubtaskIds() {
        return new ArrayList<>(this.subtaskIds);
    }

    public void addIdToSubtaskIds(int id) {
        if (id == this.getId()) {
            throw new IllegalArgumentException();
        }

        this.subtaskIds.add(id);
    }

    public void removeIdFromSubtasksIds(int id) {
        this.subtaskIds.remove((Integer.valueOf(id)));
    }

    public void clearSubtasksIds() {
        this.subtaskIds.clear();
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    @Override
    public Optional<Instant> getEndTime() {
        return Optional.of(this.endTime);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtaskIds=" + subtaskIds +
                ", " + super.toString() +
                "}";
    }
}
