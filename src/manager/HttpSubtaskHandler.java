package manager;

import app.exception.ErrorResponse;
import app.exception.InvalidTimeException;
import app.exception.TaskNotFoundException;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import tasks.Subtask;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;

public class HttpSubtaskHandler extends BaseHttpHandler {
    private TaskManager subtaskManager;
    private Gson jsonMapper;

    public HttpSubtaskHandler(TaskManager subtaskManager, Gson jsonMapper) {
        this.subtaskManager = subtaskManager;
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
        } catch (TaskNotFoundException e) {
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
        Subtask deleteSubtaskById = subtaskManager.deleteSubtaskById(id);
        String json = jsonMapper.toJson(deleteSubtaskById);
        sendText(exchange, json, 200);
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        byte[] body = exchange.getRequestBody().readAllBytes();
        String bodyString = new String(body, StandardCharsets.UTF_8);
        Subtask subtask = jsonMapper.fromJson(bodyString, Subtask.class);
        try {
            if (Objects.isNull(subtask.getId())) {
                Subtask createdSubtask = subtaskManager.createSubtask(subtask);
                String json = jsonMapper.toJson(createdSubtask);
                sendText(exchange, json, 201);
            } else {
                Subtask updateSubtask = subtaskManager.updateSubtask(subtask);
                String json = jsonMapper.toJson(updateSubtask);
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
            Subtask subtaskById = subtaskManager.findSubtaskById(id);
            String json = jsonMapper.toJson(subtaskById);
            sendText(exchange, json, 200);
        }

        if (urlParts.length == 2) {
            //получение всех задач
            ArrayList<Subtask> allSubtasks = subtaskManager.getAllSubtasks();
            String json = jsonMapper.toJson(allSubtasks);
            sendText(exchange, json, 200);
        }
    }
}

