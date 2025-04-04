package application;

import databasePart1.QuestionsAnswersDatabase;
import java.sql.SQLException;

// AnswerEvaluator class provides methods to test and validate answer operations.
public class AnswerEvaluator {
    // Error message and result attributes
    public static String errorMessage = "";
    public static boolean successful = false;
    // Empty string indicates passed test
    public static String testPostEmptyAnswer(QuestionsAnswersDatabase qaDatabase, int questionId, String userName) {
        errorMessage = "";
        successful = false;
        
        String content = "";
        
        if (content.isEmpty()) {
            errorMessage = "Error - Answer content is empty";
            return errorMessage;
        }
        
        successful = true;
        return errorMessage;
    }
    
    // Tests posting a valid answer to a question.
    public static String testPostValidAnswer(QuestionsAnswersDatabase qaDatabase, int questionId, String userName) {
        errorMessage = "";
        successful = false;
        
        try {
            String content = "This is an answer";
            Answer answer = new Answer(questionId, userName, content);
            
            int initialCount = qaDatabase.numAnswers(questionId);
            
            qaDatabase.addAnswer(answer);
            
            int newCount = qaDatabase.numAnswers(questionId);
            if (newCount > initialCount) {
                successful = true;
                return errorMessage;
            } else {
                errorMessage = "Error - Answer was not added";
                return errorMessage;
            }
        } catch (SQLException e) {
            errorMessage = "Error - Answer was not added" + e.getMessage();
            return errorMessage;
        }
    }
    
    // Tests marking an answer as resolving a question.
    public static String testMarkAnswerAsResolving(QuestionsAnswersDatabase qaDatabase, int questionId) {
        errorMessage = "";
        successful = false;
        
        try {
            int answerId = qaDatabase.getAnswerID(questionId, 0);          
            qaDatabase.answerResolves(questionId, answerId);
            if (qaDatabase.doesAnswerResolve(answerId)) {
                successful = true;
                return errorMessage;
            } else {
                errorMessage = "Error - Failed to mark answer as resolving";
                return errorMessage;
            }
        } catch (Exception e) {
            errorMessage = "Error - Failed to mark answer as resolving" + e.getMessage();
            return errorMessage;
        }
    }
    
    // Tests resolving a question without any answers.
    public static String testResolveQuestionWithoutAnswers(QuestionsAnswersDatabase qaDatabase, int questionId) {
        errorMessage = "";
        successful = false;
        
        try {
            int answerCount = qaDatabase.numAnswers(questionId);
            if (answerCount == 0) {
                errorMessage = "Error - Question without answers";
                return errorMessage;
            }
            successful = true;
            return errorMessage;
        } catch (SQLException e) {
            errorMessage = "Error - Question without answers" + e.getMessage();
            return errorMessage;
        }
    }
    
    // Tests posting a comment to a question.
    public static String testPostComment(QuestionsAnswersDatabase qaDatabase, int questionId, String userName) {
        errorMessage = "";
        successful = false;
        
        try {
            String content = "Test comment";
            Comment comment = new Comment(questionId, userName, content);
            
            int initialCount = qaDatabase.numComments(questionId);

            qaDatabase.addComments(comment);
            
            int newCount = qaDatabase.numComments(questionId);
            if (newCount > initialCount) {
                successful = true;
                return errorMessage;
            } else {
                errorMessage = "Error - Comment was not added";
                return errorMessage;
            }
        } catch (SQLException e) {
            errorMessage = "Error - Comment was not added" + e.getMessage();
            return errorMessage;
        }
    }
}
