package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Integer> subtaskIds = new ArrayList<>();

    public Epic(Integer id, String name, String description) {
        super(id, name, description, Status.NEW, Duration.ofMinutes(0), LocalDateTime.now());
    }

    public Epic(int id, String name, String description, Duration duration, LocalDateTime startTime) {
        super(id, name, description, Status.NEW, duration, startTime);
    }

    @Override
    public String toString() {
        return "Tasks.Epic{" +
                "subtaskIds=" + subtaskIds +
                "} " + super.toString();
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    public ArrayList<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void setSubtaskIds(ArrayList<Integer> subtaskIds) {
        this.subtaskIds = subtaskIds;
    }

    public void addSubtaskId(Integer subtaskId) {
        subtaskIds.add(subtaskId);
    }

    public void deleteSubtaskId(Integer subtaskId) {
        subtaskIds.remove(subtaskId);
    }



}
