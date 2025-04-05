import databasePart1.DatabaseHelper;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AdminTests {

    @Test
    void testInvitationCodeExpiration() throws SQLException {
        // Tests validating an expired invitation code
        DatabaseHelper db = new DatabaseHelper();
        String code = db.generateInvitationCode(false, true, false, false, false);

        db.expireInvitationCode(code); // simulate expiration

        assertFalse(db.validateInvitationCode(code));
    }

    @Test
    void testAssigningMultipleRolesToUser() throws SQLException {
        // Tests assigning multiple roles (e.g., student, reviewer) to a user
        DatabaseHelper db = new DatabaseHelper();
        String user = "hrishi";
        db.register(new User(user, "securePass123", "student"));

        String newRoles = "student,reviewer";
        db.changeRoles(user, newRoles);

        String rolesFromDB = db.getUserRole(user);
        assertEquals(newRoles, rolesFromDB);
    }

    @Test
    void testRevokingReviewerApproval() throws SQLException {
        // Tests revoking a reviewer request that was previously approved
        DatabaseHelper db = new DatabaseHelper();
        String user = "hrishi";

        db.addReviewerRequest(user);
        db.approveReviewerRequest(user);
        db.revokeReviewerRequest(user); // assume method exists

        List<String> requests = db.getReviewerRequests();
        assertFalse(requests.contains(user));
    }

    @Test
    void testExpiredOneTimePasswordLogin() throws SQLException {
        // Tests login attempt using an expired one-time password
        DatabaseHelper db = new DatabaseHelper();
        String user = "hrishi";
        String otp = db.createOneTimePass(user);

        db.expireOneTimePass(user); // assume method exists

        boolean result = db.validateOneTimePass(user, otp);
        assertFalse(result);
    }

    @Test
    void testResolvingAfterSolutionAlreadySelected() throws SQLException {
        // Tests marking a solution when one has already been marked for a question
        QuestionsAnswersDatabase qaDB = new QuestionsAnswersDatabase();
        int questionId = 10;
        String user = "hrishi";

        Answer answer = new Answer(questionId, user, "Answer marked as solution");
        qaDB.addAnswer(answer);

        int answerId = qaDB.getAnswerID(questionId, 0);
        qaDB.answerResolves(questionId, answerId);

        // Attempt to mark the same answer again
        qaDB.answerResolves(questionId, answerId);

        assertTrue(qaDB.doesAnswerResolve(answerId));
    }
}
