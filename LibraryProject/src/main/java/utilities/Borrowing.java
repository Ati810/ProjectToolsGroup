package utilities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Class that represents a Borrowing.
 * @author Attila
 */
public class Borrowing {
    
    private int borrowingNumber;
    
    private int libTicketNum;

    private ArrayList<String> bookISBNs;

    private Date borrowDate;

    private Date dueDate;
    
    private Date returnDate;
    
    private boolean archived;
    
    //Programon beluli Borrowing letrehozasahoz.

    /**
     * Constructor for creating a new Borrowing.
     * @param borrowingNumber The borrowingNumber of this Borrowing.
     * @param libTicketNum The libTicketNum of the Member who is borrowing.
     * @param bookISBNs The Books' ISBNs that we borrow.
     */
    public Borrowing(int borrowingNumber, int libTicketNum, ArrayList<String> bookISBNs) {
        this.borrowingNumber = borrowingNumber;
        this.libTicketNum = libTicketNum;
        this.bookISBNs = bookISBNs;
        this.borrowDate = new Date();
        
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(borrowDate);            
        calendar.add(Calendar.DAY_OF_YEAR, 14);
        this.dueDate = calendar.getTime();      //Minden kolcsonzesnel 2 heten belul vissza kell hozni a konyveket
        
        this.returnDate = null;
        this.archived = false;
    }
    
    //Ezt a konstruktort hasznaljuk, amikor az adatbazisbol kerjuk le az adatokat.

    /**
     * Constructor for creating a new Borrowing.
     * @param borrowingNumber The borrowingNumber of this Borrowing.
     * @param libTicketNum The libTicketNum of the Member who is borrowing.
     * @param bookISBNs The Books' ISBNs that we borrow.
     * @param borrowDate The date when this Borrowing happened.
     * @param dueDate The date when the Book(s) needs to be returned.
     * @param returnDate The date when the Book(s) had returned.
     * @param archived Whether this Borrowing is archived or not.
     */
    public Borrowing(int borrowingNumber, int libTicketNum, ArrayList<String> bookISBNs, Date borrowDate, Date dueDate, Date returnDate, boolean archived) {
        this.borrowingNumber = borrowingNumber;
        this.libTicketNum = libTicketNum;
        this.bookISBNs = bookISBNs;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.archived = archived;
    }
    
    /**
     * Getter function for the Borrowing's borrowing number.
     * @return The Borrowing's borrowing number as an int.
     */
    public int getBorrowingNumber() {
        return borrowingNumber;
    }

    /**
     * Getter function for the Member's library ticket number, who made this Borrowing.
     * @return The Member's library ticket number as an int.
     */
    public int getLibTicketNum() {
        return libTicketNum;
    }

    /**
     * Getter function for the Borrowing's Book ISBNs.
     * @return The Borrowing's Book ISBNs as an ArrayList.
     */
    public ArrayList<String> getBookISBNs() {
        return bookISBNs;
    }

    /**
     * Getter function for when this Borrowing happened.
     * @return The Borrowing's borrow date as a Date.
     */
    public Date getBorrowDate() {
        return borrowDate;
    }

    /**
     * Getter function for when the Book(s) needs to be returned.
     * @return The Borrowing's due date as a Date.
     */
    public Date getDueDate() {
        return dueDate;
    }
    
    /**
     * Getter function for when the Book(s) had returned.
     * @return The Borrowing's return date as a Date.
     */
    public Date getReturnDate() {
        return returnDate;
    }
    
    /**
     * Function for retrieving if this Borrowing is archive.
     * @return Whether this Borrowing is archive or not.
     */
    public boolean isArchive() {
        return archived;
    }
    
    /**
     * Setter function for setting this Borrowing as archived.
     */
    public void setArchive() {
        returnDate = new Date();
        archived = true;
    }

}
