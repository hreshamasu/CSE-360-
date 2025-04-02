package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import databasePart1.DatabaseHelper;
import java.sql.SQLException;
import java.util.List;

public class MessageInboxPage {

    private DatabaseHelper dbHelper;
    private String reviewerUser;

    public MessageInboxPage(DatabaseHelper dbHelper, String reviewerUser) {
        this.dbHelper = dbHelper;
        this.reviewerUser = reviewerUser;
    }

    public void show(Stage primaryStage) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
        Label title = new Label("Inbox - Messages for " + reviewerUser);

        ListView<String> messageList = new ListView<>();
        try {
            List<Message> messages = dbHelper.getMessagesForUser(reviewerUser);
            for (Message msg : messages) {
                String item = "From: " + msg.getSender() + "\n" + msg.getContent() +
                              "\nSent: " + msg.getTimestamp();
                messageList.getItems().add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            new ReviewerHomePage(new User(reviewerUser, "", "reviewer"), dbHelper).show(primaryStage);
        });

        layout.getChildren().addAll(title, messageList, backButton);
        Scene scene = new Scene(layout, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Message Inbox");
    }
}

