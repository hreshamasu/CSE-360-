package application;

import java.sql.Timestamp;

public class Message {
    private int id;
    private String sender;
    private String receiver;
    private String content;
    private Timestamp timestamp;
    private boolean isRead;

    // Constructor for new messages (id and timestamp auto-generated)
    public Message(String sender, String receiver, String content) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
    }

    // Constructor for messages loaded from the database
    public Message(int id, String sender, String receiver, String content, Timestamp timestamp, boolean isRead) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.timestamp = timestamp;
        this.isRead = isRead;
    }

    // Getters and setters
    public int getId() { return id; }
    public String getSender() { return sender; }
    public String getReceiver() { return receiver; }
    public String getContent() { return content; }
    public Timestamp getTimestamp() { return timestamp; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
}
