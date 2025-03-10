package app.exception;

public class TaskNotFoundExсeption extends RuntimeException {
    public TaskNotFoundExсeption(String message) {
        super(message);
    }
}
