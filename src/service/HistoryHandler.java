package service;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    public HistoryHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    protected void get(HttpExchange exchanger, String[] splitPath) throws IOException {
        super.sendResponse(exchanger, 200, this.manager.getHistory());
    }
}
