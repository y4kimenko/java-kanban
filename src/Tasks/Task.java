package Tasks;

import java.util.Objects;
import java.util.Scanner;

public class Task {


    protected Integer id;
    protected String name;
    protected String description;
    protected StatusTask status;


    public Task(String name, String description, StatusTask status, Integer id) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id);
    }


    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    //    @Override
//    public String toString() {
//        return name + " (" + status + ")\n" +
//        "Описание: " + description;
//    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public StatusTask getStatus() {
        return status;
    }

    public void setStatus(StatusTask status) {
        this.status = status;
    }
}
