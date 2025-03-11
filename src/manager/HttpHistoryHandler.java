package manager;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import tasks.Task;

import java.io.IOException;
import java.util.ArrayList;

public class HttpHistoryHandler extends BaseHttpHandler {
    private TaskManager taskManager;
    private Gson jsonMapper;

    public HttpHistoryHandler(TaskManager taskManager, Gson jsonMapper) {
        this.taskManager = taskManager;
        this.jsonMapper = jsonMapper;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String[] path = exchange.getRequestURI().getPath().split("/");
        if (method.equals("GET") && path.length == 2) {
            ArrayList<Task> history = taskManager.getHistory();
            String json = jsonMapper.toJson(history);
            sendText(exchange, json, 200);
        }
    }
}
