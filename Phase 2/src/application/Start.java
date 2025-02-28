package application;

import java.sql.SQLException;
import java.util.List;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;

public class Start extends Application {

    // Initialize the database
    private static final QuestionStorage questionDatabase = new QuestionStorage();
    private static final AnswerStorage answerDatabase = new AnswerStorage();
    
    // Initialize the vbox for the right side of the screen
    private VBox vbox = new VBox(10);
    
    // Label to display errors
    Label errorLabel = new Label("");
    
    // Initialize the user name variable
    private String userName;
    
    // Initialize a boolean to check if the resolved checkbox is checked
    private boolean isChecked = false;

    // Start the program
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // Connect to the databases
            questionDatabase.connectToQuestionDatabase();
            answerDatabase.connectToAnswerDatabase();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        login(primaryStage);
    }
    
    // Create the initial login screen for the user to enter their user name
    public void login(Stage primaryStage) {
    	
    	// Text field to enter user name
    	TextField userNameText = new TextField();
    	userNameText.setPromptText("Enter User Name");
    	
    	// Button to login
    	Button login = new Button("Login");
    	
    	vbox.getChildren().addAll(userNameText, login, errorLabel);
    	
    	// Set user name and travel to home page
    	login.setOnAction(_ -> {
    		userName = userNameText.getText();
    		if (userName.length() > 255) {
    			errorLabel.setText("Username must be shorter than 255 characters\n");
    		} else if (userName.length() == 0) {
    			errorLabel.setText("Username must be shorter than 255 characters\n");
    		} else {
    			errorLabel.setText("");
    			vbox.getChildren().clear();
    			show(primaryStage);
    		}
    	});
    	
    	// Create scene
		Scene scene = new Scene(vbox, 900, 500);
	    primaryStage.setScene(scene);
	    primaryStage.show();
    }
    	
    // Show the main page
    public void show(Stage primaryStage) {
        show(primaryStage, "");  // Default with no filter
    }
    
    // Show the main page
    public void show(Stage primaryStage, String searchFilter) {
        // Initialize vbox for right side of window
        vbox = new VBox(10);
        vbox.setAlignment(Pos.TOP_LEFT);

        VBox posts = new VBox(10);
        posts.setPadding(new Insets(10));
        posts.setPrefWidth(300);
        posts.setMaxWidth(300);
        posts.setAlignment(Pos.TOP_LEFT);

        // Button to bring up question creation page
        Button addQuestion = new Button("Ask a Question");
        addQuestion.setMinWidth(250);
        addQuestion.setOnAction(_ -> {
            createQuestion(primaryStage);
        });
        
        // Search bar to filter questions
        TextField searchField = new TextField();
        searchField.setPromptText("Filter questions");
        searchField.setMinWidth(250);  
        searchField.setMaxWidth(250);
        searchField.setText(searchFilter);
        
        // Filter button
        Button filterButton = new Button("Filter");
        filterButton.setMinWidth(250);
        filterButton.setMaxWidth(250);
        filterButton.setOnAction(_ -> {
            String searchText = searchField.getText();
            show(primaryStage, searchText);
        });

        CheckBox ownUnresolved = new CheckBox("View only own unresolved questions");
        ownUnresolved.setOnAction(_ -> {
            if (isChecked) {
                isChecked = false;
            } else {
                isChecked = true;
            }
            show(primaryStage, searchFilter);
        });
        ownUnresolved.setSelected(isChecked);

        posts.getChildren().addAll(addQuestion, searchField, filterButton, ownUnresolved);

        try {
            int[] filteredQuestionIds = null;
            if (searchFilter != null && !searchFilter.trim().isEmpty()) {
                filteredQuestionIds = questionDatabase.subsetSearch(searchFilter);
            }
            
            for (int i = questionDatabase.numQuestions(); i > 0; i--) {
                // Skip to next question if current was deleted
                if (questionDatabase.isDeleted(i)) {
                    continue;
                }
                
                // Skip if question isn't in the results
                if (filteredQuestionIds != null && !containsId(filteredQuestionIds, i)) {
                    continue;
                }
                
                 if (ownUnresolved.isSelected()) {
                    if (!(!(questionDatabase.isResolved(i)) && questionDatabase.doesUserOwnQuestion(i, userName))) {
                        continue;
                    }
                }
                
                final int qID = i;
                
                // Create button for each question
                Button postButton = new Button(questionDatabase.getTitleFromQuestionID(qID));
                
                // If the current question is resolved, add a tag
                if (questionDatabase.isResolved(i)) {
                    postButton.setText("RESOLVED " + postButton.getText());
                }
                
                postButton.setMinWidth(250);
                postButton.setStyle("-fx-background-color: transparent;");
                postButton.setWrapText(true);

                // When a question is clicked on, open it on the right
                postButton.setOnAction(_ -> {
                    printQuestion(questionDatabase.getQuestionInfo(qID), primaryStage, qID);
                });

                // Create separator between questions
                Separator separator = new Separator();
                separator.setMinWidth(250);
                posts.getChildren().addAll(postButton, separator);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // ScrollPane to make posts vbox scrollable
        ScrollPane postsScrollPane = new ScrollPane();
        postsScrollPane.setContent(posts);
        postsScrollPane.setFitToWidth(true);
        postsScrollPane.setPrefWidth(300);

        // ScrollPane to make vbox (questions and answers) scrollable
        ScrollPane rightScrollPane = new ScrollPane();
        rightScrollPane.setContent(vbox);
        rightScrollPane.setFitToWidth(true);
        rightScrollPane.setPrefWidth(550);
        rightScrollPane.setPadding(new Insets(10));

        // HBox to combine posts and vbox
        HBox hbox = new HBox(10);
        hbox.getChildren().addAll(postsScrollPane, rightScrollPane);
        hbox.setPadding(new Insets(10));
        hbox.setAlignment(Pos.TOP_LEFT);

        // Create scene
        Scene scene = new Scene(hbox, 900, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Helper method to check if an array contains a specific ID
    private boolean containsId(int[] ids, int id) {
        for (int i : ids) {
            if (i == id) {
                return true;
            }
        }
        return false;
    }

    // Show a question and answers on right side of screen
    public void printQuestion(String[] info, Stage primaryStage, int id) {
    	
        // Clear the vbox initially
        vbox.getChildren().clear();
        vbox.setAlignment(Pos.CENTER_LEFT);

        // Create labels for title, user name, and body
        Label titleLabel = new Label(info[1]);
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        titleLabel.setWrapText(true);

        Label userNameLabel = new Label(info[0]);
        userNameLabel.setStyle("-fx-font-size: 14px;");
        userNameLabel.setWrapText(true);
        
        Label bodyLabel = new Label(info[2]);
        bodyLabel.setWrapText(true);
        
        // Separator between question and answers
        Separator separator = new Separator();
        separator.setMinWidth(250);

        vbox.getChildren().addAll(titleLabel, userNameLabel, bodyLabel, separator);

        // Button to delete question
        Button deleteButton = new Button("Delete Question");
        deleteButton.setOnAction(_ -> {
            questionDatabase.deleteQuestion(id);
            show(primaryStage);
        });
        
        // Button to resolve question
        Button resolveButton = new Button("Resolve Question");
        resolveButton.setOnAction(_ -> {
            questionDatabase.resolveQuestion(id);
            show(primaryStage);
        });
        
        // Button to unresolve question
        Button unResolveButton = new Button("Unresolve Question");
        unResolveButton.setOnAction(_ -> {
            questionDatabase.unResolveQuestion(id);
            show(primaryStage);
        });

        // Array list to hold answers
        List<String[]> answers = answerDatabase.getAnswers(id);

        // Loop through answers and create user name and content labels for each
        for (String[] answer : answers) {
            Label answerUserLabel = new Label(answer[0]);
            answerUserLabel.setWrapText(true);
            Label answerContentLabel = new Label("     " + answer[1]);
            answerContentLabel.setWrapText(true);
            vbox.getChildren().addAll(answerUserLabel, answerContentLabel);
        }

        // Text area to enter the answer content
        TextArea newAnswerText = new TextArea();
        newAnswerText.setPromptText("Answer the Question");
        newAnswerText.setMaxWidth(400);
        newAnswerText.setPrefHeight(50);
        newAnswerText.setWrapText(true);

        // Button to add an answer to a question
        Button addAnswerButton = new Button("Post Answer");
        addAnswerButton.setOnAction(_ -> {
    		// Create new answer and add it to the database
    		Answer answer = new Answer(id, userName, newAnswerText.getText());
    		try {
    			answerDatabase.addAnswer(answer);
    		} catch (SQLException e) {
    			e.printStackTrace();
    		}
    		newAnswerText.clear();
    		printQuestion(info, primaryStage, id);
        	
        });

        vbox.getChildren().addAll(newAnswerText, addAnswerButton);
        
        // Button to update the question
        Button updateButton = new Button("Update Question");
        updateButton.setOnAction(_ -> {
        	if (!(newAnswerText.getText().isEmpty())) {
        		questionDatabase.updateQuestion(id, newAnswerText.getText());
        		show(primaryStage);
        	} else {
        		errorLabel.setText("Must have an input to update question");
        		vbox.getChildren().add(errorLabel);
        	}
        });

        // If the user wrote the post, give them special permissions
        if (questionDatabase.doesUserOwnQuestion(id, userName)) {
        	
        	// Allow the updating of the question
        	newAnswerText.setPromptText("Answer or Update the Question");
            vbox.getChildren().addAll(updateButton, deleteButton);
            
            // Allow the resolving/ unresolving of the question
            if (!(questionDatabase.isResolved(id))) {
            	vbox.getChildren().add(resolveButton);
            } else {
            	vbox.getChildren().add(unResolveButton);
            }
        }
        vbox.setMinWidth(500);
    }

    // Show the inputs to create a question on the right side of the screen
    public void createQuestion(Stage primaryStage) {
    	
        // Clear the vbox initially
        vbox.getChildren().clear();

        // Text field to enter a title
        Label titleLabel = new Label("Title");
        TextField titleText = new TextField();
        titleText.setMinWidth(350);

        // Text area to enter the question body
        TextArea bodyText = new TextArea();
        bodyText.setMaxWidth(450);
        bodyText.setMinHeight(200);
        bodyText.setWrapText(true);

        // Button to post the question
        Button postButton = new Button("Post");
        postButton.setOnAction(_ -> {

            // Check if any of the fields are empty
            if (titleText.getText().equals("") || bodyText.getText().equals("")) {
            	errorLabel.setText("The title and body must contain characters\n");
            
            // Check if the title is too long
            } else if (titleText.getText().length() > 255){
            	errorLabel.setText("Titles must be shorter than 255 characters\n");
            
            } else {
                Question q = new Question(userName, titleText.getText(), bodyText.getText());
                try {
                    questionDatabase.addQuestion(q);

                } catch (SQLException e) {
                    e.printStackTrace();
                }
                show(primaryStage);
            }

        });

        // Button to cancel the post
        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(_ -> {
            vbox.getChildren().clear();
            return;
        });

        // Hbox to align the title label and text field
        HBox title = new HBox(10);
        title.setStyle("-fx-alignment: center;");
        title.getChildren().addAll(titleLabel, titleText);

        vbox.getChildren().addAll(title, bodyText, postButton, cancelButton, errorLabel);
        vbox.setStyle("-fx-alignment: center; -fx-padding: 20;");
        vbox.setMinWidth(450);
    }
}