package application;
import java.sql.*;
import java.util.*;

public class AnswerStorage {
	
	// JDBC driver name and database URL 
	static final String JDBC_DRIVER = "org.h2.Driver";   
	static final String DB_URL = "jdbc:h2:~/Database";  

	//  Database credentials 
	static final String USER = "sa"; 
	static final String PASS = ""; 

	private Connection connection = null;
	private Statement statement = null; 
	//	PreparedStatement pstmt
		
	public void connectToAnswerDatabase() throws SQLException {
		try {
			Class.forName(JDBC_DRIVER); // Load the JDBC driver
			System.out.println("Connecting to database...");
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement(); 
			// You can use this command to clear the database and restart from fresh.
//			statement.execute("DROP ALL OBJECTS");

			createAnswerTable();  // Create the necessary tables if they don't exist
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}
	
	// Create the questions table
	private void createAnswerTable() throws SQLException {
		String answerTable = "CREATE TABLE IF NOT EXISTS answers ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "qID INT,"
				+ "userName VARCHAR(255), "
				+ "content TEXT, "
				+ "FOREIGN KEY (qID) REFERENCES questions(id) ON DELETE CASCADE)";
		statement.execute(answerTable);
	}
	
	
	// Return the number of answers for any question
	public int numAnswers(int qID) throws SQLException {
		String q = "SELECT COUNT(*) AS count FROM answers WHERE qID = ?";
		
		try (PreparedStatement pstmt = connection.prepareStatement(q)) {
			pstmt.setInt(1, qID);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getInt("count");
			}
		}
		return 0;
	}
	
	
	// Adds an answer to a question
	public void addAnswer(Answer answer) throws SQLException{
		String q = "INSERT INTO answers (qID, userName, content) VALUES (?, ?, ?)";
		
		try (PreparedStatement pstmt = connection.prepareStatement(q)) {
			pstmt.setInt(1, answer.getQID());
			pstmt.setString(2, answer.getUserName());
			pstmt.setString(3, answer.getContent());
			pstmt.executeUpdate();
		}
	}
	
	
	// Return an array list of arrays of strings containing the user name and content associated with answers
	public List<String[]> getAnswers(int qID) {
		String q = "SELECT userName, content FROM answers WHERE qID = ?";
		List<String[]> answers = new ArrayList<>();
		
		try (PreparedStatement pstmt = connection.prepareStatement(q)) {
			pstmt.setInt(1, qID);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				answers.add(new String[] {rs.getString("userName"), rs.getString("content")});
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return answers;
	}
	
	
	// Return an array of ids corresponding to answers that match the search word
	public int[] subsetSearch(String subset, int qID) {
		String q = "SELECT id FROM answers WHERE qID = ? AND title LIKE ? OR body LIKE ?";
		List<Integer> matches = new ArrayList<>();
		
		try (PreparedStatement pstmt = connection.prepareStatement(q)) {
			pstmt.setInt(1, qID);
			pstmt.setString(2, "%" + subset + "%");
			pstmt.setString(3, "%" + subset + "%");
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