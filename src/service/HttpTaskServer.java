package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.Instant;

public class HttpTaskServer {
    private final HttpServer server;
    private static final Gson jsonParser = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(Instant.class, new InstantAdapter())
            .create();

    public HttpTaskServer(TaskManager manager) throws IOException {
        this.server = HttpServer.create(new InetSocketAddress(8080), 0);
        activateHandlers(manager);
    }

    private void activateHandlers(TaskManager manager) {
        this.server.createContext("/tasks", new TasksHandler(manager));
        this.server.createContext("/subtasks", new SubtasksHandler(manager));
        this.server.createContext("/epics", new EpicsHandler(manager));
        this.server.createContext("/history", new HistoryHandler(manager));
        this.server.createContext("/prioritized", new PrioritizedHandler(manager));
    }

    public static Gson getJsonParser() {
        return jsonParser;
    }

    public static void main(String[] args) throws IOException {
        File csv = new File("C:\\Users\\Sergey\\dev\\java-kanban\\src\\history.csv");

        if (!csv.exists()) {
            csv.createNewFile();
        }

        HttpTaskServer taskServer = new HttpTaskServer(Managers.getDefault(csv));

        taskServer.start();
    }

    public void start() {
        this.server.start();
    }

    public void stop() {
        this.server.stop(1);
    }
}
