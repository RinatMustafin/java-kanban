package manager;

import adapter.DurationAdapter;
import adapter.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    InetSocketAddress address = new InetSocketAddress("localhost", 8080);
    HttpServer httpServer = HttpServer.create(address, 0);

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        Gson jsonMapper = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
        httpServer.createContext("/tasks", new HttpTaskHandler(taskManager, jsonMapper));
        httpServer.createContext("/subtasks", new HttpSubtaskHandler(taskManager, jsonMapper));
        httpServer.createContext("/epic", new HttpEpicHandler(taskManager, jsonMapper));
        httpServer.createContext("/history", new HttpHistoryHandler(taskManager, jsonMapper));
        httpServer.createContext("/priority", new PrioritizedHandler(taskManager, jsonMapper));
    }

    public void start() {
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(1);
    }
}
