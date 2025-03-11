package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private static class Node {
        Task value;
        Node next;
        Node prev;

        public Node(Task value, Node next, Node previous) {
            this.value = value;
            this.next = next;
            this.prev = previous;
        }
    }


    public InMemoryHistoryManager() {
    }

    HashMap<Integer, Node> history = new HashMap<>();
    Node first;
    Node last;


    @Override
    public void addToHistory(Task task) {
        Node node = history.get(task.getId());
        removeNode(node);
        linkLast(task);
    }

    @Override
    public List<Task> getHistory() {
        ArrayList<Task> history = new ArrayList<>();
        Node currentNode = first;
        while (!(currentNode == null)) {
            history.add(currentNode.value);
            currentNode = currentNode.next;
        }
        return history;

    }

    @Override
    public void remove(int id) {
        Node node = history.get(id);
        removeNode(node);
    }

    void linkLast(Task task) {
        final Node tail = last;
        final Node newNode = new Node(task, null, tail);
        last = newNode;
        if (tail == null) {
            first = newNode;
        } else {
            tail.next = newNode;
        }
        history.put(task.getId(), newNode);
    }

    private void removeNode(Node node) {
        if (!(node == null)) {
            final Node next = node.next;
            final Node prev = node.prev;
            node.value = null;

            if (first == node && last == node) {
                first = null;
                last = null;
            } else if (first == node) {
                first = next;
                first.prev = null;
            } else if (last == node) {
                last = prev;
                last.next = null;
            } else {
                prev.next = next;
                next.prev = prev;
            }
        }
    }
}
