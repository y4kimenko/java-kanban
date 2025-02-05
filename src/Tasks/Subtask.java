package Tasks;

public class Subtask extends Task {
    private Integer indexEpic;

    public Subtask(String name, String description, StatusTask status, Integer id, Integer indexEpic) {
        super(name, description, status, id );
        this.indexEpic = indexEpic;
    }


    public Integer getIndexEpic() {
        return indexEpic;
    }

    public void setIndexEpic(Integer indexEpic) {
        this.indexEpic = indexEpic;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "indexEpic=" + indexEpic +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
