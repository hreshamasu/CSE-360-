package hw4;

import org.junit.jupiter.api.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class StaffServiceTest {

    StaffService staffService;
    List<Post> postList;

    @BeforeEach
    public void setUp() {
        postList = new ArrayList<>();
        postList.add(new Post(1, "Normal post"));
        postList.add(new Post(2, "Problematic content"));
        postList.get(1).flagAsProblematic();
        staffService = new StaffService(postList);
    }

    @Test
    public void testDeleteProblematicPost() {
        boolean deleted = staffService.deleteProblematicPost(2);
        assertTrue(deleted);
        assertEquals(1, postList.size());
    }

    @Test
    public void testAddAndCheckProblemUser() {
        staffService.addProblemUser("user123", "Spam");
        assertTrue(staffService.isProblemUser("user123"));
    }

    @Test
    public void testPostAnnouncement() {
        staffService.postAnnouncement("System maintenance at midnight.");
        List<String> announcements = staffService.getAllAnnouncements();
        assertEquals(1, announcements.size());
        assertEquals("System maintenance at midnight.", announcements.get(0));
    }

    @Test
    public void testRequestAdminAction() {
        staffService.requestAdminAction("Request to delete user456");
        List<String> requests = staffService.getAdminRequests();
        assertEquals(1, requests.size());
        assertEquals("Request to delete user456", requests.get(0));
    }
}
