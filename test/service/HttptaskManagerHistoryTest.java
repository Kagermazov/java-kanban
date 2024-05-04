package service;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttptaskManagerHistoryTest {
    TaskManager manager;
    HttpTaskServer server;
    Gson jsonParser;

    public HttptaskManagerHistoryTest() throws IOException {
        this.manager = new InMemoryTaskManager();
        this.server = new HttpTaskServer(this.manager);
        this.jsonParser = HttpTaskServer.getJsonParser();
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

    @DisplayName("Should send 200 when the getHistory method called")
    @Test
    void shouldSend200When_getHistoryMethodCalled() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8080/history"))
                .GET()
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body());
    }
}
