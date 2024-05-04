package service;

import com.google.gson.Gson;
import model.*;
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

class HttpTaskMangerSubtasksTest {
    TaskManager manager;
    HttpTaskServer server;
    Gson jsonParser;
    Epic testEpic;
    Subtask testSubtask;

    public HttpTaskMangerSubtasksTest() throws IOException {
        this.manager = new InMemoryTaskManager();
        this.server = new HttpTaskServer(this.manager);
        this.jsonParser = HttpTaskServer.getJsonParser();
        this.testEpic = new Epic("Test", "Testing epic", Status.NEW, TaskTypes.EPIC,
                null, null);
        this.testSubtask = new Subtask("Test", "Testing subtask",
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

    @DisplayName("Should send 200 when the getSubtasks method called")
    @Test
    void shouldSend200When_getSubtasksMethodCalled() throws IOException, InterruptedException {
        HttpResponse<String> response = getGetResponse("http://localhost:8080/subtasks");

        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body());
    }

    @DisplayName("Should send 200 when the getSubtask method with an id called")
    @Test
    void shouldSend200When_getSubtaskMethodWithIdCalled() throws IOException, InterruptedException {
        this.manager.createEpic(this.testEpic);
        this.manager.createSubtask(this.testSubtask, 1);
        HttpResponse<String> response = getGetResponse("http://localhost:8080/subtasks/2");

        assertEquals(200, response.statusCode());
        assertEquals(this.testSubtask, this.jsonParser.fromJson(response.body(), Subtask.class));
    }

    @DisplayName("Should send 404 when the getSubtask method called and a subtask doesn`t exist")
    @Test
    void shouldSend200When_getSubtaskMethodCalledAndSubtaskDoesNotExist() throws IOException, InterruptedException {
        HttpResponse<String> response = getGetResponse("http://localhost:8080/subtasks/1");

        assertEquals(404, response.statusCode());
    }

    @DisplayName("Should send 200 when the createSubtask method called")
    @Test
    void shouldSend200When_createSubtaskMethodCalled() throws IOException, InterruptedException {
        this.manager.createEpic(this.testEpic);
        this.testSubtask.setEpicId(1);
        String subtaskJson = this.jsonParser.toJson(this.testSubtask);
        HttpResponse<String> response = getPostResponse(subtaskJson,"http://localhost:8080/subtasks");

        assertEquals(201, response.statusCode());

        List<Subtask> subtasksFromManager = this.manager.getSubtasks();

        assertNotNull(subtasksFromManager, "Subtasks don`t return");
        assertEquals(1, subtasksFromManager.size(), "Wrong subtask amount");
        assertEquals("Test", subtasksFromManager.getFirst().getName(), "Wrong subtask name");
    }

    @DisplayName("Should send 406 if a new subtask overlapping another one")
    @Test
    void shouldSend406IfNewSubtaskOverlappingAnotherOne() throws IOException, InterruptedException {
        Subtask testSubtask2 = new Subtask("Test2", "Testing task",
                Status.NEW, TaskTypes.TASK, Duration.ofSeconds(42), Instant.EPOCH);
        String testTask2Json = this.jsonParser.toJson(testSubtask);

        this.manager.createTask(testSubtask2);

        HttpResponse<String> response = getPostResponse(testTask2Json,"http://localhost:8080/tasks/");
        // проверяем код ответа
        assertEquals(406, response.statusCode());

        List<Task> tasksFromManager = this.manager.getTasks();

        assertNotNull(tasksFromManager, "Subtasks don`t return");
        assertEquals(1, tasksFromManager.size(), "Wrong subtask amount");
        assertEquals("Test2", tasksFromManager.getFirst().getName(), "Wrong subtask name");
    }

    @DisplayName("Should send 201 when the updateSubtask method called")
    @Test
    void shouldSend201When_updateSubtaskMethodCalled() throws IOException, InterruptedException {
        this.manager.createEpic(this.testEpic);

        Subtask updatedSubtask = new Subtask("Test2", "Testing task",
                Status.DONE, TaskTypes.SUBTASK, Duration.ofSeconds(42), Instant.EPOCH);
        String updatedSubtaskJson = this.jsonParser.toJson(updatedSubtask);

        this.manager.createSubtask(this.testSubtask, this.testEpic.getId());

        HttpResponse<String> response = getPostResponse(updatedSubtaskJson,"http://localhost:8080/subtasks/2");

        assertEquals(201, response.statusCode());

        List<Subtask> subtasksFromManager = this.manager.getSubtasks();

        assertNotNull(subtasksFromManager, "Subtasks don`t return");
        assertEquals(1, subtasksFromManager.size(), "Wrong subtask amount");
        assertEquals("Test2", subtasksFromManager.getFirst().getName(), "Wrong subtask name");
    }

    @DisplayName("Should send 404 when the updateSubtask method called with a wrong id")
    @Test
    void shouldSend201When_updateSubtaskMethodCalledWithWrongId() throws IOException, InterruptedException {
        this.manager.createEpic(this.testEpic);

        Subtask updatedSubtask = new Subtask("Test2", "Testing task",
                Status.DONE, TaskTypes.SUBTASK, Duration.ofSeconds(42), Instant.EPOCH);
        String updatedSubtaskJson = this.jsonParser.toJson(updatedSubtask);

        this.manager.createSubtask(this.testSubtask, this.testEpic.getId());

        HttpResponse<String> response = getPostResponse(updatedSubtaskJson,"http://localhost:8080/subtasks/1");

        assertEquals(404, response.statusCode());

        List<Subtask> subtasksFromManager = this.manager.getSubtasks();

        assertNotNull(subtasksFromManager, "Subtasks don`t return");
        assertEquals(1, subtasksFromManager.size(), "Wrong subtask amount");
        assertEquals("Test", subtasksFromManager.getFirst().getName(), "Wrong subtask name");
    }

    @DisplayName("Should send 406 when an updated subtask overlapping another task")
    @Test
    void shouldSend406WhenUpdatedSubtaskOverlappingAnotherTask() throws IOException, InterruptedException {
        this.manager.createEpic(this.testEpic);
        this.manager.createSubtask(this.testSubtask, this.testEpic.getId());

        Subtask testSubtask = new Subtask("Test2", "Testing task",
                Status.NEW, TaskTypes.SUBTASK, Duration.ofSeconds(2), Instant.ofEpochSecond(43));
        Subtask updatedSubtask = new Subtask("Test3", "Testing task",
                Status.DONE, TaskTypes.SUBTASK, Duration.ofSeconds(45), Instant.EPOCH);
        String updatedSubtaskJson = this.jsonParser.toJson(updatedSubtask);

        this.manager.createTask(testSubtask);

        HttpResponse<String> response = getPostResponse(updatedSubtaskJson,"http://localhost:8080/subtasks/3");

        assertEquals(406, response.statusCode());

        List<Subtask> subtasksFromManager = this.manager.getSubtasks();

        assertNotNull(subtasksFromManager, "Subtasks don`t return");
        assertEquals(1, subtasksFromManager.size(), "Wrong subtask amount");
        assertEquals("Test", subtasksFromManager.getFirst().getName(), "Wrong subtask name");
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

    @DisplayName("Should send 200 when the deleteSubtask method called")
    @Test
    void shouldSend200When_deleteSubtaskMethodCalled() throws IOException, InterruptedException {
        this.manager.createEpic(this.testEpic);
        this.manager.createSubtask(this.testSubtask, 1);
        HttpResponse<String> response = getDeleteResponse();

        assertEquals(200, response.statusCode());
        assertEquals(new ArrayList<>(), this.manager.getSubtasks());
    }

    @DisplayName("Should send 404 when the deleteSubtask method called and a subtask doesn`t exist")
    @Test
    void shouldSend404MethodWasCalledAndSubtaskDoesNotExist() throws IOException, InterruptedException {
        HttpResponse<String> response = getDeleteResponse();

        assertEquals(404, response.statusCode());
    }

    private static HttpResponse<String> getDeleteResponse() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/2"))
                .DELETE()
                .build();
        return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    }


}