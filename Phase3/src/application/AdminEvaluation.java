package application;

import databasePart1.DatabaseHelper;
import java.sql.SQLException;
import java.util.List;

/**
 * AdminEvaluator class provides methods to test and validate admin operations.
 * Similar to PasswordEvaluator, it tests specific scenarios and returns error messages.
 */
public class AdminEvaluator {
    // Error message and result attributes
	// Empty string demonstrates passed test case
    public static String errorMessage = "";
    public static boolean successful = false;
    //Tests user deletion by admin.
 
    public static String testDeleteUser(DatabaseHelper dbHelper, String userName) {
        errorMessage = "";
        successful = false;
        
        try {
            if (!dbHelper.doesUserExist(userName)) {
                errorMessage = "Error: User to delete does not exist";
                return errorMessage;
            }
            
            // Delete the user
            String result = dbHelper.tryDeleteUser(userName);
            
            // Check deletion result
            if (result.equals(userName)) {
                // Verify user no longer exists
                if (!dbHelper.doesUserExist(userName)) {
                    successful = true;
                    return errorMessage; // Empty string indicates success
                } else {
                    errorMessage = "Error: User still exists after deletion";
                    return errorMessage;
                }
            } else {
                errorMessage = "Error: User deletion failed - " + result;
                return errorMessage;
            }
        } catch (SQLException e) {
            errorMessage = "Error: Exception during user deletion: " + e.getMessage();
            return errorMessage;
        }
    }
    
    // Tests generating a valid invitation code.

    public static String testGenerateInvitationCode(DatabaseHelper dbHelper) {
        errorMessage = "";
        successful = false;
        
        String code = dbHelper.generateInvitationCode(false, true, false, false, false);
        
        if (code != null && !code.isEmpty()) {
            String roles = dbHelper.getRoleFromCode(code);
            if (roles != null && roles.equals("student")) {
                successful = true;
                return errorMessage;
            } else {
                errorMessage = "Error: Generated invitation code has incorrect roles";
                return errorMessage;
            }
        } else {
            errorMessage = "Error: Failed to generate invitation code";
            return errorMessage;
        }
    }
    
    // Tests validating an invitation code.
    public static String testValidateInvitationCode(DatabaseHelper dbHelper) {
        errorMessage = "";
        successful = false;
        
        String code = dbHelper.generateInvitationCode(false, true, false, false, false);
        
        if (dbHelper.validateInvitationCode(code)) {
            successful = true;
            return errorMessage; // Empty string indicates success
        } else {
            errorMessage = "Error: Failed to validate a fresh invitation code";
            return errorMessage;
        }
    }
    
    // Tests changing user roles.
    public static String testChangeUserRoles(DatabaseHelper dbHelper, String userName) {
        errorMessage = "";
        successful = false;
        
        try {
            if (!dbHelper.doesUserExist(userName)) {
                errorMessage = "Error: User for role change does not exist";
                return errorMessage;
            }
            
            String newRoles = "student, reviewer";
            dbHelper.changeRoles(userName, newRoles);
            
            String userRole = dbHelper.getUserRole(userName);
            if (userRole.equals(newRoles)) {
                successful = true;
                return errorMessage;
            } else {
                errorMessage = "Error: User roles were not changed correctly";
                return errorMessage;
            }
        } catch (SQLException e) {
            errorMessage = "Error: Exception during role change: " + e.getMessage();
            return errorMessage;
        }
    }
    
    // Tests approving a reviewer request.
    public static String testApproveReviewerRequest(DatabaseHelper dbHelper, String studentUserName) {
        errorMessage = "";
        successful = false;
        
        try {
            dbHelper.addReviewerRequest(studentUserName);
            
            dbHelper.approveReviewerRequest(studentUserName);
            
            String userRole = dbHelper.getUserRole(studentUserName);
            if (userRole != null && userRole.contains("reviewer")) {
                List<String> requests = dbHelper.getReviewerRequests();
                if (!requests.contains(studentUserName)) {
                    successful = true;
                    return errorMessage;
                } else {
                    errorMessage = "Error: Reviewer request was not removed after approval";
                    return errorMessage;
                }
            } else {
                errorMessage = "Error: User does not have reviewer role after approval";
                return errorMessage;
            }
        } catch (SQLException e) {
            errorMessage = "Error: Exception during reviewer approval: " + e.getMessage();
            return errorMessage;
        }
    }
    
    // Tests creating a one-time password for a user.
    public static String testCreateOneTimePassword(DatabaseHelper dbHelper, String userName) {
        errorMessage = "";
        successful = false;
        
        String oneTimePass = dbHelper.createOneTimePass(userName);
        
        // Check if a password was generated
        if (oneTimePass != null && !oneTimePass.isEmpty()) {
            successful = true;
            return errorMessage;
        } else {
            errorMessage = "Error: Failed to create one-time password";
            return errorMessage;
        }
    }
}