package manager;

import app.exception.InvalidTimeException;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.Set;

public interface TaskManager {
    Task createTask(Task task) throws InvalidTimeException;

    Task updateTask(Task task);

    Task deleteTaskById(Integer id);

    ArrayList<Task> getAllTasks();

    void deleteAllTasks();

    Task findTaskById(Integer id);

    Epic createEpic(Epic epic) throws InvalidTimeException;

    Epic updateEpic(Epic epic);

    Epic deleteEpicById(Integer id);

    ArrayList<Epic> getAllEpics();

    void deleteAllEpics();

    Epic findEpicById(Integer id);

    Subtask createSubtask(Subtask subtask) throws InvalidTimeException;

    Subtask updateSubtask(Subtask subtask);

    Subtask deleteSubtaskById(Integer id);

    ArrayList<Subtask> getAllSubtasks();

    void deleteAllSubtasks();

    Subtask findSubtaskById(Integer id);

    ArrayList<Subtask> findSubtaskByEpicId(Integer idOfEpic);

    ArrayList<Task> getHistory();

    Set<Task> getPrioritizedTasks();

}
