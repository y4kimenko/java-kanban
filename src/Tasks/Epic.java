package Tasks;



import java.util.ArrayList;
import java.util.HashSet;

public class Epic extends Task {
    private final ArrayList<Integer> subTasks = new ArrayList<>();

    public Epic(String name, String description, StatusTask status, Integer id) {
        super(name, description, status, id);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subTasks=" + subTasks +
                ", id=" + id +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", name='" + name + '\'' +
                '}';
    }

    public ArrayList<Integer> getSubTasks() {
        return subTasks;
    }

}
