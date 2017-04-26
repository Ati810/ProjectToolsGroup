package utilities;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Created by Lorand on 2017. 04. 26..
 */
public class BookTest {
    @Test
    public void borrowBookTest() throws Exception {
        int quantity = 5;
        int available = 5;
        Book book = new Book("dummyISBN","dummyAuthor","dummyTitle",2000,quantity,available,0,false);
        book.borrowBook();
        assertEquals(book.getAvailable(),available-1);
    }

    @Test
    public void returnBookTest() throws Exception {
        int quantity = 5;
        int available = quantity-1;
        Book book = new Book("dummyISBN","dummyAuthor","dummyTitle",2000,quantity,available,0,false);
        book.returnBook();
        assertEquals(book.getAvailable(),quantity);
    }

}