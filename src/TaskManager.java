import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int counter = 0;

    public Task createTask(Task task) {
        int newId = nextId();
        task.setId(newId);
        tasks.put(task.getId(), task);

        return task;
    }

    public Task updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            Task task1 = tasks.get(task.getId());
            task1.setName(task.getName());
            task1.setDescription(task.getDescription());
        }
        return task;
    }

    public Task deleteTaskById(Integer id) {
        return tasks.remove(id);
    }

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public Task findTaskById(Integer id) {
        return tasks.get(id);
    }

    private int nextId() {
        return ++counter;
    }

    public Epic createEpic(Epic epic) {
        int newId = nextId();
        epic.setId(newId);
        epics.put(epic.getId(), epic);
        return epic;
    }

    public Task updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            Epic epic1 = epics.get(epic.getId());
            epic1.setName(epic.getName());
            epic1.setDescription(epic.getDescription());
        }
        return epic;
    }

    public void deleteEpicById(Integer id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            for (int subtaskId : epic.getSubtaskIds()) {
                subtasks.remove(subtaskId);
            }
        }
        epics.remove(id);
    }

    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public void deleteAllEpics() {
        epics.clear();
        deleteAllSubtasks();
    }

    public Epic findEpicById(Integer id) {
        return epics.get(id);
    }

    public Subtask createSubtask(Subtask subtask) {
        int epicId = subtask.getEpicId();

        if (epics.containsKey(epicId)) {
            int newId = nextId();
            subtask.setId(newId);

            Epic epic = epics.get(epicId);
            epic.addSubtaskId(subtask.getId());

            subtasks.put(subtask.getId(), subtask);
            makeEpicStatus(epics.get(subtask.getEpicId()));
        } else {
            System.out.println("Такого эпика нет, введите новый эпик");
        }

        return subtask;
    }

    public Subtask updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            Task task1 = tasks.get(subtask.getId());
            task1.setName(subtask.getName());
            task1.setDescription(subtask.getDescription());
        }
        return subtask;
    }

    public void deleteSubtaskById(Integer id) {
        Subtask subtask = subtasks.get(id);
        Integer epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);
        epic.deleteSubtaskId(id);
        subtasks.remove(id);
    }

    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public void deleteAllSubtasks() {
        for (Integer subtaskId : subtasks.keySet()) {
            deleteSubtaskById(subtaskId);
        }
    }

    public Subtask findSubtaskById(Integer id) {
        return subtasks.get(id);
    }

    public ArrayList<Subtask> findSubtaskByEpicId(Integer idOfEpic) {
        Epic epic = epics.get(idOfEpic);
        ArrayList<Integer> idsOfSubtasks = epic.getSubtaskIds();
        ArrayList<Subtask> resultOfSubtasks = new ArrayList<>();
        for (Integer idOfSubtask : idsOfSubtasks) {
            Subtask subtask = findSubtaskById(idOfSubtask);
            resultOfSubtasks.add(subtask);
        }
        return resultOfSubtasks;
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
