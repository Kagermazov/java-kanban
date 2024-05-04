package service;

import com.google.gson.Gson;
import model.Epic;
import model.Status;
import model.Subtask;
import model.TaskTypes;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class HttpTaskMangerEpicsTest {
    TaskManager manager;
    HttpTaskServer server;
    Gson jsonParser;
    Epic testEpic;
    Subtask testSubtask;

    public HttpTaskMangerEpicsTest() throws IOException {
        this.manager = new InMemoryTaskManager();
        this.server = new HttpTaskServer(this.manager);
        this.jsonParser = HttpTaskServer.getJsonParser();
        this.testEpic = new Epic("Test", "Testing epic",
                Status.NEW, TaskTypes.EPIC, null, null);
        this.testSubtask = new Subtask("Test2", "Testing subtask",
                Status.NEW, TaskTypes.SUBTASK, Duration.ofSeconds(42), Instant.EPOCH);
    }

    @BeforeEach
    public void setUp() {
        manager.deleteTasks();
        manager.deleteSubtasks();
        manager.deleteEpics();
        server.start();
    }

    @AfterEach
    public void shutDown() {
        server.stop();
    }

    @DisplayName("Should send 200 when the getEpics method called")
    @Test
    void shouldSend200When_getEpicsMethodCalled() throws IOException, InterruptedException {
        HttpResponse<String> response = getGetResponse("http://localhost:8080/epics/");

        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body());
    }

    @DisplayName("Should send 200 when the getEpic method called")
    @Test
    void shouldSend200When_getEpicMethodCalled() throws IOException, InterruptedException {
        this.manager.createEpic(this.testEpic);
        HttpResponse<String> response = getGetResponse("http://localhost:8080/epics/1");

        assertEquals(200, response.statusCode());
        assertEquals(this.testEpic, this.jsonParser.fromJson(response.body(), Epic.class));
    }

    @DisplayName("Should send 404 when the getEpic method called and an epic doesn`t exist")
    @Test
    void shouldSend200When_getEpicMethodCalledAndEpicDoesNotExist() throws IOException, InterruptedException {
        HttpResponse<String> response = getGetResponse("http://localhost:8080/epics/1");

        assertEquals(404, response.statusCode());
    }

    @DisplayName("Should send 200 when the getEpicSubtasks method called")
    @Test
    void shouldSend200When_getEpicSubtasksMethodCalled() throws IOException, InterruptedException {
        this.manager.createEpic(this.testEpic);
        this.manager.createSubtask(this.testSubtask, 1);

        HttpResponse<String> response = getGetResponse("http://localhost:8080/epics/1/subtasks");

        assertEquals(200, response.statusCode());

        List<Subtask> epicSubtasks = this.manager.getEpicSubtasks(1);

        assertNotNull(epicSubtasks, "Epics don`t return");
        assertEquals(1, epicSubtasks.size(), "Wrong epic amount");
        assertEquals("Test2", epicSubtasks.getFirst().getName(), "Wrong epic name");
    }

    @DisplayName("Should send 404 when the getEpicSubtasks method called and an epic does`nt exist")
    @Test
    void shouldSend200When_getEpicSubtasksMethodCalledAndEpicDoesNotExist() throws IOException, InterruptedException {
        HttpResponse<String> response = getGetResponse("http://localhost:8080/epics/1/subtasks");

        assertEquals(404, response.statusCode());
    }

    @DisplayName("Should send 200 when the createEpic method called")
    @Test
    void shouldSend200When_createEpicMethodCalled() throws IOException, InterruptedException {
        String epicJson = this.jsonParser.toJson(this.testEpic);
        HttpResponse<String> response = getPostResponse(epicJson,"http://localhost:8080/epics");

        assertEquals(201, response.statusCode());

        List<Epic> epicsFromManager = this.manager.getEpics();

        assertNotNull(epicsFromManager, "Epics don`t return");
        assertEquals(1, epicsFromManager.size(), "Wrong epic amount");
        assertEquals("Test", epicsFromManager.getFirst().getName(), "Wrong epic name");
    }

    @DisplayName("Should send 200 if the deleteEpic method called")
    @Test
    void shouldSend200If_deleteTaskMethodCalled() throws IOException, InterruptedException {
        this.manager.createTask(this.testEpic);

        HttpResponse<String> response = getDeleteResponse();

        assertEquals(200, response.statusCode());
        assertEquals(new ArrayList<>(), this.manager.getEpics());
    }

    @DisplayName("Should send 404 when the deleteEpic method was called and an epic doesn`t exist")
    @Test
    void shouldSend404When_deleteEpicMethodWasCalledAndEpicDoesNotExist() throws IOException, InterruptedException {
        HttpResponse<String> response = getDeleteResponse();

        assertEquals(404, response.statusCode());
    }

    private static HttpResponse<String> getDeleteResponse() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8080/epics/1"))
                .DELETE()
                .build();
        return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    }

    private static HttpResponse<String> getPostResponse(String taskJson, String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    }

    private static HttpResponse<String> getGetResponse(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    }
}
