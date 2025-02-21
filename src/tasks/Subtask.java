package tasks;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    int epicId;

    public Subtask(Integer id, String name, String description, int epicId, Status status, Duration duration, LocalDateTime startTime) {
        super(id, name, description, status, duration, startTime);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "Tasks.Subtask{" +
                "epicId=" + epicId +
                "} " + super.toString();
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }


}
