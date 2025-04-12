package hw4;

public class Post {
    private int id;
    private String content;
    private boolean isProblematic;

    public Post(int id, String content) {
        this.id = id;
        this.content = content;
        this.isProblematic = false;
    }

    public int getId() { return id; }
    public String getContent() { return content; }
    public boolean isProblematic() { return isProblematic; }
    public void flagAsProblematic() { this.isProblematic = true; }
}
