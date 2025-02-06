package manager;

import java.io.File;

public class Managers {
    public static TaskManager getDefault() {
      //  HistoryManager historyManager = getDefaultHistory();
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getFileBackedTaskManager(File file) {
        return new FileBackedTaskManager(file);
    }

}
