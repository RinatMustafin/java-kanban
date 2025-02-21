package manager;


import app.exception.InvalidTimeException;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected HashMap<Integer, Task> tasks = new HashMap<>();
    protected HashMap<Integer, Epic> epics = new HashMap<>();
    protected HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int counter = 0;
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private final TreeSet<Task> prioritizedTasks = new TreeSet<>(
            Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder()))
    );

    @Override
    public Task createTask(Task task) {
        int newId = nextId();
        task.setId(newId);

        try {
            checkTime(task);
        } catch (InvalidTimeException e) {
            System.err.println(e.getMessage());
        }

        tasks.put(task.getId(), task);
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
        return task;
    }

    @Override
    public Task updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            Task taskNew = tasks.get(task.getId());
            taskNew.setName(task.getName());
            taskNew.setDescription(task.getDescription());
            prioritizedTasks.remove(taskNew);
            if (taskNew.getStartTime() != null) {
                prioritizedTasks.add(taskNew);
            }
            try {
                checkTime(task);
            } catch (InvalidTimeException e) {
                System.out.println(e.getMessage());
            }
        }
        return task;
    }

    @Override
    public Task deleteTaskById(Integer id) {
        Task task = tasks.remove(id);
        if (task != null) {
            prioritizedTasks.remove(task); // Удаляем задачу из TreeSet
        }
        return task;
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void deleteAllTasks() {
        for (Task task : tasks.values()) {
            prioritizedTasks.remove(task);
        }
        tasks.clear();
    }

    @Override
    public Task findTaskById(Integer id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.addToHistory(task);
        }
        return task;
    }


    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(historyManager.getHistory());
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        return new TreeSet<>(prioritizedTasks); // Возвращаем копию TreeSet
    }

    private int nextId() {
        return ++counter;
    }

    @Override
    public Epic createEpic(Epic epic) {
        int newId = nextId();
        epic.setId(newId);
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public Task updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            Epic epic1 = epics.get(epic.getId());
            epic1.setName(epic.getName());
            epic1.setDescription(epic.getDescription());
        }
        return epic;
    }

    @Override
    public void deleteEpicById(Integer id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            for (int subtaskId : epic.getSubtaskIds()) {
                Subtask subtask = subtasks.remove(subtaskId);
                prioritizedTasks.remove(subtask);
            }
        }
        epics.remove(id);
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteAllEpics() {
        for (Epic epic : epics.values()) {
            for (int subtaskId : epic.getSubtaskIds()) {
                prioritizedTasks.remove(subtasks.get(subtaskId));
            }
        }
        epics.clear();
        deleteAllSubtasks();
    }

    @Override
    public Epic findEpicById(Integer id) {
        return epics.get(id);
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        int epicId = subtask.getEpicId();

        try {
            checkTime(subtask);
        } catch (InvalidTimeException e) {
            System.out.println(e.getMessage());
        }

        if (epics.containsKey(epicId)) {
            int newId = nextId();
            subtask.setId(newId);

            Epic epic = epics.get(epicId);
            epic.addSubtaskId(subtask.getId());

            subtasks.put(subtask.getId(), subtask);
            makeEpicStatus(epics.get(subtask.getEpicId()));
            updateEpicTime(epic);
            if (subtask.getStartTime() != null) {
                prioritizedTasks.add(subtask);
            }
        } else {
            System.out.println("Такого эпика нет, " + epicId + "введите новый эпик");
        }

        return subtask;
    }

    private void updateEpicTime(Epic epic) {
        LocalDateTime localDateTime = getMinimalTime(epic);
        long duration = calculateEpicDuration(epic.getSubtaskIds());
        epic.setStartTime(localDateTime);
        epic.setDuration(Duration.ofMinutes(duration));
        epic.setEndTime(epic.getStartTime().plus(epic.getDuration()));
    }

    private long calculateEpicDuration(ArrayList<Integer> subtaskIds) {
        return subtaskIds.stream()
                .map(subtasks::get)
                .map(subtask -> subtask.getDuration().toMinutes())
                .reduce(0L, Long::sum);
    }

    private LocalDateTime getMinimalTime(Epic epic) {
        return epic.getSubtaskIds().stream()
                .map(subtasks::get)
                .map(Task::getStartTime)
                .sorted(Comparator.naturalOrder())
                .limit(1)
                .toList()
                .getFirst();
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        try {
            checkTime(subtask);
        } catch (InvalidTimeException e) {
            System.out.println(e.getMessage());
        }
        if (subtasks.containsKey(subtask.getId())) {
            Subtask subtaskNew = subtasks.get(subtask.getId());
            subtaskNew.setName(subtask.getName());
            subtaskNew.setDescription(subtask.getDescription());
            prioritizedTasks.remove(subtaskNew);
            if (subtaskNew.getStartTime() != null) {
                prioritizedTasks.add(subtaskNew);
            }
            updateEpicTime(epics.get(subtask.getEpicId()));
        }
        return subtask;
    }

    @Override
    public void deleteSubtaskById(Integer id) {
        Subtask subtask = subtasks.get(id);
        Integer epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);
        epic.deleteSubtaskId(id);
        subtasks.remove(id);
        prioritizedTasks.remove(subtask);
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteAllSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            prioritizedTasks.remove(subtask);
        }
        subtasks.clear();
    }

    @Override
    public Subtask findSubtaskById(Integer id) {
        return subtasks.get(id);
    }

    @Override
    public ArrayList<Subtask> findSubtaskByEpicId(Integer idOfEpic) {
        Epic epic = epics.get(idOfEpic);
        ArrayList<Integer> idsOfSubtasks = epic.getSubtaskIds();
        ArrayList<Subtask> resultOfSubtasks = idsOfSubtasks.stream().map(this::findSubtaskById).collect(Collectors.toCollection(ArrayList::new));
        return resultOfSubtasks;
    }

    private void checkTime(Task task) throws InvalidTimeException {
        if (task.getStartTime() == null || task.getEndTime() == null) {
            return;
        }

        List<Integer> buffer = prioritizedTasks.stream()
                .filter(task1 -> !Objects.equals(task1.getId(), task.getId()))
                .filter(task1 -> task1.getStartTime() != null && task1.getEndTime() != null)
                .filter(task1 -> task1.getEndTime().isAfter(task.getStartTime()))
                .filter(task1 -> task1.getStartTime().isBefore(task.getEndTime()))
                .map(Task::getId)
                .toList();

        if (!buffer.isEmpty()) {
            throw new InvalidTimeException("Задача " + task.getId() + " пересекается с " + buffer);
        }
    }

    private void makeEpicStatus(Epic epic) {
        if (epic.getSubtaskIds().isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        boolean allTasksIsNew = true;
        boolean allTasksIsDone = true;

        ArrayList<Subtask> epicSubtasks = findSubtaskByEpicId(epic.getId());

        for (Subtask subtask : epicSubtasks) {
            if (subtask == null) {
                continue; // Пропускаем null подзадачи
            }

            if (subtask.getStatus() != Status.NEW) {
                allTasksIsNew = false;
            }

            if (subtask.getStatus() != Status.DONE) {
                allTasksIsDone = false;
            }
        }

        if (allTasksIsDone) {
            epic.setStatus(Status.DONE);
        } else if (allTasksIsNew) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

}
