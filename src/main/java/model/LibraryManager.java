package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import database.Database;
import utilities.Book;
import utilities.Borrowing;
import utilities.Member;
import utilities.Penalty;

/**
 * Class that manages books, members, borrowings and the penalties, and uses a Database.
 *
 */
public class LibraryManager {

    private TreeMap<Integer, Member> members;

    private TreeMap<Integer, Penalty> penalties;

    private TreeMap<Integer, Borrowing> borrowings;

    private TreeMap<Integer, Borrowing> archivedBorrowings;

    private TreeMap<String, Book> books;

    private Database database;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    /**
     * Constructor for creating the database and filling the local storage with the received data.
     * @param name The username for the database connection.
     * @param password The password for the database connection.
     * @throws Exception
     */
    public LibraryManager(String name, String password) throws Exception {
        try {
            database = new Database(name, password);

            members = database.getMembers();
            penalties = database.getPenalties();
            borrowings = database.getBorrowings();
            archivedBorrowings = new TreeMap<>();
            books = database.getBooks();

            /*for (Book b : books) {
             System.out.println(b.getISBN() + " " + b.getAuthor() + " " + b.getTitle() + " " + b.getReleaseYear() + " " + b.getQuantity() + " " + b.getAvailable()
             + " " + b.getNumOfBorrowings() + " " + b.isLocked());
             }*/
            /*for (Member m : members.values()) {
                System.out.println(m.getLibTicketNum() + " " + m.getName() + " " + m.getAddress() + " " + m.getDelayCount());
            }*/
            /*for (Borrowing b : borrowings.values()) {
                System.out.println(b.getBorrowingNumber() + " " + b.getLibTicketNum() + " " + b.getBorrowDate() + " " + b.getDueDate() + " " + (b.getReturnDate() == null ? "" : b.getReturnDate())
                 + " " + b.isArchive() + " " + b.getBookISBNs().toString());
            }*/
            /*for (Penalty p : penalties.values()) {
                System.out.println(p.getLibTicketNum() + " " + p.getStartDate() + " " + p.getEndDate());
            }*/

            //Az aktiv Borrowing-ok beallitasa a Member-ekhez, valamint ha archiv, akkor atrakjuk az archivedBorrowings-ba
            for (Borrowing b : borrowings.values()) {
                if (b.isArchive()) {
                    archivedBorrowings.put(b.getBorrowingNumber(), b);
                } else {
                    members.get(b.getLibTicketNum()).addBorrowing(b);
                }
            }

            //Az aktiv kolcsonzesek kozul kivesszuk az archivaltakat
            for (Borrowing b : archivedBorrowings.values()) {
                borrowings.remove(b.getBorrowingNumber());
            }

            //A Penalty-k beallitasa a Member-ekhez
            for (Penalty p : penalties.values()) {
                members.get(p.getLibTicketNum()).setPenalty(p);
            }

            checkPenalties();

        } catch (Exception e) {
            //e.printStackTrace();
            throw e;
        }
    }

    /**
     * Getter function for the local Members TreeMap.
     * @return A TreeMap of Members, where the Key is the libTicketNumber and the Value is the Member.
     */
    public TreeMap<Integer, Member> getMembers() {
        return members;
    }

    /**
     * Function for creating a new Member and inserting it into the database.
     * @param name The name of the new Member.
     * @param address The address of the new Member.
     * @throws Exception
     */
    public void newMember(String name, String address) throws Exception {
        try {
            int libTicketNum = members.lastKey() + 1;
            Member newMember = new Member(libTicketNum, name, address);

            members.put(libTicketNum, newMember);
            database.insertMember(newMember);

        } catch (Exception e) {
            throw e;
        }
    }

    //Csak olyan Member-t kaphatunk, akinek nincs hatraleka

    /**
     * Function for deleting an existing Member locally and from the database.
     * @param member The Member that needs to be deleted.
     * @throws Exception
     */
    public void deleteMember(Member member) throws Exception {
        members.remove(member.getLibTicketNum());
        try {
            if (member.getBorrowings().isEmpty()) {
                if (member.hasPenalty()) {
                    database.deletePenalty(member.getPenalty());
                }

                database.deleteMember(member);
                members.remove(member.getLibTicketNum());

            } else {
                throw new Exception("You can't delete " + member.getName() + ", because she/he has to return all her/his books.");
            }

        } catch (Exception e) {
            throw e;
        }
    }

    //Ha mindket ertek null, akkor hibat dobunk, kulonben ami nem null, azt megvaltoztatjuk

