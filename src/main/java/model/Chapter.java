package model;

public class Chapter {
    public int id;
    public String chapterName;

    @Override
    public String toString() {
        return "Chapter{" +
                "id=" + id +
                ", chapterName='" + chapterName + '\'' +
                '}';
    }
}
