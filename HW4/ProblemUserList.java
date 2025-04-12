package hw4;

import java.util.HashMap;
import java.util.Map;

public class ProblemUserList {
    private Map<String, String> problemUsers = new HashMap<>();

    public void addProblemUser(String username, String reason) {
        problemUsers.put(username, reason);
    }

    public boolean isProblemUser(String username) {
        return problemUsers.containsKey(username);
    }

    public String getReason(String username) {
        return problemUsers.get(username);
    }

    public void removeProblemUser(String username) {
        problemUsers.remove(username);
    }
}
