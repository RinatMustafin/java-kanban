import manager.FileBackedTaskManager;
import manager.Managers;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;
import manager.TaskManager;

import java.io.File;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Main {

    public static void main(String[] args) {
        File file = new File("data.csv");
        TaskManager taskManager = Managers.getFileBackedTaskManager(file);

        File backup = new File("backup.csv");
        FileBackedTaskManager restoredTaskManager = FileBackedTaskManager.loadFromFile(backup);

        System.out.println(String.format("Количество загруженных задач: %d", restoredTaskManager.getTasks().size()));
        System.out.println(String.format("Количество загруженных подзадач: %d", restoredTaskManager.getSubtasks().size()));
        System.out.println(String.format("Количество загруженных эпиков: %d", restoredTaskManager.getEpics().size()));

        Task task1 = new Task(null, "Уборка", "Сделать уборку всех комнат", Status.NEW, Duration.ofMinutes(10), LocalDateTime.of(LocalDate.now(), LocalTime.now()));
        Task task2 = new Task(null, "Написание", "Написать книгу", Status.NEW, Duration.ofMinutes(15), LocalDateTime.of(LocalDate.now(), LocalTime.now().plus(Duration.ofMinutes(15))));
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        task1.setName("Уборка всей комнаты");
        taskManager.updateTask(task1);
        taskManager.deleteTaskById(-1);
        Epic epic1 = new Epic(null, "Чтение", "Книги 2");
        taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask(null, "Чтение первой книги", "Книга 1", epic1.getId(), Status.DONE, Duration.ofMinutes(15), LocalDateTime.of(LocalDate.now(), LocalTime.now().plus(Duration.ofMinutes(30))));
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask(null, "Чтение второй книги", "Книга 2", epic1.getId(), Status.DONE, Duration.ofMinutes(15), LocalDateTime.of(LocalDate.now(), LocalTime.now().plus(Duration.ofMinutes(45))));
        taskManager.createSubtask(subtask2);
        Epic epic2 = new Epic(null, "Бег", "Круг"); // 4
        taskManager.createEpic(epic2);
        Subtask subtask3 = new Subtask(null, "Медленный бег", "Круг 1", epic2.getId(), Status.IN_PROGRESS, Duration.ofMinutes(15), LocalDateTime.of(LocalDate.now(), LocalTime.now().plus(Duration.ofMinutes(60))));
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
