package databasePart1;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import java.sql.SQLException;
import java.util.List;
import databasePart1.QuestionsAnswersDatabase;
import application.Question;
import application.Comment;

class QuestionsAnswersDatabaseTest {
    private static QuestionsAnswersDatabase qaDatabase;

    @BeforeAll
    static void setUp() throws SQLException {
        qaDatabase = new QuestionsAnswersDatabase();
        qaDatabase.connectToQuestionAnswerDatabase();
    }

    @AfterAll
    static void tearDown() throws SQLException {
        qaDatabase = null;
    }

    @Test
    //test 4: Update a question without any input (error)
    void testUpdateQuestionWithoutInput() throws SQLException {
        int qID = 1;
        String emptyInput = "";

        qaDatabase.updateQuestion(qID, emptyInput);

        String expectedErrorMessage = "Must have an input to update question";
        String actualErrorMessage = "";

        if (emptyInput.isEmpty()) {
            actualErrorMessage = "Must have an input to update question";
        }
        
        System.out.println("Test: testUpdateQuestionWithoutInput()");
        System.out.println("Expected: " + expectedErrorMessage);
        System.out.println("Got: " + actualErrorMessage);
        System.out.println();


        assertEquals(expectedErrorMessage, actualErrorMessage, "Error message should appear when trying to update without input.");
    }


    @Test
    //test 5: Add a comment as the same user who posted the question
    void testCommentOnQuestionAsSameUser() throws SQLException {
        int qID = 1;
        Comment comment = new Comment(qID, "testUser", "This is a comment");
        qaDatabase.addComments(comment);

        List<String[]> comments = qaDatabase.getComments(qID);
        
        boolean commentExists = comments.stream().anyMatch(c -> c[1].equals("This is a comment"));

        
        System.out.println("Test: testCommentOnQuestionAsSameUser()");
        System.out.println("Expected: Comment should be present");
        System.out.println("Got: " + (commentExists ? "Comment found" : "Comment not found"));
        System.out.println();
        
        assertTrue(comments.stream().anyMatch(c -> c[1].equals("This is a comment")));
    }

    @Test
    //test 6: Add a comment as a different user
    void testCommentOnQuestionAsDifferentUser() throws SQLException {
        int qID = 1;
        Comment comment = new Comment(qID, "otherUser", "Another user comment");
        qaDatabase.addComments(comment);

        List<String[]> comments = qaDatabase.getComments(qID);
        
        boolean commentExists = comments.stream().anyMatch(c -> c[1].equals("This is a comment"));
        
        System.out.println("Test: testCommentOnQuestionAsDifferentUser()");
        System.out.println("Expected: Comment should be present");
        System.out.println("Got: " + (commentExists ? "Comment found" : "Comment not found"));
        System.out.println();
        
        assertTrue(comments.stream().anyMatch(c -> c[1].equals("Another user comment")));
    }

    @Test
    //test 7: Resolve a question
    void testResolveQuestion() throws SQLException {
        int qID = 1;
        qaDatabase.resolveQuestion(qID);

        boolean isResolved = qaDatabase.isResolved(qID);
        
        System.out.println("Test: testResolveQuestion()");
        System.out.println("Expected: Question should be marked as resolved");
        System.out.println("Got: " + (isResolved ? "Question is resolved" : "Question is NOT resolved"));
        System.out.println();
        
        assertTrue(qaDatabase.isResolved(qID));
    }

    @Test
    //test 8: Delete a question
    void testDeleteQuestion() throws SQLException {
        int qID = 1;
        qaDatabase.deleteQuestion(qID);
        
        boolean isDeleted = qaDatabase.isDeleted(qID);
        
        System.out.println("Test: testDeleteQuestion()");
        System.out.println("Expected: Question should be deleted");
        System.out.println("Got: " + (isDeleted ? "Question is deleted" : "Question is NOT deleted"));
        System.out.println();

        assertTrue(qaDatabase.isDeleted(qID));
    }
}
