package main.tasks;

public enum TypeTask {
    TASK,
    EPIC,
    SUBTASK;

    @Override
    public String toString() {
        return this.name();
    }
}
