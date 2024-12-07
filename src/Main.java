import Tasks.Epic;
import Tasks.Status;
import Tasks.Subtask;
import Tasks.Task;
import manager.TaskManager;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        Task task1 = new Task(null, "Уборка", "Сделать уборку всех комнат", Status.NEW);
        Task task2 = new Task(null, "Написание", "Написать книгу", Status.NEW);
        task1.setName("Уборка всей комнаты");
        taskManager.updateTask(task1);
        taskManager.deleteTaskById(-1);
        Epic epic1 = new Epic(null, "Чтение", "Книги 2");
        taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask(null, "Чтение первой книги", "Книга 1", epic1.getId(), Status.DONE);
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask(null, "Чтение второй книги", "Книга 2", epic1.getId(), Status.DONE);
        taskManager.createSubtask(subtask2);
        Epic epic2 = new Epic(null, "Бег", "Круг"); // 4
        taskManager.createEpic(epic2);
        Subtask subtask3 = new Subtask(null, "Медленный бег", "Круг 1", epic2.getId(), Status.IN_PROGRESS);
        taskManager.createSubtask(subtask3);

        System.out.println(task1);
        System.out.println(task2);
        System.out.println(epic1);
        System.out.println(subtask1);
        System.out.println(subtask2);
        System.out.println(epic2);
        System.out.println(subtask3);


    }


}
