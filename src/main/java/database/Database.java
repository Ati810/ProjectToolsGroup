package database;

import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.TreeMap;

import utilities.Book;
import utilities.Borrowing;
import utilities.Member;
import utilities.Penalty;

public class Database {

    private static final String JDBC_DRIVER = "oracle.jdbc.driver.OracleDriver";
    private static final String DB_URL = "jdbc:oracle:thin:@aramis.inf.elte.hu:1521:eszakigrid97";

    private Connection conn = null;
    private Statement stmt = null;

    /**
     * Constructor for establishing connection with the database.
     * @param username The username for the database connection.
     * @param password The password for the database connection.
     * @throws Exception
     */
    public Database(String username, String password) throws Exception {
        try {
            //Register JDBC driver
            Class.forName(JDBC_DRIVER);

            //STEP 3: Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, username, password);

        } catch (SQLException se) {
            //Handle errors for JDBC
            //se.printStackTrace();
            //throw se;
            throw new Exception("Invalid username/password.");

        } catch (ClassNotFoundException e) {
            //Handle errors for Class.forName
            //e.printStackTrace();
            //throw e;
            throw new Exception("There was an error with the JDBC driver.");

        } /*finally {
            //finally block used to close resources
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException se2) {
            }// nothing we can do
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }//end finally try
        }*/
    }

