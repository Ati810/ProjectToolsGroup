package utilities;

/**
 * Class that represents a Book.
 * @author Attila
 */
public class Book {
    
    private final String ISBN;

    private final String author;

    private final String title;

    private final int releaseYear;

    private int quantity;

    private int available;

    private int numOfBorrowings;

    private boolean locked;

    //Ezt a konstruktort hasznaljuk, amikor az adatbazisbol kerjuk le az adatokat (csak igy hozunk letre konyvet).

    /**
     * Constructor for creating a new Book.
     * @param ISBN The ISBN of the Book.
     * @param author The author of the Book.
     * @param title The title of the Book.
     * @param releaseYear The release year of the Book.
     * @param quantity The maximum number of this Book in the library.
     * @param available How many book is available in the library with this title (maximum quantity amount).
     * @param numOfBorrowings How many times has someone borrowed this Book.
     * @param locked Whether this Book is locked or not.
     */
    public Book(String ISBN, String author, String title, int releaseYear, int quantity, int available, int numOfBorrowings, boolean locked) {
        this.ISBN = ISBN;
        this.author = author;
        this.title = title;
        this.releaseYear = releaseYear;
        this.quantity = quantity;
        this.available = available;
        this.numOfBorrowings = numOfBorrowings;
        this.locked = locked;
    }

    /**
     * Getter function for the Book's author.
     * @return The Book's author as a String.
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Getter function for the Book's title.
     * @return The Book's title as a String.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Getter function for the Book's ISBN.
     * @return The Book's ISBN as a String.
     */
    public String getISBN() {
        return ISBN;
    }

    /**
     * Getter function for the Book's release year.
     * @return The Book's release year as an int.
     */
    public int getReleaseYear() {
        return releaseYear;
    }

    /**
     * Getter function for the Book's quantity.
     * @return The Book's quantity as an int.
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Setter function for the Book's quantity.
     * @param quantity The new quantity of this Book.
     */
    public void setQuantity(int quantity) {
        if (quantity >= 0) {
            this.quantity = quantity;
        }
    }

    /**
     * Getter function for the Book's availability.
     * @return The Book's availability as an int.
     */
    public int getAvailable() {
        return available;
    }

    /**
     * Getter function for how many times this Book has been borrowed.
     * @return The number of borrowings as an int.
     */
    public int getNumOfBorrowings() {
        return numOfBorrowings;
    }

    /**
     * Function for retrieving if this Book is locked.
     * @return Whether this Book is locked or not.
     */
    public boolean isLocked() {
        return locked;
    }

    /**
     * Setter function for setting this Book locked, if it's unlocked.
     */
    public void setLocked() {
        if (!locked) {
            locked = true;
        }
    }

    /**
     * Setter function for setting this Book unlocked, if it's locked.
     */
    public void setUnlocked() {
        if (locked) {
            locked = false;
        }
    }

    /**
     * Function for handling the borrowing of this Book.
     */
    public void borrowBook() {
        if (available >= 1) {
            --available;
            ++numOfBorrowings;
        }
    }

    /**
     * Function for handling the returning of this Book.
     */
    public void returnBook() {
        if (available < quantity) {
            ++available;
        }
    }

}
