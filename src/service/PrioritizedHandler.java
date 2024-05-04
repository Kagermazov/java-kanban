package service;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    public PrioritizedHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void get(HttpExchange exchanger, String[] splitPath) throws IOException {
        super.sendResponse(exchanger,200, this.manager.getPrioritizedTasks());
    }
}
