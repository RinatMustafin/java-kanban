package tasks;

public enum TaskType {
    TASK(Task.class),
    EPIC(Epic.class),
    SUBTASK(Subtask.class);

    private final Class<? extends Task> type;

    TaskType(Class<? extends Task> type) {
        this.type = type;
    }

    public Class<? extends Task> getType() {
        return type;
    }

}
