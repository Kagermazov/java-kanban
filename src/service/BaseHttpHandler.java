package service;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;


class BaseHttpHandler implements HttpHandler {
    TaskManager manager;

    protected BaseHttpHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchanger) throws IOException {
        try {
            String method = exchanger.getRequestMethod();
            String body = new String(exchanger.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            String[] splitPath = exchanger.getRequestURI().getPath().split("/");

            switch (method) {
                case "GET" -> get(exchanger, splitPath);
                case "POST" -> post(body, exchanger, splitPath);
                case "DELETE" -> delete(exchanger, splitPath);
                default -> sendResponse(exchanger, 400, "No such request method");
            }
        } catch (Exception e) {
            sendResponse(exchanger, 500, "Internal server error");
        }
    }

    protected void delete(HttpExchange exchanger, String[] splitPath) throws IOException {
//        the method is overrided by subclasses
    }

    protected void post(String body, HttpExchange exchanger, String[] splitPath) throws IOException {
//        the method is overrided by subclasses
    }

    protected void get(HttpExchange exchanger, String[] splitPath) throws IOException {
//        the method is overrided by subclasses
    }

    protected void sendResponse(HttpExchange exchanger, int rCode, Object payload) throws IOException {
        exchanger.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchanger.sendResponseHeaders(rCode, 0);

        try (OutputStream os = exchanger.getResponseBody()) {
            os.write(HttpTaskServer.getJsonParser().toJson(payload).getBytes());
        }
    }
}
