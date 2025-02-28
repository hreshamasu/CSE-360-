package application;

public class Question {
	private String userName;
	private String title;
	private String body;
	
	// Constructor to initialize a question with a user name, title, and body
	public Question(String userName, String title, String body) {
		 if (title.length() > 255) {
	            throw new IllegalArgumentException("Titles must be shorter than 255 characters");
	        }
		this.userName = userName;
		this.title = title;
		this.body = body;
	}
	
	// Functions to return user name, title, or body
	public String getUserName() {return userName;}
	public String getTitle() {return title;}
	public String getBody() {return body;}
}
