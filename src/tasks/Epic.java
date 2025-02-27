package tasks;

import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Integer> subtaskIds = new ArrayList<>();

    public Epic(Integer id, String name, String description) {
        super(id, name, description, Status.NEW);
    }

    @Override
    public String toString() {
        return "Tasks.Epic{" +
                "subtaskIds=" + subtaskIds +
                "} " + super.toString();
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
