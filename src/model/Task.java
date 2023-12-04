package model;

public class Task {
    private String name;
    private String description;
    private int id;
    private String status;

    public Task(String name, String description, String status) {
        this.name = name;
        this.description = description;
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
        return "NAME='" + name.length() + '\'' +
                ", description='" + description.length() + '\'' +
                ", id=" + id +
                ", status='" + status;
    }
}
