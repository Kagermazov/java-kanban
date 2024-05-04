package service;

import com.google.gson.Gson;
import model.Status;
import model.Task;
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

class HttpTaskManagerTasksTest {
    TaskManager manager;
    HttpTaskServer server;
    Gson jsonParser;
    Task testTask;

    public HttpTaskManagerTasksTest() throws IOException {
        this.manager = new InMemoryTaskManager();
        this.server = new HttpTaskServer(this.manager);
        this.jsonParser = HttpTaskServer.getJsonParser();
        this.testTask = new Task("Test", "Testing task",
                Status.NEW, TaskTypes.TASK, Duration.ofSeconds(42), Instant.EPOCH);
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

    @DisplayName("Should send 200 when the createTask method called")
    @Test
    void shouldSend200When_createTaskMethodCalled() throws IOException, InterruptedException {
        String taskJson = this.jsonParser.toJson(this.testTask);
        HttpResponse<String> response = getPostResponse(taskJson,"http://localhost:8080/tasks");

        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = this.manager.getTasks();

        assertNotNull(tasksFromManager, "Tasks don`t return");
        assertEquals(1, tasksFromManager.size(), "Wrong task amount");
        assertEquals("Test", tasksFromManager.getFirst().getName(), "Wrong task name");
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

    @DisplayName("Should send 406 if a new task overlapping another one")
    @Test
    void shouldSend406IfNewTaskOverlappingAnotherOne() throws IOException, InterruptedException {
        Task testTask2 = new Task("Test2", "Testing task",
                Status.NEW, TaskTypes.TASK, Duration.ofSeconds(42), Instant.EPOCH);
        String testTaskJson = this.jsonParser.toJson(testTask);

        this.manager.createTask(testTask2);

        HttpResponse<String> response = getPostResponse(testTaskJson,"http://localhost:8080/tasks/");
        // проверяем код ответа
        assertEquals(406, response.statusCode());

        List<Task> tasksFromManager = this.manager.getTasks();

        assertNotNull(tasksFromManager, "Tasks don`t return");
        assertEquals(1, tasksFromManager.size(), "Wrong task amount");
        assertEquals("Test2", tasksFromManager.getFirst().getName(), "Wrong task name");
    }

    @DisplayName("Should send 201 when the updateTask method called")
    @Test
    void shouldSend201When_updateTaskMethodCalled() throws IOException, InterruptedException {
            Task updatedTask = new Task("Test2", "Testing task",
                Status.DONE, TaskTypes.TASK, Duration.ofSeconds(42), Instant.EPOCH);
        String updatedTaskJson = this.jsonParser.toJson(updatedTask);

        this.manager.createTask(this.testTask);

        HttpResponse<String> response = getPostResponse(updatedTaskJson,"http://localhost:8080/tasks/1");
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = this.manager.getTasks();

        assertNotNull(tasksFromManager, "Tasks don`t return");
        assertEquals(1, tasksFromManager.size(), "Wrong task amount");
        assertEquals("Test2", tasksFromManager.getFirst().getName(), "Wrong task name");
    }

    @DisplayName("Should send 406 if an updated task overlapping another one")
    @Test
    void shouldSend406IfUpdatedTaskOverlappingAnotherOne() throws IOException, InterruptedException {
        Task testTask2 = new Task("Test2", "Testing task",
                Status.NEW, TaskTypes.TASK, Duration.ofSeconds(2), Instant.ofEpochSecond(43));
        Task updatedTask = new Task("Test3", "Testing task",
                Status.NEW, TaskTypes.TASK, Duration.ofSeconds(1), Instant.ofEpochSecond(44));
        String updatedTaskJson = this.jsonParser.toJson(updatedTask);

        this.manager.createTask(this.testTask);
        this.manager.createTask(testTask2);

        HttpResponse<String> response = getPostResponse(updatedTaskJson, "http://localhost:8080/tasks/1");

        assertEquals(406, response.statusCode());
    }

    @DisplayName("Should send 200 when the getTasks method called")
    @Test
    void shouldSend200When_getTasksMethodCalled() throws IOException, InterruptedException {
        HttpResponse<String> response = getGetResponse("http://localhost:8080/tasks/");

        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body());
    }

    @DisplayName("Should send 200 when the getTask method called")
    @Test
    void shouldSend200When_getTaskMethodCalled() throws IOException, InterruptedException {
        this.manager.createTask(this.testTask);
        HttpResponse<String> response = getGetResponse("http://localhost:8080/tasks/1");

        assertEquals(200, response.statusCode());
        assertEquals(this.testTask, this.jsonParser.fromJson(response.body(), Task.class));
    }

    @DisplayName("Should send 404 when the getTask method called and a task doesn`t exist")
    @Test
    void shouldSend200When_getTaskMethodCalledFndTaskDoesNotExist() throws IOException, InterruptedException {
        HttpResponse<String> response = getGetResponse("http://localhost:8080/tasks/1");

        assertEquals(404, response.statusCode());
    }

    @DisplayName("Should send 200 if the deleteTask method called")
    @Test
    void shouldSend200If_deleteTaskMethodCalled() throws IOException, InterruptedException {
        this.manager.createTask(this.testTask);

        HttpResponse<String> response = getDeleteResponse();

        assertEquals(200, response.statusCode());
        assertEquals(new ArrayList<>(), this.manager.getTasks());
    }

    private static HttpResponse<String> getDeleteResponse() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/1"))
                .DELETE()
                .build();
        return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    }

    @DisplayName("Should send 404 when the deleteTask method was called and a task doesn`t exist")
    @Test
    void shouldSend404When_deleteTaskMethodWasCalledAndTaskDoesNotExist() throws IOException, InterruptedException {
        HttpResponse<String> response = getDeleteResponse();

        assertEquals(404, response.statusCode());
    }

}