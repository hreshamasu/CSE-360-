package application;

import java.sql.SQLException;
import java.util.List;
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
import databasePart1.QuestionsAnswersDatabase;
import databasePart1.DatabaseHelper;

public class QuestionsAnswersPage {

	// Initialize the database
    private static final QuestionsAnswersDatabase qaDatabase = new QuestionsAnswersDatabase();
    
    // Initialize the vbox for the right side of the screen
    private VBox vbox = new VBox(10);
    
    // Label to display errors
    Label errorLabel = new Label("");
    
    // Initialize the user name variable
    private String userName;
    
    // Initialize a boolean to check if the resolved checkbox is checked
    private boolean isChecked = false;
    
    final User user;
    final DatabaseHelper databaseHelper;
    
    private boolean connected = false;
    
    public QuestionsAnswersPage(Stage primaryStage, DatabaseHelper databaseHelper, User user) {
    	this.databaseHelper = databaseHelper;
    	this.user = user;
    	show(primaryStage);
    }
    
    	
    // Show the main page
    public void show(Stage primaryStage) {
    	if (!connected) {
    		try {
    			qaDatabase.connectToQuestionAnswerDatabase();
    			connected = true;
    		} catch (SQLException e) {
    			e.printStackTrace();
    		}
    	}
    	
    	userName = user.getUserName();
    	
        // Initialize vbox for right side of window
        vbox = new VBox(10);
        vbox.setAlignment(Pos.TOP_LEFT);

        // Set sizing and padding for posts vbox
        VBox posts = new VBox(10);
        posts.setPadding(new Insets(10));
        posts.setPrefWidth(300);
        posts.setMaxWidth(300);
        posts.setAlignment(Pos.TOP_LEFT);

        // Button to bring up question creation page
        Button addQuestion = new Button("Ask a Question");
        addQuestion.setMinWidth(250);
        addQuestion.setOnAction(a -> {
            createQuestion(primaryStage);
        });
        
        // CheckBox to allow filtering of own unresolved questions
        CheckBox ownUnresolved = new CheckBox("View only own unresolved questions");
        ownUnresolved.setOnAction(a -> {
        	if (isChecked) {
        		isChecked = false;
        	} else {
        		isChecked = true;
        	}
        	show(primaryStage);
        });
        ownUnresolved.setSelected(isChecked);
        posts.getChildren().addAll(addQuestion, ownUnresolved);

        // Fill posts vbox with all posted questions
        try {
            for (int i = qaDatabase.numQuestions(); i > 0; i--) {

                // Skip to next question if current was deleted
                if (qaDatabase.isDeleted(i)) {
                    continue;
                }
                
                // Skip to next question if ownUnresolved is checked and next question is resolved or not owned by current user
                if (ownUnresolved.isSelected()) {
                	if (!(!(qaDatabase.isResolved(i)) && qaDatabase.doesUserOwnQuestion(i, userName))) {
                		continue;
                	}
                }
                
                final int qID = i;
                
                // Create button for each question
                Button postButton = new Button(qaDatabase.getTitleFromQuestionID(qID));
                
                // If the current question is resolved, add a tag
                if (qaDatabase.isResolved(i)) {
                	postButton.setText(postButton.getText() + " *Answered*");
                }
                
                postButton.setMinWidth(250);
                postButton.setStyle("-fx-background-color: transparent;");
                postButton.setWrapText(true);

                // When a question is clicked on, open it on the right
                postButton.setOnAction(a -> {
                    printQuestion(qaDatabase.getQuestionInfo(qID), primaryStage, qID);
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
        rightScrollPane.setPadding(new Insets (10));

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
        bodyLabel.setWrapText(true);
        
        // Separator between question and answers
        Separator separator = new Separator();
        separator.setMinWidth(250);

        vbox.getChildren().addAll(titleLabel, userNameLabel, bodyLabel, separator);

        // Button to delete question
        Button deleteButton = new Button("Delete Question");
        deleteButton.setOnAction(a -> {
        	qaDatabase.deleteQuestion(id);
            show(primaryStage);
        });
        
        // Button to resolve question
        Button resolveButton = new Button("Resolve Question");
        resolveButton.setOnAction(a -> {
        	qaDatabase.resolveQuestion(id);
            show(primaryStage);
        });
        
        // Button to unresolve question
        Button unResolveButton = new Button("Unresolve Question");
        unResolveButton.setOnAction(a -> {
        	qaDatabase.unResolveQuestion(id);
            show(primaryStage);
        });

        // Array list to hold answers
        List<String[]> answers = qaDatabase.getAnswers(id);

        // Loop through answers and create user name and content labels for each
        for (int i = 0; i < answers.size(); i++) {
        	final int answerCount = i;
        	String[] answer = answers.get(i);
        	
            Label answerUserLabel = new Label(answer[0]);
            answerUserLabel.setWrapText(true);
            
            Label answerContentLabel = new Label("     " + answer[1]);
            answerContentLabel.setWrapText(true);
            
            int answerID = qaDatabase.getAnswerID(id, answerCount);
            if (qaDatabase.doesAnswerResolve(answerID)) {
            	answerUserLabel.setStyle("-fx-font-weight: bold;");
            	answerContentLabel.setStyle("-fx-font-weight: bold;");
            }
            
            if (qaDatabase.doesUserOwnQuestion(id, userName)) {
            	Button answeredButton = new Button("Answers the Question");
            	
            	answeredButton.setOnAction(a -> {
            		
            		
		    		if (answerID == 0) {
		    			System.out.println("Error in getAnswerID\n");
		    			return;
		    		}
		        	qaDatabase.resolveQuestion(id);
		        	qaDatabase.answerResolves(id, answerID);
		            show(primaryStage);
                });
            	vbox.getChildren().addAll(answerUserLabel, answerContentLabel, answeredButton);
            } else {
            	vbox.getChildren().addAll(answerUserLabel, answerContentLabel);
            }
        }

        // Text area to enter the answer content
        TextArea newAnswerText = new TextArea();
        newAnswerText.setPromptText("Answer the Question");
        newAnswerText.setMaxWidth(400);
        newAnswerText.setPrefHeight(50);
        newAnswerText.setWrapText(true);

        // Button to add an answer to a question
        Button addAnswerButton = new Button("Post Answer");
        addAnswerButton.setOnAction(a -> {
    		// Create new answer and add it to the database
    		Answer answer = new Answer(id, userName, newAnswerText.getText());
    		try {
    			qaDatabase.addAnswer(answer);
    		} catch (SQLException e) {
    			e.printStackTrace();
    		}
    		newAnswerText.clear();
    		printQuestion(info, primaryStage, id);
    	
        });

        vbox.getChildren().addAll(newAnswerText, addAnswerButton);
        
        // Button to update the question
        Button updateButton = new Button("Update Question");
        updateButton.setOnAction(a -> {
        	if (!(newAnswerText.getText().isEmpty())) {
        		qaDatabase.updateQuestion(id, newAnswerText.getText());
        		show(primaryStage);
        	} else {
        		errorLabel.setText("Must have an input to update question");
        		vbox.getChildren().add(errorLabel);
        	}
        });

        // If the user wrote the post, give them special permissions
        if (qaDatabase.doesUserOwnQuestion(id, userName)) {
        	
        	// Allow the updating of the question
        	newAnswerText.setPromptText("Answer or Update the Question");
            vbox.getChildren().addAll(updateButton, deleteButton);
            
            // Allow the resolving/ unresolving of the question
            if (!(qaDatabase.isResolved(id))) {
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
        postButton.setOnAction(a -> {

            // Check if any of the fields are empty
            if (titleText.getText().equals("") || bodyText.getText().equals("")) {
            	errorLabel.setText("The title and body must contain characters\n");
            
            // Check if the title is too long
            } else if (titleText.getText().length() > 255){
            	errorLabel.setText("Titles must be shorter than 255 characters\n");
            
            } else {
                Question q = new Question(userName, titleText.getText(), bodyText.getText());
                try {
                	qaDatabase.addQuestion(q);

                } catch (SQLException e) {
                    e.printStackTrace();
                }
                show(primaryStage);
            }

        });

        // Button to cancel the post
        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(a -> {
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
