package manager;

import adapter.DurationAdapter;
import adapter.LocalDateTimeAdapter;
import app.exception.InvalidTimeException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerTasksTest {
    private HttpTaskServer taskServer;
    private InMemoryTaskManager taskManager;
    private Gson gson;

    @BeforeEach
    public void setUp() throws IOException {
        taskManager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(taskManager);
        taskServer.start();
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
    }

    @AfterEach
    public void tearDown() {
        taskServer.stop();
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        // Создаем задачу
        Task task = new Task(null, "Test Task", "Description", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        String taskJson = gson.toJson(task);

        // Отправляем POST-запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем статус ответа
        assertEquals(201, response.statusCode());

        // Проверяем, что задача добавилась в менеджер
        assertEquals(1, taskManager.getAllTasks().size());
        assertEquals("Test Task", taskManager.getAllTasks().get(0).getName());
    }

    @Test
    public void testGetTaskById() throws IOException, InterruptedException, InvalidTimeException {
        // Создаем задачу
        Task task = new Task(null,"Test Task", "Description", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        taskManager.createTask(task);

        // Отправляем GET-запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + task.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем статус ответа
        assertEquals(200, response.statusCode());

        // Проверяем, что задача возвращается корректно
        Task returnedTask = gson.fromJson(response.body(), Task.class);
        assertEquals(task.getId(), returnedTask.getId());
        assertEquals(task.getName(), returnedTask.getName());
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException, InvalidTimeException {
        // Создаем задачу
        Task task = new Task(null,"Test Task", "Description", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        taskManager.createTask(task);

        // Отправляем DELETE-запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + task.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        assertEquals(0, taskManager.getAllTasks().size());
    }
    @Test
    public void testAddSubtask() throws IOException, InterruptedException {
        // Создаем эпик и подзадачу
        Epic epic = new Epic(null, "Epic", "Description");
        taskManager.createEpic((Epic) epic);

        Subtask subtask = new Subtask(null,"Subtask", "Description", epic.getId(), Status.NEW, Duration.ofMinutes(15), LocalDateTime.now().plusHours(1));
        String subtaskJson = gson.toJson(subtask);

        // Отправляем POST-запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем статус ответа
        assertEquals(201, response.statusCode());

        // Проверяем, что подзадача добавилась в менеджер
        assertEquals(1, taskManager.getAllSubtasks().size());
        assertEquals("Subtask", taskManager.getAllSubtasks().get(0).getName());
    }
    @Test
    public void testGetHistory() throws IOException, InterruptedException, InvalidTimeException {
        // Создаем задачу и добавляем её в историю
        Task task = new Task(null, "Test Task", "Description", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        taskManager.createTask(task);
        taskManager.findTaskById(task.getId());

        // Отправляем GET-запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем статус ответа
        assertEquals(200, response.statusCode());

        // Проверяем, что история возвращается корректно
        assertNotNull(response.body());
        assertTrue(response.body().contains("Test Task"));
    }
}