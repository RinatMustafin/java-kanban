package manager;

import app.exception.FileManagerFileInitializationException;
import app.exception.FileManagerSaveException;
import tasks.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {


    private File data;

    public FileBackedTaskManager(File data) {
        this.data = data;
    }

    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private void save() {
        Collection<Task> values = tasks.values();
        try (FileWriter fr = new FileWriter(data)) {
            fr.write("id,type,name,status,description,epic,duration,time" + System.lineSeparator());
        } catch (IOException e) {
            String errorMessage = String.format("Ошибка при сохранении в файл %s", e.getMessage());
            System.out.println(errorMessage);
            throw new FileManagerSaveException(errorMessage);
        }
        for (Task task : tasks.values()) {
            String taskAsString = taskToString(task);
            System.out.println(String.format("Записываем в файл: %s", taskAsString));
            writeStringToFile(taskAsString);
        }
    }

    private void writeStringToFile(String taskAsString) {
        try (FileWriter fr = new FileWriter(data, true)) {
            fr.write(taskAsString + System.lineSeparator());
        } catch (IOException e) {
            String errorMessage = String.format("Ошибка при сохранении в файл %s", e.getMessage());
            System.out.println(errorMessage);
            throw new FileManagerSaveException(errorMessage);
        }
    }

    private String taskToString(Task task) {
        String result = task.getId() + "," + task.getType() + "," + task.getName() + "," + task.getStatus() + "," + task.getDescription() + ",";
        if (task.getType().equals(TaskType.SUBTASK)) {
            Subtask subtask = (Subtask) task;
            result += subtask.getEpicId();
        } else if (task.getType().equals(TaskType.EPIC)) {
            Epic epic = (Epic) task;
            result += epic.getId();
        }
        result += "," + task.getDuration().toMinutes() + "," + task.getStartTime().format(formatter);
        return result;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        try {
            List<String> allLines = Files.readAllLines(file.toPath());
            if (allLines.isEmpty()) {
                return fileBackedTaskManager;
            }

            for (int i = 1; i < allLines.size(); i++) {
                String line = allLines.get(i);
                String[] lines = line.split(",");
                Task task = null;
                TaskType taskType = TaskType.valueOf(lines[1]);
                switch (taskType) {
                    case TASK:
                        task = new Task(
                                Integer.parseInt(lines[0]),
                                lines[2],
                                lines[4],
                                getTaskStatus(lines[3]),
                                Duration.ofMinutes(Long.parseLong(lines[6])),
                                LocalDateTime.parse(lines[7], formatter)
                        );
                        fileBackedTaskManager.tasks.put(task.getId(), task);
                        break;
                    case SUBTASK:
                        task = new Subtask(
                                Integer.parseInt(lines[0]),
                                lines[2],
                                lines[4],
                                Integer.parseInt(lines[5]),
                                getTaskStatus(lines[3]),
                                Duration.ofMinutes(Long.parseLong(lines[6])),
                                LocalDateTime.parse(lines[7], formatter)
                        );
                        fileBackedTaskManager.subtasks.put(task.getId(), (Subtask) task);
                        break;
                    case EPIC:
                        task = new Epic(
                                Integer.parseInt(lines[0]), // id
                                lines[2], // name
                                lines[4],
                                Duration.ofMinutes(Long.parseLong(lines[6])),
                                LocalDateTime.parse(lines[7], formatter)// description
                        );
                        fileBackedTaskManager.epics.put(task.getId(), (Epic) task);
                        break;

                }
            }
            return fileBackedTaskManager;
        } catch (IOException e) {
            String errorMessage = String.format("Ошибка при сохранении в файл %s", e.getMessage());
            System.out.println(errorMessage);
            throw new FileManagerFileInitializationException(errorMessage);
        }

    }


    @Override
    public Task createTask(Task task) {
        Task createdTask = super.createTask(task);
        save();
        return createdTask;
    }


    @Override
    public Task updateTask(Task task) {
        Task updateTask = super.updateTask(task);
        save();
        return updateTask;
    }

    @Override
    public Task deleteTaskById(Integer id) {
        Task deletedTaskById = super.deleteTaskById(id);
        save();
        return deletedTaskById;
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic createdEpic = super.createEpic(epic);
        tasks.put(epic.getId(), epic);
        save();
        return createdEpic;
    }

    @Override
    public Task updateEpic(Epic epic) {
        Task updatedEpic = super.updateEpic(epic);
        save();
        return updatedEpic;
    }

    @Override
    public void deleteEpicById(Integer id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        Subtask createdSubtask = super.createSubtask(subtask);
        tasks.put(subtask.getId(), subtask);
        save();
        return createdSubtask;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        Subtask updatedSubtask = super.updateSubtask(subtask);
        save();
        return updatedSubtask;
    }

    @Override
    public void deleteSubtaskById(Integer id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }


    private static Status getTaskStatus(String line) {
        return switch (line) {
            case "NEW" -> Status.NEW;
            case "IN_PROGRESS" -> Status.IN_PROGRESS;
            case "DONE" -> Status.DONE;
            default -> throw new IllegalStateException(String.format("Неизвестное значение: %s", line));
        };
    }
}

