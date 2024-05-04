package model;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

public class Task {
    private final String name;
    private final String description;
    private int id;
    private Status taskStatus;
    private final TaskTypes type;
    private final Duration duration;
    private Instant startTime;

    public Task(String name, String description, Status taskStatus, TaskTypes type, Duration taskDuration,
                Instant dateTime) {
        this.name = name;
        this.description = description;
        this.duration = taskDuration;
        this.id = -1;
        this.taskStatus = taskStatus;
        this.type = type;
        this.startTime = dateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task taskToCompare = (Task) o;
        return this.id == taskToCompare.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    public Duration getDuration() {
        return this.duration;
    }

    public Instant getStartTime() {
        return this.startTime;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public int getId() {
        return this.id;
    }

    public Status getTaskStatus() {
        return this.taskStatus;
    }

    public TaskTypes getType() {
        return this.type;
    }

    public Optional<Instant> getEndTime() {
        if (this.startTime == null) {
            return Optional.empty();
        }

        return Optional.of(this.startTime.plus(this.duration));
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTaskStatus(Status taskStatus) {
        this.taskStatus = taskStatus;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    @Override
    public String toString() {
        return "name = " + this.name +
                ", description = " + this.description +
                ", id = " + this.id +
                ", taskStatus = " + this.taskStatus +
                ", type = " + this.type +
                ", duration = " + this.duration +
                ", startTime = " + this.startTime;
    }
}
