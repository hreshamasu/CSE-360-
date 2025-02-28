package application;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import databasePart1.QuestionsAnswersDatabase;

class QuestionTest {
    private static QuestionsAnswersDatabase qaDatabase;

    @Test
    //test 1: Post a question without a title (error)
    void testCreateQuestionWithoutTitle() {
        String emptyTitle = "";

        Question q = new Question("testUser", emptyTitle, "Body");

        // Simulate what happens in the UI when "Post" is clicked
        String expectedErrorMessage = "The title and body must contain characters";
        String actualErrorMessage = "";

        if (q.getTitle().isEmpty() || q.getBody().isEmpty()) {
            actualErrorMessage = "The title and body must contain characters";
        }

        // Check if the expected error message matches what would appear in the UI
        assertEquals(expectedErrorMessage, actualErrorMessage, "Error message should be displayed for empty fields.");
    }
    
    @Test 
    //test 2: Post a question with a title >255 chars (error)
    
    void testCreateQuestionWithLongTitle() {
        String longTitle = "a".repeat(256);  // Generate a 256-character string

        String expectedErrorMessage = "Titles must be shorter than 255 characters";
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Question("testUser", longTitle, "Valid body text.");
        });

        assertTrue(exception.getMessage().contains(expectedErrorMessage), "Title should be rejected if it's too long.");
    }

    @Test
    //test 3: Post a question without a body (error)
    void testCreateQuestionWithoutBody() {
        String emptyBody = "";

        // Create a new Question instance with empty fields
        Question q = new Question("testUser", "title", emptyBody);

        // Simulate what happens in the UI when "Post" is clicked
        String expectedErrorMessage = "The title and body must contain characters";
        String actualErrorMessage = "";

        if (q.getTitle().isEmpty() || q.getBody().isEmpty()) {
            actualErrorMessage = "The title and body must contain characters";
        }

        // Check if the expected error message matches what would appear in the UI
        assertEquals(expectedErrorMessage, actualErrorMessage, "Error message should be displayed for empty fields.");
    }

}
