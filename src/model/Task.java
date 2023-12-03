package model;

public class Task {
    private final String NAME;
    private final String DESCRIPTION;
    private int id;
    private String status;

    public Task(String NAME, String description, String status) {
        this.NAME = NAME;
        this.DESCRIPTION = description;
        this.id = -1;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "NAME='" + NAME.length() + '\'' +
                ", DESCRIPTION='" + DESCRIPTION.length() + '\'' +
                ", id=" + id +
                ", status='" + status;
    }
}
