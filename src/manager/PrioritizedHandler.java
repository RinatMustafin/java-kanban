package manager;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import tasks.Task;
import java.io.IOException;
import java.util.Set;

public class PrioritizedHandler extends BaseHttpHandler {
    private TaskManager taskManager;
    private Gson jsonMapper;

    public PrioritizedHandler(TaskManager taskManager, Gson jsonMapper) {
        this.taskManager = taskManager;
        this.jsonMapper = jsonMapper;
    }


    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String[] path = exchange.getRequestURI().getPath().split("/");
        if (method.equals("GET") && path.length == 2) {
            Set<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
            String json = jsonMapper.toJson(prioritizedTasks);
            sendText(exchange, json, 200);
        }
    }
}
