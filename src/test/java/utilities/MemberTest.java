package utilities;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * @author Barna
 */
public class MemberTest {
    @Test
    public void addBorrowingTest() throws Exception {
        Member member = new Member(99, "Kelley Yakov", "06545 Riverside Road");
        Borrowing borrowing = new Borrowing(999, 99, new ArrayList<>());
        assertEquals(member.getBorrowings().size(), 0);
        member.addBorrowing(borrowing);
        assertEquals(member.getBorrowings().size(), 1);
    }

}