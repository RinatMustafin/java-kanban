package manager;


import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int counter = 0;
private HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public Task createTask(Task task) {
        int newId = nextId();
        task.setId(newId);
        tasks.put(task.getId(), task);

        return task;
    }

    @Override
    public Task updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            Task taskNew = tasks.get(task.getId());
            taskNew.setName(task.getName());
            taskNew.setDescription(task.getDescription());
        }
        return task;
    }

    @Override
    public Task deleteTaskById(Integer id) {
        return tasks.remove(id);
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public Task findTaskById(Integer id) {
        Task task = tasks.get(id);
        Task taskForHistory = new Task(task.getId(), task.getName(), task.getDescription(), task.getStatus());
        historyManager.addToHistory(taskForHistory);
        return tasks.get(id);
    }


    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(historyManager.getHistory());
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
                subtasks.remove(subtaskId);
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

        if (epics.containsKey(epicId)) {
            int newId = nextId();
            subtask.setId(newId);

            Epic epic = epics.get(epicId);
            epic.addSubtaskId(subtask.getId());

            subtasks.put(subtask.getId(), subtask);
            makeEpicStatus(epics.get(subtask.getEpicId()));
        } else {
            System.out.println("Такого эпика нет, " + epicId + "введите новый эпик");
        }

        return subtask;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            Task task1 = tasks.get(subtask.getId());
            task1.setName(subtask.getName());
            task1.setDescription(subtask.getDescription());
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
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteAllSubtasks() {
        for (Integer subtaskId : subtasks.keySet()) {
            deleteSubtaskById(subtaskId);
        }
    }

    @Override
    public Subtask findSubtaskById(Integer id) {
        return subtasks.get(id);
    }

    @Override
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
