package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Task;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    private TaskManager taskManager;

    @BeforeEach
    public void init() {
        taskManager = Managers.getDefault();

    }

    @Test
    void createTask() {
        String name = "Написать тесты";
        String description = "Сделать ТЗ";
        Task task = new Task(null, name, description, Status.NEW);

        taskManager.createTask(task);

        Task actualTask = taskManager.findTaskById(task.getId());
        assertNotNull(actualTask.getId());
        assertEquals(actualTask.getStatus(), Status.NEW);
        assertEquals(actualTask.getDescription(), description);
        assertEquals(actualTask.getName(), name);
    }

    @Test
    void updateTask() {
        String name = "Написать тесты";
        String description = "Сделать ТЗ";
        Task task = new Task(null, name, description, Status.NEW);

        taskManager.createTask(task);
        assertNotNull(task.getId(), "Task ID не должен быть null");
        Task taskStatus = taskManager.findTaskById(task.getId());
        assertNotNull(taskStatus, "Task не должен быть null");
        taskStatus.setStatus(Status.DONE);
        taskManager.updateTask(taskStatus);

        Status StatusAfterUpdate = taskStatus.getStatus();
        assertEquals(StatusAfterUpdate, Status.DONE);
    }

    @Test
    void deleteTaskById() {
        String name = "Написать тесты";
        String description = "Сделать ТЗ";
        Task task1 = new Task(null, name, description, Status.NEW);
        String name2 = "Написать книгу";
        String description2 = "Сделать чернила";
        Task task2 = new Task(null, name2, description2, Status.NEW);


        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.deleteTaskById(task2.getId());
        String expected = String.format("[Tasks.Task{id=1, name='%s', description='%s', status=NEW}]", name, description);
        String actually = taskManager.getAllTasks().toString();

        assertEquals(expected, actually);
    }

    @Test
    void getAllTasks() {
        String name = "Написать тесты";
        String description = "Сделать ТЗ";
        Task task1 = new Task(null, name, description, Status.NEW);
        taskManager.createTask(task1);
        taskManager.getAllTasks();
        String expected = String.format("[Tasks.Task{id=1, name='%s', description='%s', status=NEW}]", name, description);
        String actually = taskManager.getAllTasks().toString();
        assertEquals(expected, actually);
    }

    @Test
    void deleteAllTasks() {
        String name = "Написать тесты";
        String description = "Сделать ТЗ";
        Task task1 = new Task(null, name, description, Status.NEW);
        taskManager.createTask(task1);
        taskManager.deleteAllTasks();
        assertTrue(taskManager.getAllTasks().isEmpty());
    }


    @Test
    void createEpic() {
        String name = "Приготовить ужин";
        String description = "Порезать картошку";
        Epic epic = new Epic(null, name, description);

        taskManager.createTask(epic);

        Task actualTask = taskManager.findTaskById(epic.getId());
        assertNotNull(actualTask.getId());
        assertEquals(actualTask.getDescription(), description);
        assertEquals(actualTask.getName(), name);
    }

    @Test
    void updateEpic() {
        String name = "Приготовить ужин";
        String description = "Порезать картошку";
        Epic epic = new Epic(null, name, description);

        taskManager.createEpic(epic);
        epic.setName("Завтрак");
        taskManager.updateEpic(epic);
        assertEquals("Завтрак", epic.getName());
    }

    @Test
    void deleteEpicById() {
        String name = "Написать тесты";
        String description = "Сделать ТЗ";
        Epic epic1 = new Epic(null, name, description);
        String name2 = "Написать книгу";
        String description2 = "Сделать чернила";
        Epic epic2 = new Epic(null, name2, description2);


        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.deleteEpicById(epic2.getId());
        String expected = String.format("[Tasks.Epic{subtaskIds=[]} Tasks.Task{id=1, name='%s', description='%s', status=NEW}]", name, description);
        String actually = taskManager.getAllEpics().toString();

        assertEquals(expected, actually);
    }

    @Test
    void getAllEpics() {
        String name = "Приготовить ужин";
        String description = "Порезать картошку";
        Epic epic = new Epic(null, name, description);

        taskManager.createEpic(epic);

        String expected = String.format("[Tasks.Task{id=1, name='%s', description='%s', status=NEW}]", name, description);
        String actually = taskManager.getAllEpics().toString();
    }

    @Test
    void deleteAllEpics() {
        String name = "Приготовить ужин";
        String description = "Порезать картошку";
        Epic epic = new Epic(null, name, description);

        taskManager.createEpic(epic);
        taskManager.deleteAllEpics();
        assertTrue(taskManager.getAllEpics().isEmpty());
    }


    @Test
    void taskInHistoryListShouldNotBeUpdatedAfterTaskUpdate() {
        String name = "Написать тесты";
        String description = "Сделать ТЗ";
        Task task = new Task(null, name, description, Status.NEW);

        taskManager.createTask(task);

        taskManager.findTaskById(task.getId());
        ArrayList<Task> history = taskManager.getHistory();
        Task taskInHistory = history.get(0);

        Status StatusInHistoryBeforeUpdate = taskInHistory.getStatus();
        task.setStatus(Status.DONE);
        taskManager.updateTask(task);

        Task StatusInHistoryAfterUpdate = taskManager.getHistory().get(0);
        assertEquals(StatusInHistoryBeforeUpdate, StatusInHistoryAfterUpdate.getStatus());

    }


}