    /**
     * Function for updating an existing Member locally and in the database.
     * @param libTicketNum The libTicketNum of the Member, that needs to be updated.
     * @param newName The new name of the Member, or null if we don't want to change it.
     * @param newAddress The new address of the Member, or null if we don't want to change it.
     * @throws Exception
     */
    public void updateMember(int libTicketNum, String newName, String newAddress) throws Exception {
        try {
            if (newName == null && newAddress == null) {
                throw new Exception("You must change the member's name and/or address.");

            } else if (newAddress == null && !members.get(libTicketNum).getName().equals(newName)) {
                if (newName.length() > 1) {
                    members.get(libTicketNum).setName(newName);
                    database.updateMember(members.get(libTicketNum));
                } else {
                    throw new Exception("The new name must be longer than 1 character.");
                }

            } else if (newName == null && !members.get(libTicketNum).getAddress().equals(newAddress)) {
                if (newAddress.length() > 1) {
                    members.get(libTicketNum).setAddress(newAddress);
                    database.updateMember(members.get(libTicketNum));
                } else {
                    throw new Exception("The new address must be longer than 1 character.");
                }

            } else if ((!members.get(libTicketNum).getName().equals(newName) || !members.get(libTicketNum).getAddress().equals(newAddress)) &&
                    newName != null && newAddress != null) {
                members.get(libTicketNum).setName(newName);
                members.get(libTicketNum).setAddress(newAddress);
                database.updateMember(members.get(libTicketNum));
            }
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Getter function for the local Penalties TreeMap.
     * @return A TreeMap of Penalties, where the Key is the libTicketNumber and the Value is the Penalty.
     */
    public TreeMap<Integer, Penalty> getPenalties() {
        return penalties;
    }

    /**
     * Getter function for the local Borrowings TreeMap.
     * @return A TreeMap of Borrowings, where the Key is the borrowingNumber and the Value is the Borrowing.
     */
    public TreeMap<Integer, Borrowing> getBorrowings() {
        return borrowings;
    }

    //Csak olyan Member-t kaphatunk, akinek nincs buntetese, s csak olyan Book-ok, melyeket lehet kolcsonozni

    /**
     * Function for creating a new Borrowing and inserting it into the database.
     * @param member The Member who wants to borrow the Book(s).
     * @param books The Book(s) that the Member wants to borrow.
     * @throws Exception
     */
    public void newBorrowing(Member member, ArrayList<Book> books) throws Exception {
        try {
            ArrayList<String> bookISBNs = new ArrayList<>();

            //Minden konyvet kikolcsonzunk
            for (Book b : books) {
                b.borrowBook();
                bookISBNs.add(b.getISBN());
                database.updateBook(b);
            }

            int b_key = (!borrowings.isEmpty() ? borrowings.lastKey() + 1 : 0);
            int ab_key = (!archivedBorrowings.isEmpty() ? archivedBorrowings.lastKey() + 1 : 0);
            int finalKey = (b_key > ab_key ? b_key : ab_key);
            Borrowing borrowing = new Borrowing(finalKey, member.getLibTicketNum(), bookISBNs);
            database.insertBorrowing(borrowing);

            //Eltaroljuk a kolcsonzonel ezt a kolcsonzest
            member.addBorrowing(borrowing);

            //Eltaroljuk a rendszerben ezt a kolcsonzest
            borrowings.put(finalKey, borrowing);
        } catch (Exception e) {
            //e.printStackTrace();
            throw e;
        }
    }

    /**
     * Function for deleting an existing Borrowing locally and from the database.
     * @param borrowing The Borrowing that needs to be deleted.
     * @throws Exception
     */
    public void deleteBorrowing(Borrowing borrowing) throws Exception {
        ArrayList<String> retBookISBNs = borrowing.getBookISBNs();

        //Minden konyvnel jelezzuk, hogy visszavittuk
        for (String s : retBookISBNs) {
            books.get(s).returnBook();
            database.updateBook(books.get(s));
        }

        Member member = members.get(borrowing.getLibTicketNum());

        //Ha kesve hoztuk vissza a konyveket, akkor noveljuk a kesesek szamat
        if ((new Date()).after(borrowing.getDueDate())) {
            member.setDelayCount(member.getDelayCount() + 1);

            try {
                //Ha ez az 5. kesesunk, akkor buntetest kapunk, s nullazzuk a kesesek szamat
                if (member.getDelayCount() == 5) {
                    Penalty penalty = new Penalty(member.getLibTicketNum());

                    member.setPenalty(penalty);
                    member.setDelayCount(0);
                    database.insertPenalty(penalty);
                    database.updateMember(member);
                }
            } catch (Exception e) {
                throw e;
            }
        }

        //Archivaljuk ezt a kolcsonzest
        try {
            borrowing.setArchive();
            archivedBorrowings.put(borrowing.getBorrowingNumber(), borrowing);
            database.updateBorrowing(borrowing);
        } catch (Exception e) {
            //e.printStackTrace();
            throw e;
        }

        //Toroljuk a kolcsonzotol ezt a kolcsonzest
        member.getBorrowings().remove(borrowing);

        //Vegul toroljuk ezt a kolcsonzest az aktiv kolcsonzesek kozul
        borrowings.remove(borrowing.getBorrowingNumber());
    }

    /**
     * Getter function for the local ArchivedBorrowings TreeMap.
     * @return A TreeMap of Borrowings, where the Key is the borrowingNumber, the Value is the Borrowing and every Borrowings are archived.
     */
    public TreeMap<Integer, Borrowing> getArchivedBorrowings() {
        return archivedBorrowings;
    }

    /**
     * Getter function for the local Books TreeMap.
     * @return A TreeMap of Books, where the Key is the ISBN and the Value is the Book.
     */
    public TreeMap<String, Book> getBooks() {
        return books;
    }

    /**
     * Function for locking/unlocking the given Book(s). If the Book(s) was previously locked, then it will be unlocked and vice versa.
     * @param books The Book(s) that needs to be locked/unlocked.
     * @throws Exception
     */
    public void lockUnlockBooks(ArrayList<Book> books) throws Exception {
        try {
            for (Book b : books) {
                if (b.isLocked()) {
                    b.setUnlocked();
                    database.updateBook(b);
                } else if (!b.isLocked()) {
                    b.setLocked();
                    database.updateBook(b);
                }
            }
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Function for locking the given Book(s).
     * @param books The Book(s) that needs to be locked.
     * @throws Exception
     */
    public void lockBooks(ArrayList<Book> books) throws Exception {
        try {
            for (Book b : books) {
                if (!b.isLocked()) {
                    b.setLocked();
                    database.updateBook(b);
                }
            }
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Function for unlocking the given Book(s).
     * @param books The Book(s) that needs to be unlocked.
     * @throws Exception
     */
    public void unlockBooks(ArrayList<Book> books) throws Exception {
        try {
            for (Book b : books) {
                if (b.isLocked()) {
                    b.setUnlocked();
                    database.updateBook(b);
                }
            }
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Function for updating the local storage with data from the database.
     * @throws Exception
     */
    public void refreshDataFromDatabase() throws Exception {
        try {
            members = database.getMembers();
            penalties = database.getPenalties();
            borrowings = database.getBorrowings();
            archivedBorrowings = new TreeMap<>();
            books = database.getBooks();

            //Az aktiv Borrowing-ok beallitasa a Member-ekhez, valamint ha archiv, akkor atrakjuk az archivedBorrowings-ba
            for (Borrowing b : borrowings.values()) {
                if (b.isArchive()) {
                    archivedBorrowings.put(b.getBorrowingNumber(), b);
                } else {
                    members.get(b.getLibTicketNum()).addBorrowing(b);
                }
            }

            //Az aktiv kolcsonzesek kozul kivesszuk az archivaltakat
            for (Borrowing b : archivedBorrowings.values()) {
                borrowings.remove(b.getBorrowingNumber());
            }

            //A Penalty-k beallitasa a Member-ekhez
            for (Penalty p : penalties.values()) {
                members.get(p.getLibTicketNum()).setPenalty(p);
            }

        } catch (Exception e) {
            //e.printStackTrace();
            throw e;
        }
    }

    //Ha a tagunknak lejar a buntetese, akkor toroljuk

    /**
     * Function for checking every Member's penalty and delete them, if they are expired.
     */
    private void checkPenalties() /*throws Exception*/ {
        final Runnable checker = new Runnable() {

            @Override
            public void run() {
                Date now = new Date();

                for (Member m : members.values()) {
                    if (m.hasPenalty()) {
                        System.out.println("Checking...");
                        if (now.after(m.getPenalty().getEndDate())) {
                            try {
                                database.deletePenalty(m.getPenalty());
                                m.setPenalty(null);
                            } catch (Exception ex) {
                                //throw ex;
                            }
                        }
                    }
                }
            }
        };

        //Indulas utan rogton, majd orankent ellenorizzuk a buntetesek lejartat
        scheduler.scheduleAtFixedRate(checker, 0, 1, TimeUnit.HOURS);
        //scheduler.scheduleAtFixedRate(checker, 0, 10, TimeUnit.SECONDS);  //10 masodpercenkent ellenorzi
    }

}
