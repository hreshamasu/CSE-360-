package application;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuestionStorage {
	
	// JDBC driver name and database URL 
	static final String JDBC_DRIVER = "org.h2.Driver";   
	static final String DB_URL = "jdbc:h2:~/Database";  

	//  Database credentials 
	static final String USER = "sa"; 
	static final String PASS = ""; 

	private Connection connection = null;
	private Statement statement = null;
		
	public void connectToQuestionDatabase() throws SQLException {
		try {
			Class.forName(JDBC_DRIVER); // Load the JDBC driver
			System.out.println("Connecting to database...");
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement(); 
			// You can use this command to clear the database and restart from fresh.
//			statement.execute("DROP ALL OBJECTS");

			createQuestionTable();  // Create the necessary tables if they don't exist
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}
	
	
	// Create the questions table
	private void createQuestionTable() throws SQLException {
		String questionTable = "CREATE TABLE IF NOT EXISTS questions ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "userName VARCHAR(255), "
				+ "title VARCHAR(255), "
				+ "body TEXT, "
				+ "deleted BOOLEAN DEFAULT FALSE, "
				+ "resolved BOOLEAN DEFAULT FALSE)";
		statement.execute(questionTable);
	}
	
	
	// Count the number of questions
	public int numQuestions() throws SQLException {
		String q = "SELECT COUNT(*) AS count FROM questions";
		ResultSet rs = statement.executeQuery(q);
		if (rs.next()) {
			return rs.getInt("count");
		}
		return 0;
	}
	
	
	// Adds a question to the database
	public void addQuestion(Question question) throws SQLException {
		String q = "INSERT INTO questions (userName, title, body) VALUES (?, ?, ?)";
		
		try (PreparedStatement pstmt = connection.prepareStatement(q)) {
			pstmt.setString(1, question.getUserName());
			pstmt.setString(2, question.getTitle());
			pstmt.setString(3, question.getBody());
			pstmt.executeUpdate();
		}
	}
	
	
	// Return the title associated with a question id
	public String getTitleFromQuestionID(int id) {
		String q = "SELECT title FROM questions WHERE id = ?";
		
		try (PreparedStatement pstmt = connection.prepareStatement(q)) {
			pstmt.setInt(1, id);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getString("title");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "Error";
	}
	
	
	// Return an array containing the user name, title, and body of a question associated with an id
	public String[] getQuestionInfo(int id) {
		String q = "SELECT * FROM questions WHERE id = ?";
		String out[] = new String[3];
		
		try (PreparedStatement pstmt = connection.prepareStatement(q)) {
			pstmt.setInt(1, id);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				out[0] = rs.getString("userName");
				out[1] = rs.getString("title");
				out[2] = rs.getString("body");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return out;
	}
	
	
	// Return true if the question associated with an id was created by a specified user
	public boolean doesUserOwnQuestion(int id, String userName) {
		String q = "SELECT userName FROM questions WHERE id = ?";
		
		try (PreparedStatement pstmt = connection.prepareStatement(q)) {
			pstmt.setInt(1, id);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				String user = rs.getString("userName");
				if (user.equals(userName)) {
					return true;
				}
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	// 'Delete' a question by setting the deleted boolean to true
	// Does not actually delete the question so it is still held in the database
	public void deleteQuestion(int id) {
		String q = "UPDATE questions SET deleted = TRUE WHERE id = ?";
		
		try (PreparedStatement pstmt = connection.prepareStatement(q)) {
			pstmt.setInt(1, id);
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	// Return true if a question has been deleted
	public boolean isDeleted(int id) throws SQLException {
		String q = "SELECT deleted FROM questions WHERE id = ?";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(q)) {
	        pstmt.setInt(1, id);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getBoolean("deleted");
	        }
	    }
	    return false;
	}
	
	
	// Set a question to resolved
	public void resolveQuestion(int id) {
		String q = "UPDATE questions SET resolved = TRUE WHERE id = ?";
		
		try (PreparedStatement pstmt = connection.prepareStatement(q)) {
			pstmt.setInt(1, id);
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	// Set a question to unresolved
	public void unResolveQuestion(int id) {
		String q = "UPDATE questions SET resolved = FALSE WHERE id = ?";
		
		try (PreparedStatement pstmt = connection.prepareStatement(q)) {
			pstmt.setInt(1, id);
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	// Return true if a question has been resolved
	public boolean isResolved(int id){
		String q = "SELECT resolved FROM questions WHERE id = ?";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(q)) {
	        pstmt.setInt(1, id);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            return rs.getBoolean("resolved");
	        }
	        
	    } catch (SQLException e) {
	    	e.printStackTrace();
	    }
	    return false;
	}
	
	
	// Update the body of a question to any new string
	public void updateQuestion(int id, String updatedBody) {
		String q = "UPDATE questions SET body = ? WHERE id = ?";
		
		try (PreparedStatement pstmt = connection.prepareStatement(q)) {
			pstmt.setString(1, updatedBody);
			pstmt.setInt(2, id);
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	// Return an array of ids corresponding to questions that match the search word
	public int[] subsetSearch(String subset) {
		String q = "SELECT id FROM questions WHERE title LIKE ? OR body LIKE ?";
		List<Integer> matches = new ArrayList<>();
		
		try (PreparedStatement pstmt = connection.prepareStatement(q)) {
			pstmt.setString(1, "%" + subset + "%");
			pstmt.setString(2, "%" + subset + "%");
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				matches.add(rs.getInt("id"));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return matches.stream().mapToInt(i -> i).toArray();
	}
}