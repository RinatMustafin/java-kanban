package app.exception;

public class FileManagerSaveException extends RuntimeException {
    public FileManagerSaveException(String message) {
        super(message);
    }
}
