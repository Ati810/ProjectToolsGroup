package utilities;

import java.util.ArrayList;

/**
 * Class that contains the information of a new Borrowing.
 * @author Attila
 */
public class NewBorrowingInfo {
    
    /**
     * The Member who wants to borrow.
     */
    public Member member;
    
    /**
     * The Book(s) that the Member wants to borrow.
     */
    public ArrayList<Book> books;
    
    /**
     * Constructor for creating a new NewBorrowingInfo.
     * @param member The Member who wants to borrow.
     * @param books The Book(s) that the Member wants to borrow.
     */
    public NewBorrowingInfo(Member member, ArrayList<Book> books) {
        this.member = member;
        this.books = books;
    }
    
}
