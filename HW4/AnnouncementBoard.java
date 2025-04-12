package hw4;

import java.util.ArrayList;
import java.util.List;

public class AnnouncementBoard {
    private List<String> announcements = new ArrayList<>();

    public void postAnnouncement(String message) {
        announcements.add(message);
    }

    public List<String> getAllAnnouncements() {
        return announcements;
    }
}
