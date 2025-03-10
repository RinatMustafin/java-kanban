package manager;

import app.exception.ErrorResponse;
import app.exception.FileManagerSaveException;
import app.exception.InvalidTimeException;
import app.exception.TaskNotFoundExсeption;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;

public class HttpTaskHandler extends BaseHttpHandler {
    private TaskManager taskManager;
    private Gson jsonMapper;

    public HttpTaskHandler(TaskManager taskManager, Gson jsonMapper) {
        this.taskManager = taskManager;
        this.jsonMapper = jsonMapper;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String method = exchange.getRequestMethod();
        try {
            switch (method) {
                case "GET":
                    handleGet(exchange);
                case "POST":
                    handlePost(exchange);
                case "DELETE":
                    handleDelete(exchange);
                default:
                    ErrorResponse errorResponse = new ErrorResponse(String.format("Обработка метода не предусмотрена: %s", method), 405, exchange.getRequestURI().getPath());
                    String jsonText = jsonMapper.toJson(errorResponse);
                    sendText(exchange, jsonText, errorResponse.getErrorCode());
            }
        } catch (TaskNotFoundExсeption e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), 404, exchange.getRequestURI().getPath());
            String jsonText = jsonMapper.toJson(errorResponse);
            sendText(exchange, jsonText, errorResponse.getErrorCode());

        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), 500, exchange.getRequestURI().getPath());
            String jsonText = jsonMapper.toJson(errorResponse);
            sendText(exchange, jsonText, errorResponse.getErrorCode());
        } finally {
            exchange.close();
        }
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        URI requestURI = exchange.getRequestURI();
        String path = requestURI.getPath();
        String[] urlParts = path.split("/");
        Integer id = Integer.valueOf(urlParts[2]);
        Task deletedTaskById = taskManager.deleteTaskById(id);
        String json = jsonMapper.toJson(deletedTaskById);
        sendText(exchange, json, 200);
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        byte[] body = exchange.getRequestBody().readAllBytes();
        String bodyString = new String(body, StandardCharsets.UTF_8);

        Task task = jsonMapper.fromJson(bodyString, Task.class);
        try {
            if (Objects.isNull(task.getId())) {
                Task createdTask = taskManager.createTask(task);
                String json = jsonMapper.toJson(createdTask);
                sendText(exchange, json, 201);
            } else {
                Task updateTask = taskManager.updateTask(task);
                String json = jsonMapper.toJson(updateTask);
                sendText(exchange, json, 201);
            }
        } catch (InvalidTimeException e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), 406, exchange.getRequestURI().getPath());
            String jsonText = jsonMapper.toJson(errorResponse);
            sendText(exchange, jsonText, errorResponse.getErrorCode());
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        URI requestURI = exchange.getRequestURI();
        String path = requestURI.getPath();
        String[] urlParts = path.split("/");

        if (urlParts.length == 3) {
            //получение по ID
            Integer id = Integer.valueOf(urlParts[2]);
            Task taskById = taskManager.findTaskById(id);
            String json = jsonMapper.toJson(taskById);
            sendText(exchange, json, 200);
        }

        if (urlParts.length == 2) {
            //получение всех задач
            ArrayList<Task> allTasks = taskManager.getAllTasks();
            String json = jsonMapper.toJson(allTasks);
            sendText(exchange, json, 200);
        }
    }
}
