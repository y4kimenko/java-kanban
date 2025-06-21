package main.manager;


import main.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class InMemoryHistoryManager implements HistoryManager {
    private static class Node {
        final Task task;
        Node prev;
        Node next;

        Node(Task task) {
            this.task = task;
        }
    }

    private Node head;                     // первый просмотр
    private Node tail;                     // последний просмотр

    private final Map<Integer, Node> history = new HashMap<Integer, Node>();

    @Override
    public ArrayList<Task> getHistory() {
        ArrayList<Task> history = new ArrayList<>();
        for (Node node = head; node != null; node = node.next) {
            history.add(node.task);
        }
        return history;
    }

    @Override
    public void add(Task task) {
        if (task == null) return;

        Node existing = history.get(task.getId());
        if (existing != null) {       // уже есть – убираем прежний узел
            removeNode(existing);
        }

        history.put(task.getId(), linkLast(new Node(task))); // создаём новый просмотр
    }

    @Override
    public void remove(int id) {
        if (history.get(id) == null) return;
        if (Objects.isNull(head)) return;

        removeNode(history.remove(id));
    }

    // ============ Вспомогательные методы ===============
    private Node linkLast(Node node) {

        if (node != null) {
            if (tail == null) {
                head = tail = node;
            } else {
                tail.next = node;
                node.prev = tail;
                tail = node;
            }
        }
        return node;
    }

    private void removeNode(Node removeNode) {

            if (removeNode.prev != null) {
                removeNode.prev.next = removeNode.next;
            } else {
                head = removeNode.next;
            }
            if (removeNode.next != null) {
                removeNode.next.prev = removeNode.prev;
            } else {
                tail = removeNode.prev;
            }
        }


}



