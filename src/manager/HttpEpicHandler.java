package manager;

import app.exception.ErrorResponse;
import app.exception.InvalidTimeException;
import app.exception.TaskNotFoundExсeption;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import tasks.Epic;
import tasks.Subtask;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class HttpEpicHandler extends BaseHttpHandler {
    private TaskManager taskManager;
    private Gson jsonMapper;

    public HttpEpicHandler(TaskManager taskManager, Gson jsonMapper) {
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
        Epic deleteEpicById = taskManager.deleteEpicById(id);
        String json = jsonMapper.toJson(deleteEpicById);
        sendText(exchange, json, 200);
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        byte[] body = exchange.getRequestBody().readAllBytes();
        String bodyString = new String(body, StandardCharsets.UTF_8);
        Epic epic = jsonMapper.fromJson(bodyString, Epic.class);

        if (epic.getDuration() == null) {
            epic.setDuration(Duration.ZERO);
        }
        if (epic.getStartTime() == null) {
            epic.setStartTime(LocalDateTime.now());
            epic.setEndTime(LocalDateTime.now());
        }


        try {
            if (Objects.isNull(epic.getId())) {
                Epic createdEpic = taskManager.createEpic(epic);
                String json = jsonMapper.toJson(createdEpic);
                sendText(exchange, json, 201);
            } else {
                Epic updateEpic = taskManager.updateEpic(epic);
                String json = jsonMapper.toJson(updateEpic);
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
            Epic epicById = taskManager.findEpicById(id);
            String json = jsonMapper.toJson(epicById);
            sendText(exchange, json, 200);
        }

        if (urlParts.length == 2) {
            //получение всех задач
            ArrayList<Epic> allEpics = taskManager.getAllEpics();
            String json = jsonMapper.toJson(allEpics);
            sendText(exchange, json, 200);
        }
        if (urlParts.length == 4) {
            //получение по ID
            Integer id = Integer.valueOf(urlParts[2]);
            ArrayList<Subtask> subtaskByEpicId = taskManager.findSubtaskByEpicId(id);
            String json = jsonMapper.toJson(subtaskByEpicId);
            sendText(exchange, json, 200);
        }
    }
}
