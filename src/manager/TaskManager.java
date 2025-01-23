package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;

public interface TaskManager {
    Task createTask(Task task);

    Task updateTask(Task task);

    Task deleteTaskById(Integer id);

    ArrayList<Task> getAllTasks();

    void deleteAllTasks();

    Task findTaskById(Integer id);

    Epic createEpic(Epic epic);

    Task updateEpic(Epic epic);

    void deleteEpicById(Integer id);

    ArrayList<Epic> getAllEpics();

    void deleteAllEpics();

    Epic findEpicById(Integer id);

    Subtask createSubtask(Subtask subtask);

    Subtask updateSubtask(Subtask subtask);

    void deleteSubtaskById(Integer id);

    ArrayList<Subtask> getAllSubtasks();

    void deleteAllSubtasks();

    Subtask findSubtaskById(Integer id);

    ArrayList<Subtask> findSubtaskByEpicId(Integer idOfEpic);


    ArrayList<Task> getHistory();


}
