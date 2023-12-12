package model;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, Status status) {
        super(name, description, status);
        epicId = -1;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
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
