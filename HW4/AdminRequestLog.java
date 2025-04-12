package hw4;

import java.util.ArrayList;
import java.util.List;

public class AdminRequestLog {
    private List<String> requests = new ArrayList<>();

    public void addRequest(String request) {
        requests.add(request);
    }

    public List<String> getRequests() {
        return requests;
    }
}
