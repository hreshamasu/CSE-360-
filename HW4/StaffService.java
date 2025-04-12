package hw4;

import java.util.Iterator;
import java.util.List;

public class StaffService {
    private List<Post> posts;
    private ProblemUserList problemUserList = new ProblemUserList();
    private AnnouncementBoard board = new AnnouncementBoard();
    private AdminRequestLog requestLog = new AdminRequestLog();

    public StaffService(List<Post> posts) {
        this.posts = posts;
    }

    public boolean deleteProblematicPost(int postId) {
        Iterator<Post> it = posts.iterator();
        while (it.hasNext()) {
            Post p = it.next();
            if (p.getId() == postId && p.isProblematic()) {
                it.remove();
                return true;
            }
        }
        return false;
    }

    public void addProblemUser(String username, String reason) {
        problemUserList.addProblemUser(username, reason);
    }

    public boolean isProblemUser(String username) {
        return problemUserList.isProblemUser(username);
    }

    public void postAnnouncement(String msg) {
        board.postAnnouncement(msg);
    }

    public List<String> getAllAnnouncements() {
        return board.getAllAnnouncements();
    }

    public void requestAdminAction(String request) {
        requestLog.addRequest(request);
    }

    public List<String> getAdminRequests() {
        return requestLog.getRequests();
    }
}
