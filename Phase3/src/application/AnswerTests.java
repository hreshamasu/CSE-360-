import databasePart1.QuestionsAnswersDatabase;
import application.Answer;
import application.Comment;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class AnswerTests {

    @Test
    void testEditingExistingAnswer() throws SQLException {
        // Tests editing an existing answer and verifying the updated content
        QuestionsAnswersDatabase qaDB = new QuestionsAnswersDatabase();
        int questionId = 1;
        String user = "hrishi";
        Answer answer = new Answer(questionId, user, "Original answer");
        qaDB.addAnswer(answer);

        int answerId = qaDB.getAnswerID(questionId, 0);
        qaDB.editAnswerContent(answerId, "Edited answer content");

        String updated = qaDB.getAnswerContent(answerId);
        assertEquals("Edited answer content", updated);
    }

    @Test
    void testDeletingAnswer() throws SQLException {
        // Tests deleting an answer and confirming it no longer exists
        QuestionsAnswersDatabase qaDB = new QuestionsAnswersDatabase();
        int questionId = 2;
        String user = "hrishi";
        Answer answer = new Answer(questionId, user, "Answer to delete");
        qaDB.addAnswer(answer);

        int answerId = qaDB.getAnswerID(questionId, 0);
        qaDB.deleteAnswer(answerId);

        assertFalse(qaDB.answerExists(answerId));
    }

    @Test
    void testSubmittingDuplicateAnswers() throws SQLException {
        // Tests submitting the same answer content twice and validating the count
        QuestionsAnswersDatabase qaDB = new QuestionsAnswersDatabase();
        int questionId = 3;
        String user = "hrishi";
        String content = "Duplicate answer";

        Answer answer1 = new Answer(questionId, user, content);
        Answer answer2 = new Answer(questionId, user, content);
        qaDB.addAnswer(answer1);

        int before = qaDB.numAnswers(questionId);
        qaDB.addAnswer(answer2);
        int after = qaDB.numAnswers(questionId);

        assertEquals(before + 1, after);
    }

    @Test
    void testAddingCommentToAnswer() throws SQLException {
        // Tests adding a comment to a question and confirming the count increases
        QuestionsAnswersDatabase qaDB = new QuestionsAnswersDatabase();
        int questionId = 4;
        String user = "hrishi";
        Comment comment = new Comment(questionId, user, "This is a comment");

        int initialCount = qaDB.numComments(questionId);
        qaDB.addComments(comment);
        int newCount = qaDB.numComments(questionId);

        assertTrue(newCount > initialCount);
    }

    @Test
    void testAddingAnswerToLockedQuestion() throws SQLException {
        // Tests adding an answer to a locked question and expecting a failure
        QuestionsAnswersDatabase qaDB = new QuestionsAnswersDatabase();
        int qID = 6;
        qaDB.lockQuestion(qID);

        Answer answer = new Answer(qID, "hrishi", "Attempt on locked question");
        Exception exception = assertThrows(SQLException.class, () -> qaDB.addAnswer(answer));

        assertTrue(exception.getMessage().contains("locked"));
    }
}      