    /**
     * Getter function for retrieving the Members from the database.
     * @return A TreeMap of Members, where the Key is the libTicketNumber and the Value is the Member.
     * @throws Exception
     */
    public TreeMap<Integer, Member> getMembers() throws Exception {
        TreeMap<Integer, Member> members = new TreeMap<>();

        try {
            //Check if we still have the connection and if not, we throw an exception, that will cause to re-login
            if (conn == null) {
                throw new Exception("Connection lost with the database.");
            }

            //Execute a query
            System.out.println("Creating statement...");
            stmt = conn.createStatement();
            String sql;
            sql = "SELECT * FROM Member";
            ResultSet rs = stmt.executeQuery(sql);

            //Extract data from result set
            while (rs.next()) {
                //Retrieve by column name
                int libTicketNumber = rs.getInt("LibTicketNumber");
                String name = rs.getString("Name");
                String address = rs.getString("Address");
                int delayCount = rs.getInt("DelayCount");

                //Add values to TreeMap
                members.put(libTicketNumber , new Member(libTicketNumber, name, address, delayCount, null, null));
            }

            //Clean-up environment
            rs.close();
            stmt.close();

        } catch (SQLException se) {
            //se.printStackTrace();
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                throw new Exception("There was an error while closing the connection with the database.");
            }

            throw new Exception("There was an error while retrieving data from database.");

        } finally {
            //finally block used to close resources
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException se2) {
            }
        }

        return members;
    }

    /**
     * Getter function for retrieving the Books from the database.
     * @return A TreeMap of Books, where the Key is the ISBN and the Value is the Book.
     * @throws Exception
     */
    public TreeMap<String, Book> getBooks() throws Exception {
        TreeMap<String, Book> books = new TreeMap<>();

        try {
            //Check if we still have the connection and if not, we throw an exception, that will cause to re-login
            if (conn == null) {
                throw new Exception("Connection lost with the database.");
            }

            //Execute a query
            System.out.println("Creating statement...");
            stmt = conn.createStatement();
            String sql;
            sql = "SELECT * FROM Books";
            ResultSet rs = stmt.executeQuery(sql);

            //Extract data from result set
            while (rs.next()) {
                //Retrieve by column name
                String isbn = rs.getString("ISBN");
                String author = rs.getString("Author");
                String title = rs.getString("Title");
                int releaseYear = rs.getInt("ReleaseYear");
                int quantity = rs.getInt("Quantity");
                int available = rs.getInt("Available");
                int numOfBorrowings = rs.getInt("NumOfBorrowings");
                boolean locked = rs.getBoolean("Locked");

                //Add values to TreeMap
                books.put(isbn , new Book(isbn, author, title, releaseYear, quantity, available, numOfBorrowings, locked));
            }

            //Clean-up environment
            rs.close();
            stmt.close();

        } catch (SQLException se) {
            //se.printStackTrace();
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                throw new Exception("There was an error while closing the connection with the database.");
            }

            throw new Exception("There was an error while retrieving data from database.");

        } finally {
            //finally block used to close resources
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException se2) {
            }
        }

        return books;
    }

    /**
     * Getter function for retrieving the Borrowings from the database.
     * @return A TreeMap of Borrowings, where the Key is the borrowingNumber and the Value is the Borrowing.
     * @throws Exception
     */
    public TreeMap<Integer, Borrowing> getBorrowings() throws Exception {
        TreeMap<Integer, Borrowing> borrowings = new TreeMap<>();

        try {
            //Check if we still have the connection and if not, we throw an exception, that will cause to re-login
            if (conn == null) {
                throw new Exception("Connection lost with the database.");
            }

            //Execute a query
            System.out.println("Creating statement...");
            stmt = conn.createStatement();
            String sql;
            sql = "SELECT * FROM Borrowing";
            ResultSet rs = stmt.executeQuery(sql);

            //Extract data from result set
            while (rs.next()) {
                //Retrieve by column name
                int borrowingNumber = rs.getInt("BorrowingNumber");
                int libTicketNumber = rs.getInt("LibTicketNumber");
                Date borrowDate = rs.getDate("BorrowDate");
                Date dueDate = rs.getDate("DueDate");
                Date returnDate = rs.getDate("ReturnDate");
                boolean archived = rs.getBoolean("Archived");

                Struct books_ob = (Struct)rs.getObject("Books");
                Object[] books_va = books_ob.getAttributes();
                BigDecimal[] ISBNs = (BigDecimal[])((Array)books_va[0]).getArray();
                ArrayList<String> bookISBNs = new ArrayList<>();

                for (BigDecimal b : ISBNs) {
                    if (b == null) break;

                    bookISBNs.add(b.toString());
                }

                //Add values to TreeMap
                borrowings.put(borrowingNumber , new Borrowing(borrowingNumber, libTicketNumber, bookISBNs, borrowDate, dueDate, returnDate, archived));
            }

            //Clean-up environment
            rs.close();
            stmt.close();

        } catch (SQLException se) {
            //se.printStackTrace();
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                throw new Exception("There was an error while closing the connection with the database.");
            }

            throw new Exception("There was an error while retrieving data from database.");

        } finally {
            //finally block used to close resources
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException se2) {
            }
        }

        return borrowings;
    }

    /**
     * Getter function for retrieving the Penalties from the database.
     * @return A TreeMap of Penalties, where the Key is the libTicketNumber and the Value is the Penalty.
     * @throws Exception
     */
    public TreeMap<Integer, Penalty> getPenalties() throws Exception {
        TreeMap<Integer, Penalty> penalties = new TreeMap<>();

        try {
            //Check if we still have the connection and if not, we throw an exception, that will cause to re-login
            if (conn == null) {
                throw new Exception("Connection lost with the database.");
            }

            //Execute a query
            System.out.println("Creating statement...");
            stmt = conn.createStatement();
            String sql;
            sql = "SELECT * FROM Penalty";
            ResultSet rs = stmt.executeQuery(sql);

            //Extract data from result set
            while (rs.next()) {
                //Retrieve by column name
                int libTicketNumber = rs.getInt("LibTicketNumber");
                Date startDate = rs.getDate("StartDate");
                Date midDate = rs.getDate("MidDate");
                Date endDate = rs.getDate("EndDate");

                //Add values to TreeMap
                penalties.put(libTicketNumber , new Penalty(libTicketNumber, startDate, midDate, endDate));
            }

            //Clean-up environment
            rs.close();
            stmt.close();

        } catch (SQLException se) {
            //se.printStackTrace();
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                throw new Exception("There was an error while closing the connection with the database.");
            }

            throw new Exception("There was an error while retrieving data from database.");

        } finally {
            //finally block used to close resources
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException se2) {
            }
        }

        return penalties;
    }

    /**
     * Function for inserting a new Member into the database.
     * @param member The Member that needs to be inserted into the database.
     * @throws Exception
     */
    public void insertMember(Member member) throws Exception {
        try {
            //Check if we still have the connection and if not, we throw an exception, that will cause to re-login
            if (conn == null) {
                throw new Exception("Connection lost with the database.");
            }

            //Execute a query
            System.out.println("Inserting records into the table...");

            String sql = "INSERT INTO Member " +
                    "VALUES (?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareCall(sql);
            ps.setInt(1, member.getLibTicketNum());
            ps.setString(2, member.getName());
            ps.setString(3, member.getAddress());
            ps.setInt(4, member.getDelayCount());
            ps.executeUpdate();

            System.out.println("Inserted records into the table...");

        } catch (SQLException se) {
            //se.printStackTrace();
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                throw new Exception("There was an error while closing the connection with the database.");
            }

            throw new Exception("There was an error while inserting data into the database.");

        } finally {
            //finally block used to close resources
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException se2) {
            }
        }
    }

    /**
     * Function for deleting an existing Member from the database.
     * @param member The Member that needs to be deleted from the database.
     * @throws Exception
     */
    public void deleteMember(Member member) throws Exception {
        try {
            //Check if we still have the connection and if not, we throw an exception, that will cause to re-login
            if (conn == null) {
                throw new Exception("Connection lost with the database.");
            }

            //Execute a query
            System.out.println("Deleting records from the table...");

            String sql = "DELETE FROM Member " +
                    "WHERE LibTicketNumber = ?";
            PreparedStatement ps = conn.prepareCall(sql);
            ps.setInt(1, member.getLibTicketNum());
            ps.executeUpdate();

            System.out.println("Deleted records from the table...");

        } catch (SQLException se) {
            //se.printStackTrace();
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                throw new Exception("There was an error while closing the connection with the database.");
            }

            throw new Exception("There was an error while deleting data from the database.");

        } finally {
            //finally block used to close resources
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException se2) {
            }
        }
    }

    /**
     * Function for updating an existing Member in the database.
     * @param member The Member that needs to be updated in the database.
     * @throws Exception
     */
    public void updateMember(Member member) throws Exception {
        try {
            //Check if we still have the connection and if not, we throw an exception, that will cause to re-login
            if (conn == null) {
                throw new Exception("Connection lost with the database.");
            }

            //Execute a query
            System.out.println("Creating statement...");

            String sql = "UPDATE Member " +
                    "SET Name = ?, Address = ?, DelayCount = ?" +
                    "WHERE LibTicketNumber = ?";
            PreparedStatement ps = conn.prepareCall(sql);
            ps.setString(1, member.getName());
            ps.setString(2, member.getAddress());
            ps.setInt(3, member.getDelayCount());
            ps.setInt(4, member.getLibTicketNum());
            ps.executeUpdate();

            System.out.println("Table has been updated...");

        } catch (SQLException se) {
            //se.printStackTrace();
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                throw new Exception("There was an error while closing the connection with the database.");
            }

            throw new Exception("There was an error while updating data in the database.");

        } finally {
            //finally block used to close resources
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException se2) {
            }
        }
    }

    /**
     * Function for updating an existing Book in the database.
     * @param book The Book that needs to be updated in the database.
     * @throws Exception
     */
    public void updateBook(Book book) throws Exception {
        try {
            //Check if we still have the connection and if not, we throw an exception, that will cause to re-login
            if (conn == null) {
                throw new Exception("Connection lost with the database.");
            }

            //Execute a query
            System.out.println("Creating statement...");

            String sql = "UPDATE Books " +
                    "SET Available = ?, NumOfBorrowings = ?, Locked = ?" +
                    "WHERE ISBN = ?";
            PreparedStatement ps = conn.prepareCall(sql);
            ps.setInt(1, book.getAvailable());
            ps.setInt(2, book.getNumOfBorrowings());
            ps.setInt(3, (book.isLocked() ? 1 : 0));
            ps.setBigDecimal(4, new BigDecimal(book.getISBN()));
            ps.executeUpdate();

            System.out.println("Table has been updated...");

        } catch (SQLException se) {
            se.printStackTrace();
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                throw new Exception("There was an error while closing the connection with the database.");
            }

            throw new Exception("There was an error while updating data in the database.");

        } finally {
            //finally block used to close resources
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException se2) {
            }
        }
    }

    /**
     * Function for inserting a new Penalty into the database.
     * @param penalty The Penalty that needs to be inserted into the database.
     * @throws Exception
     */
    public void insertPenalty(Penalty penalty) throws Exception {
        try {
            //Check if we still have the connection and if not, we throw an exception, that will cause to re-login
            if (conn == null) {
                throw new Exception("Connection lost with the database.");
            }

            //Execute a query
            System.out.println("Inserting records into the table...");

            String sql = "INSERT INTO Penalty " +
                    "VALUES (?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareCall(sql);
            ps.setInt(1, penalty.getLibTicketNum());
            ps.setDate(2, new Date(penalty.getStartDate().getTime()));
            ps.setDate(3, new Date(penalty.getMidDate().getTime()));
            ps.setDate(4, new Date(penalty.getEndDate().getTime()));
            ps.executeUpdate();

            System.out.println("Inserted records into the table...");

        } catch (SQLException se) {
            //se.printStackTrace();
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                throw new Exception("There was an error while closing the connection with the database.");
            }

            throw new Exception("There was an error while inserting data into the database.");

        } finally {
            //finally block used to close resources
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException se2) {
            }
        }
    }

    /**
     * Function for deleting an existing Penalty from the database.
     * @param penalty The Penalty that needs to be deleted from the database.
     * @throws Exception
     */
    public void deletePenalty(Penalty penalty) throws Exception {
        try {
            //Check if we still have the connection and if not, we throw an exception, that will cause to re-login
            if (conn == null) {
                throw new Exception("Connection lost with the database.");
            }

            //Execute a query
            System.out.println("Deleting records from the table...");

            String sql = "DELETE FROM Penalty " +
                    "WHERE LibTicketNumber = ?";
            PreparedStatement ps = conn.prepareCall(sql);
            ps.setInt(1, penalty.getLibTicketNum());
            ps.executeUpdate();

            System.out.println("Deleted records from the table...");

        } catch (SQLException se) {
            //se.printStackTrace();
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                throw new Exception("There was an error while closing the connection with the database.");
            }

            throw new Exception("There was an error while deleting data from the database.");

        } finally {
            //finally block used to close resources
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException se2) {
            }
        }
    }

    /**
     * Function for inserting a new Borrowing into the database.
     * @param borrowing The Borrowing that needs to be inserted into the database.
     * @throws Exception
     */
    public void insertBorrowing(Borrowing borrowing) throws Exception {
        try {
            //Check if we still have the connection and if not, we throw an exception, that will cause to re-login
            if (conn == null) {
                throw new Exception("Connection lost with the database.");
            }

            //Execute a query
            System.out.println("Inserting records into the table...");

            String sql = "INSERT INTO Borrowing " +
                    "VALUES (?, ?, ?, ?, ?, ?, books_ob(books_va(?, ?, ?, ?)))";
            PreparedStatement ps = conn.prepareCall(sql);
            ps.setInt(1, borrowing.getBorrowingNumber());
            ps.setInt(2, borrowing.getLibTicketNum());
            ps.setDate(3, new Date(borrowing.getBorrowDate().getTime()));
            ps.setDate(4, new Date(borrowing.getDueDate().getTime()));
            ps.setDate(5, (borrowing.getReturnDate() == null ? null : new Date(borrowing.getReturnDate().getTime())));
            ps.setInt(6, (borrowing.isArchive() ? 1 : 0));
            ps.setBigDecimal(7, new BigDecimal(borrowing.getBookISBNs().get(0)));
            ps.setBigDecimal(8, (borrowing.getBookISBNs().size() >= 2 ? new BigDecimal(borrowing.getBookISBNs().get(1)) : null));
            ps.setBigDecimal(9, (borrowing.getBookISBNs().size() >= 3 ? new BigDecimal(borrowing.getBookISBNs().get(2)) : null));
            ps.setBigDecimal(10, (borrowing.getBookISBNs().size() >= 4 ? new BigDecimal(borrowing.getBookISBNs().get(3)) : null));
            ps.executeUpdate();

            System.out.println("Inserted records into the table...");

        } catch (SQLException se) {
            //se.printStackTrace();
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                throw new Exception("There was an error while closing the connection with the database.");
            }

            throw new Exception("There was an error while inserting data into the database.");

        } finally {
            //finally block used to close resources
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException se2) {
            }
        }
    }

    /**
     * Function for updating an existing Borrowing in the database.
     * @param borrowing The Borrowing that needs to be updated in the database.
     * @throws Exception
     */
    public void updateBorrowing(Borrowing borrowing) throws Exception {
        try {
            //Check if we still have the connection and if not, we throw an exception, that will cause to re-login
            if (conn == null) {
                throw new Exception("Connection lost with the database.");
            }

            //Execute a query
            System.out.println("Creating statement...");

            String sql = "UPDATE Borrowing " +
                    "SET ReturnDate = ?, Archived = ?" +
                    "WHERE BorrowingNumber = ?";
            PreparedStatement ps = conn.prepareCall(sql);
            ps.setDate(1, (borrowing.getReturnDate() == null ? null : new Date(borrowing.getReturnDate().getTime())));
            ps.setInt(2, (borrowing.isArchive() ? 1 : 0));
            ps.setInt(3, borrowing.getBorrowingNumber());
            ps.executeUpdate();

            System.out.println("Table has been updated...");

        } catch (SQLException se) {
            //se.printStackTrace();
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                throw new Exception("There was an error while closing the connection with the database.");
            }

            throw new Exception("There was an error while updating data in the database.");

        } finally {
            //finally block used to close resources
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException se2) {
            }
        }
    }

}
