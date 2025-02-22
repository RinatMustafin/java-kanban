package manager;

import app.exception.InvalidTimeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;

    protected abstract T createTaskManager();

    @BeforeEach
    public void setUp() throws IOException {
        taskManager = createTaskManager();
    }

    public static class TestHelper {

        public static Task createTask(String name, String description, Status status, Duration duration) {
            return new Task(null, name, description, status, duration, LocalDateTime.now());
        }

        public static Epic createEpic(String name, String description) {
            return new Epic(null, name, description);
        }

        public static Subtask createSubtask(String name, String description, Epic epic, Status status, Duration duration) {
            return new Subtask(null, name, description, epic.getId(), status, duration, LocalDateTime.now());
        }
    }

    @Test
    public void testCreateTask() {
        Task task = TestHelper.createTask("Уборка", "Помыть полы", Status.NEW, Duration.ofMinutes(30));
        Task createdTask = taskManager.createTask(task);

        assertNotNull(createdTask.getId());
        assertEquals(task, createdTask);
    }

    @Test
    public void testUpdateTask() {
        Task task = TestHelper.createTask("Уборка", "Помыть полы", Status.NEW, Duration.ofMinutes(30));
        Task createdTask = taskManager.createTask(task);

        createdTask.setStatus(Status.IN_PROGRESS);
        Task updatedTask = taskManager.updateTask(createdTask);

        assertEquals(Status.IN_PROGRESS, updatedTask.getStatus());
    }

    @Test
    public void testDeleteTaskById() {
        Task task = TestHelper.createTask("Уборка", "Помыть полы", Status.NEW, Duration.ofMinutes(30));
        Task createdTask = taskManager.createTask(task);

        taskManager.deleteTaskById(createdTask.getId());
        assertNull(taskManager.findTaskById(createdTask.getId()));
    }

    @Test
    public void testGetAllTasks() {
        Task task1 = TestHelper.createTask("Уборка", "Помыть полы", Status.NEW, Duration.ofMinutes(30));
        Task task2 = TestHelper.createTask("Чтение", "Книга", Status.NEW, Duration.ofMinutes(30));

        taskManager.createTask(task1);
        taskManager.createTask(task2);

        assertEquals(2, taskManager.getAllTasks().size());
    }

    @Test
    public void testDeleteAllTasks() {
        Task task1 = TestHelper.createTask("Уборка", "Помыть полы", Status.NEW, Duration.ofMinutes(30));
        Task task2 = TestHelper.createTask("Чтение", "Книга", Status.NEW, Duration.ofMinutes(30));

        taskManager.createTask(task1);
        taskManager.createTask(task2);

        taskManager.deleteAllTasks();
        assertEquals(0, taskManager.getAllTasks().size());
    }

    @Test
    public void testFindTaskById() {
        Task task = TestHelper.createTask("Уборка", "Помыть полы", Status.NEW, Duration.ofMinutes(30));
        Task createdTask = taskManager.createTask(task);

        Task foundTask = taskManager.findTaskById(createdTask.getId());
        assertEquals(createdTask, foundTask);
    }

    @Test
    public void testCreateEpic() {
        Epic epic = TestHelper.createEpic("Уборка", "Дом");
        Epic createdEpic = taskManager.createEpic(epic);

        assertNotNull(createdEpic.getId());
        assertEquals(epic, createdEpic);
    }

    @Test
    public void testUpdateEpic() {
        Epic epic = TestHelper.createEpic("Уборка", "Дом");
        Epic createdEpic = taskManager.createEpic(epic);

        createdEpic.setName("Генеральная уборка");
        Epic updatedEpic = (Epic) taskManager.updateEpic(createdEpic);

        assertEquals("Генеральная уборка", updatedEpic.getName());
    }

    @Test
    public void testDeleteEpicById() {
        Epic epic = TestHelper.createEpic("Уборка", "Дом");
        Epic createdEpic = taskManager.createEpic(epic);

        taskManager.deleteEpicById(createdEpic.getId());
        assertNull(taskManager.findEpicById(createdEpic.getId()));
    }

    @Test
    public void testGetAllEpics() {
        Epic epic1 = TestHelper.createEpic("Уборка", "Дом");
        Epic epic2 = TestHelper.createEpic("Чтение", "Книга");

        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        assertEquals(2, taskManager.getAllEpics().size());
    }

    @Test
    public void testDeleteAllEpics() {
        Epic epic1 = TestHelper.createEpic("Уборка", "Дом");
        Epic epic2 = TestHelper.createEpic("Чтение", "Книга");

        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        taskManager.deleteAllEpics();
        assertEquals(0, taskManager.getAllEpics().size());
    }

    @Test
    public void testFindEpicById() {
        Epic epic = TestHelper.createEpic("Уборка", "Дом");
        Epic createdEpic = taskManager.createEpic(epic);

        Epic foundEpic = taskManager.findEpicById(createdEpic.getId());
        assertEquals(createdEpic, foundEpic);
    }

    @Test
    public void testCreateSubtask() {
        Epic epic = TestHelper.createEpic("Уборка", "Дом");
        Epic createdEpic = taskManager.createEpic(epic);

        Subtask subtask = TestHelper.createSubtask("Мытье", "Мытье полов", createdEpic, Status.NEW, Duration.ofMinutes(30));
        Subtask createdSubtask = taskManager.createSubtask(subtask);

        assertNotNull(createdSubtask, "Подзадача не была создана");
        assertNotNull(createdSubtask.getId(), "ID подзадачи не был установлен");
        assertEquals(subtask, createdSubtask, "Созданная подзадача не соответствует ожидаемой");
    }

    @Test
    public void testUpdateSubtask() {
        Epic epic = new Epic(null, "Уборка", "Дом");
        Epic createdEpic = taskManager.createEpic(epic);

        Subtask subtask = TestHelper.createSubtask("Мытье", "Мытье полов", createdEpic, Status.NEW, Duration.ofMinutes(30));
        Subtask createdSubtask = taskManager.createSubtask(subtask);

        createdSubtask.setStatus(Status.IN_PROGRESS);
        Subtask updatedSubtask = taskManager.updateSubtask(createdSubtask);

        assertEquals(Status.IN_PROGRESS, updatedSubtask.getStatus());
    }

    @Test
    public void testDeleteSubtaskById() {
        Epic epic = new Epic(null, "Уборка", "Дом");
        Epic createdEpic = taskManager.createEpic(epic);

        Subtask subtask = TestHelper.createSubtask("Мытье", "Мытье полов", createdEpic, Status.NEW, Duration.ofMinutes(30));
        Subtask createdSubtask = taskManager.createSubtask(subtask);

        taskManager.deleteSubtaskById(createdSubtask.getId());
        assertNull(taskManager.findSubtaskById(createdSubtask.getId()));
    }

    @Test
    public void testGetAllSubtasks() {
        Epic epic = new Epic(null, "Уборка", "Дом");
        Epic createdEpic = taskManager.createEpic(epic);

        Subtask subtask1 = TestHelper.createSubtask("Мытье", "Мытье полов", createdEpic, Status.NEW, Duration.ofMinutes(30));
        Subtask subtask2 = TestHelper.createSubtask("Чистка", "Чистка ковра", createdEpic, Status.NEW, Duration.ofMinutes(30));

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        assertEquals(2, taskManager.getAllSubtasks().size());
    }

    @Test
    public void testDeleteAllSubtasks() {
        Epic epic = new Epic(null, "Уборка", "Дом");
        Epic createdEpic = taskManager.createEpic(epic);

        Subtask subtask1 = TestHelper.createSubtask("Мытье", "Мытье полов", createdEpic, Status.NEW, Duration.ofMinutes(30));
        Subtask subtask2 = TestHelper.createSubtask("Чистка", "Чистка ковра", createdEpic, Status.NEW, Duration.ofMinutes(30));

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        taskManager.deleteAllSubtasks();
        assertEquals(0, taskManager.getAllSubtasks().size());
    }

    @Test
    public void testFindSubtaskById() {
        Epic epic = new Epic(null, "Уборка", "Дом");
        Epic createdEpic = taskManager.createEpic(epic);

        Subtask subtask = TestHelper.createSubtask("Мытье", "Мытье полов", createdEpic, Status.NEW, Duration.ofMinutes(30));
        Subtask createdSubtask = taskManager.createSubtask(subtask);

        Subtask foundSubtask = taskManager.findSubtaskById(createdSubtask.getId());
        assertEquals(createdSubtask, foundSubtask);
    }

    @Test
    public void testFindSubtaskByEpicId() {
        Epic epic = new Epic(null, "Уборка", "Дом");
        Epic createdEpic = taskManager.createEpic(epic);

        Subtask subtask1 = TestHelper.createSubtask("Мытье", "Мытье полов", createdEpic, Status.NEW, Duration.ofMinutes(30));
        Subtask subtask2 = TestHelper.createSubtask("Чистка", "Чистка ковра", createdEpic, Status.NEW, Duration.ofMinutes(30));
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        assertEquals(2, taskManager.findSubtaskByEpicId(createdEpic.getId()).size());
    }

    @Test
    public void testGetHistory() {
        Task task = TestHelper.createTask("Уборка", "Дом", Status.NEW, Duration.ofMinutes(30));
        Task createdTask = taskManager.createTask(task);

        taskManager.findTaskById(createdTask.getId());
        assertEquals(1, taskManager.getHistory().size());
    }

    @Test
    public void testGetPrioritizedTasks() {
        Task task1 = TestHelper.createTask("Мытье", "Пол", Status.NEW, Duration.ofMinutes(30));
        Task task2 = new Task(null, "Читска", "Ковер", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now().plusHours(1));

        taskManager.createTask(task1);
        taskManager.createTask(task2);

        assertEquals(2, taskManager.getPrioritizedTasks().size());
    }

    @Test
    public void testEpicStatusAllNew() {
        Epic epic = TestHelper.createEpic("Чистка", "Ковер");
        Epic createdEpic = taskManager.createEpic(epic);

        Subtask subtask1 = TestHelper.createSubtask("Вынести", "Вынести на улицу", createdEpic, Status.NEW, Duration.ofMinutes(30));
        Subtask subtask2 = new Subtask(null, "Занести", "Занести домой", createdEpic.getId(), Status.NEW, Duration.ofMinutes(30), LocalDateTime.now().plusHours(1));

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        assertEquals(Status.NEW, createdEpic.getStatus());
    }

    @Test
    public void testEpicStatusAllDone() {
        Epic epic = TestHelper.createEpic("Чистка", "Ковер");
        Epic createdEpic = taskManager.createEpic(epic);

        Subtask subtask1 = TestHelper.createSubtask("Вынести", "Вынести на улицу", createdEpic, Status.DONE, Duration.ofMinutes(30));
        Subtask subtask2 = new Subtask(null, "Занести", "Занести домой", createdEpic.getId(), Status.DONE, Duration.ofMinutes(30), LocalDateTime.now().plusHours(1));

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        assertEquals(Status.DONE, createdEpic.getStatus());
    }

    @Test
    public void testEpicStatusNewAndDone() {
        Epic epic = TestHelper.createEpic("Чистка", "Ковер");
        Epic createdEpic = taskManager.createEpic(epic);

        Subtask subtask1 = TestHelper.createSubtask("Вынести", "На улицу", createdEpic, Status.NEW, Duration.ofMinutes(30));
        Subtask subtask2 = new Subtask(null, "Занести", "Домой", createdEpic.getId(), Status.DONE, Duration.ofMinutes(30), LocalDateTime.now().plusHours(1));

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        assertEquals(Status.IN_PROGRESS, createdEpic.getStatus());
    }

    @Test
    public void testEpicStatusInProgress() {
        Epic epic = TestHelper.createEpic("Чистка", "Ковер");
        Epic createdEpic = taskManager.createEpic(epic);

        Subtask subtask1 = TestHelper.createSubtask("Вынести", "На улицу", createdEpic, Status.IN_PROGRESS, Duration.ofMinutes(30));
        Subtask subtask2 = new Subtask(null, "Занести", "Домой", createdEpic.getId(), Status.IN_PROGRESS, Duration.ofMinutes(30), LocalDateTime.now().plusHours(1));

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        assertEquals(Status.IN_PROGRESS, createdEpic.getStatus());
    }

}