package unitTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.sql.Timestamp;

import org.junit.jupiter.api.Test;

import application.Review;



public class ReviewTest {

    @Test
    public void testBasicConstructor() {
        Review r = new Review(1, "question", "san", "Helpful!");
        assertEquals(1, r.getTargetID());
        assertEquals("question", r.getTargetType());
        assertEquals("san", r.getReviewerName());
        assertEquals("Helpful!", r.getReviewText());
        assertNull(r.getTimestamp());
    }

    @Test
    public void testFullConstructor() {
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        Review r = new Review(100, 1, "answer", "bob", "Good point", ts);
        assertEquals(100, r.getId());
        assertEquals(1, r.getTargetID());
        assertEquals("answer", r.getTargetType());
        assertEquals("bob", r.getReviewerName());
        assertEquals("Good point", r.getReviewText());
        assertEquals(ts, r.getTimestamp());
    }

    @Test
    public void testSetReviewText() {
        Review r = new Review(1, "question", "carol", "Old");
        r.setReviewText("Updated");
        assertEquals("Updated", r.getReviewText());
    }

    @Test
    public void testNullReviewTextAllowed() {
        Review r = new Review(1, "question", "dave", null);
        assertNull(r.getReviewText());
    }

    @Test
    public void testEmptyReviewerNameAllowed() {
        Review r = new Review(1, "answer", "", "Hmm");
        assertEquals("", r.getReviewerName());
    }

    @Test
    public void testNullReviewerName() {
        Review r = new Review(1, "question", null, "Testing");
        assertNull(r.getReviewerName());
    }

    @Test
    public void testNullTargetType() {
        Review r = new Review(1, null, "emma", "Testing");
        assertNull(r.getTargetType());
    }

    @Test
    public void testLongReviewText() {
        String longText = "a".repeat(1000);
        Review r = new Review(1, "answer", "frank", longText);
        assertEquals(1000, r.getReviewText().length());
    }

    @Test
    public void testNegativeTargetID() {
        Review r = new Review(-1, "question", "gina", "Check ID");
        assertEquals(-1, r.getTargetID());
    }

    @Test
    public void testNullTimestamp() {
        Review r = new Review(1, "answer", "harry", "No time");
        assertNull(r.getTimestamp());
    }
}

   