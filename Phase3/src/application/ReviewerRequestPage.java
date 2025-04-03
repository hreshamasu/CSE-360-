package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import databasePart1.DatabaseHelper;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewerRequestPage {

    private DatabaseHelper dbHelper;

    public ReviewerRequestPage(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public void show(Stage primaryStage) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
        Label title = new Label("Reviewer Requests");

        ListView<String> requestList = new ListView<>();
        List<String> requests = new ArrayList<>();
        String sql = "SELECT userName FROM cse360users WHERE reviewerRequested = 1";
        try (PreparedStatement pstmt = dbHelper.getConnection().prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                requests.add(rs.getString("userName"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        requestList.getItems().addAll(requests);

        Button approveButton = new Button("Approve Selected Request");
        approveButton.setOnAction(e -> {
            String selectedUser = requestList.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                try {
                    String updateSql = "UPDATE cse360users SET role = 'reviewer', reviewerRequested = 0 WHERE userName = ?";
                    try (PreparedStatement pstmt = dbHelper.getConnection().prepareStatement(updateSql)) {
                        pstmt.setString(1, selectedUser);
                        pstmt.executeUpdate();
                    }
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Approved " + selectedUser + " as reviewer.");
                    alert.show();
                    show(primaryStage);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            new InstructorHomePage().show(dbHelper, primaryStage);
        });

        layout.getChildren().addAll(title, requestList, approveButton, backButton);
        Scene scene = new Scene(layout, 500, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Reviewer Requests");
    }
}
