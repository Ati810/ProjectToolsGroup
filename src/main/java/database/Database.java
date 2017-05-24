package database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
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
import java.util.logging.Level;
import java.util.logging.Logger;

import utilities.Book;
import utilities.Borrowing;
import utilities.Member;
import utilities.Penalty;

/**
 * Class that connects to the database, and manages it.
 * 
 */
public class Database {

    private static final String JDBC_DRIVER = "oracle.jdbc.driver.OracleDriver";
    private static final String DEFAULT_DB_URL = "jdbc:oracle:thin:@aramis.inf.elte.hu:1521:eszakigrid97";

    private Connection conn = null;
    private Statement stmt = null;

    /**
     * Constructor for establishing connection with the default database, or the
     * one specified in the config.cfg(only sqlite supported int the config).
     *
     * @param username The username for the database connection.
     * @param password The password for the database connection.
     * @throws Exception
     */
    public Database(String username, String password) throws Exception {

        File configFile = new File("config.cfg");
        String DB_URL = null;
        boolean NEW_DATA_BASE = false;
        if (configFile.isFile()) {
            BufferedReader br = new BufferedReader(new FileReader(configFile));
           
            String line = br.readLine();
            while (line != null) {
                if (line.contains("DB_URL=")) {
                    DB_URL = line.split("=")[1];
                }else if (line.contains("NEW_DATA_BASE=true")) {
                    NEW_DATA_BASE = true;
                }else{
                }
                line = br.readLine();
            }
            br.close();
             PrintWriter pw = new PrintWriter(new FileWriter(configFile));
             pw.println("DB_URL="+((DB_URL != null)?DB_URL:Database.DEFAULT_DB_URL));
            pw.close();
        }
        if (DB_URL != null) {
            try {
                Class.forName(JDBC_DRIVER);
                System.out.println("Connecting to database...");
                conn = DriverManager.getConnection(DB_URL, username, password);
                Database.setupDatabaseStructure(this.conn);
                if (NEW_DATA_BASE) {
                    Database.setupDatabaseStructure(this.conn);
                    Database.fillDatabase(this.conn);
                }
            } catch (Exception se) {
                this.fallBackToDefault(username, password);
            }
        } else {
            this.fallBackToDefault(username, password);
        }
    }

    /**
     * Getter function for retrieving the Members from the database.
     *
     * @return A TreeMap of Members, where the Key is the libTicketNumber and
     * the Value is the Member.
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
                members.put(libTicketNumber, new Member(libTicketNumber, name, address, delayCount, null, null));
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
     *
     * @return A TreeMap of Books, where the Key is the ISBN and the Value is
     * the Book.
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
                books.put(isbn, new Book(isbn, author, title, releaseYear, quantity, available, numOfBorrowings, locked));
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
     *
     * @return A TreeMap of Borrowings, where the Key is the borrowingNumber and
     * the Value is the Borrowing.
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

                Struct books_ob = (Struct) rs.getObject("Books");
                Object[] books_va = books_ob.getAttributes();
                BigDecimal[] ISBNs = (BigDecimal[]) ((Array) books_va[0]).getArray();
                ArrayList<String> bookISBNs = new ArrayList<>();

                for (BigDecimal b : ISBNs) {
                    if (b == null) {
                        break;
                    }

                    bookISBNs.add(b.toString());
                }

                //Add values to TreeMap
                borrowings.put(borrowingNumber, new Borrowing(borrowingNumber, libTicketNumber, bookISBNs, borrowDate, dueDate, returnDate, archived));
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
     *
     * @return A TreeMap of Penalties, where the Key is the libTicketNumber and
     * the Value is the Penalty.
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
                penalties.put(libTicketNumber, new Penalty(libTicketNumber, startDate, midDate, endDate));
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
     *
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

            String sql = "INSERT INTO Member "
                    + "VALUES (?, ?, ?, ?)";
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
     *
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

            String sql = "DELETE FROM Member "
                    + "WHERE LibTicketNumber = ?";
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
     *
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

            String sql = "UPDATE Member "
                    + "SET Name = ?, Address = ?, DelayCount = ?"
                    + "WHERE LibTicketNumber = ?";
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
     *
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

            String sql = "UPDATE Books "
                    + "SET Available = ?, NumOfBorrowings = ?, Locked = ?"
                    + "WHERE ISBN = ?";
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
     *
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

            String sql = "INSERT INTO Penalty "
                    + "VALUES (?, ?, ?, ?)";
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
     *
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

            String sql = "DELETE FROM Penalty "
                    + "WHERE LibTicketNumber = ?";
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
     *
     * @param borrowing The Borrowing that needs to be inserted into the
     * database.
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

            String sql = "INSERT INTO Borrowing "
                    + "VALUES (?, ?, ?, ?, ?, ?, books_ob(books_va(?, ?, ?, ?)))";
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
     *
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

            String sql = "UPDATE Borrowing "
                    + "SET ReturnDate = ?, Archived = ?"
                    + "WHERE BorrowingNumber = ?";
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

    /**
     * The fallback function from the constructor.
     *
     * @param username The username for the database connection.
     * @param password The password for the database connection.
     * @throws Exception
     */
    private void fallBackToDefault(String username, String password) throws Exception {
        try {
            //Register JDBC driver
            Class.forName(JDBC_DRIVER);

            //STEP 3: Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DEFAULT_DB_URL, username, password);

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

        }
        /*finally {
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
     * Function for  seting up the new database's structure
     */
    
    private static void setupDatabaseStructure(Connection conn) {
        try {
            conn.createStatement().executeUpdate("create table Member\n"
                    + "(\n"
                    + "    LibTicketNumber INT not null,\n"
                    + "    Name VARCHAR2(30) not null,\n"
                    + "    constraint Name_CH check (LENGTH(Name) > 0 AND LENGTH(Name) <= 30),\n"
                    + "    Address VARCHAR2(150) not null,\n"
                    + "    constraint Address_CH check (LENGTH(Address) > 0 AND LENGTH(Address) <= 150),\n"
                    + "    DelayCount INT default 0 not null,\n"
                    + "    constraint DelayCount_CH check (DelayCount <= 4),\n"
                    + "    primary key (LibTicketNumber)\n"
                    + ");\n"
                    + "\n"
                    + "create table Penalty\n"
                    + "(\n"
                    + "    LibTicketNumber INT not null,\n"
                    + "    StartDate DATE not null,\n"
                    + "    MidDate DATE not null,\n"
                    + "    EndDate DATE not null,\n"
                    + "    constraint Date_CH check (StartDate < EndDate),\n"
                    + "    constraint MidDate_CH check (StartDate < MidDate AND MidDate < EndDate),\n"
                    + "    primary key (LibTicketNumber)\n"
                    + ");\n"
                    + "\n"
                    + "INSERT INTO PENALTY\n"
                    + "VALUES(2, SYSDATE, SYSDATE + 7, SYSDATE + 14);\n"
                    + "COMMIT;\n"
                    + "\n"
                    + "\n"
                    + "create type books_va as varray(4) of NUMBER(13, 0);\n"
                    + "\n"
                    + "create type books_ob as object\n"
                    + "(\n"
                    + "    ISBNs books_va\n"
                    + ");\n"
                    + "\n"
                    + "create table Borrowing\n"
                    + "(\n"
                    + "    BorrowingNumber INT not null,\n"
                    + "    LibTicketNumber INT not null,\n"
                    + "    BorrowDate DATE default SYSDATE not null,\n"
                    + "    DueDate DATE not null,\n"
                    + "    constraint BorrowDue_CH check (BorrowDate < DueDate),\n"
                    + "    ReturnDate DATE,\n"
                    + "    --constraint ReturnDate_CH check (ReturnDate > BorrowDate),\n"
                    + "    Archived NUMBER(1, 0) default 0 not null,   --1 - igaz, 0 - hamis\n"
                    + "    Books books_ob not null,\n"
                    + "    primary key (BorrowingNumber)\n"
                    + ");\n"
                    + "\n"
                    + "INSERT INTO BORROWING\n"
                    + "VALUES(1, 1, DEFAULT, SYSDATE + 14, NULL, 0, books_ob(books_va(1616100754499, 1681021454699, 1650021082899, 1632111693599)));\n"
                    + "COMMIT;\n"
                    + "\n"
                    + "INSERT INTO BORROWING\n"
                    + "VALUES(25, 3, SYSDATE - 15, SYSDATE - 1, NULL, 0, books_ob(books_va(1616100754499, 1681021454699, 1650021082899, 1632111693599)));\n"
                    + "COMMIT;\n"
                    + "\n"
                    + "\n"
                    + "create table Books\n"
                    + "(\n"
                    + "    ISBN NUMBER(13, 0) not null,\n"
                    + "    Author VARCHAR2(30) not null,\n"
                    + "    Title VARCHAR2(50) not null,\n"
                    + "    ReleaseYear INT,\n"
                    + "    Quantity INT not null,\n"
                    + "    constraint Quantity_CH check (Quantity >= 1),\n"
                    + "    Available INT not null,\n"
                    + "    constraint Available_CH check (Available <= Quantity),\n"
                    + "    NumOfBorrowings INT default 0 not null,\n"
                    + "    Locked NUMBER(1, 0) default 0 not null,   --1 - igaz, 0 - hamis\n"
                    + "    primary key (ISBN)\n"
                    + ");");
        } catch (SQLException ex) {
            System.out.println("There was an error while creating the database's structure");
        }
    }

    /**
    * Function for fill up the new database with elements
    */
    
    private static void fillDatabase(Connection conn) {
        try {
            conn.createStatement().executeUpdate("INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (1,'Joy Monroe','542-5602 Auctor, Street',3);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (2,'Chloe Nicholson','966-9205 Non Street',2);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (3,'Kylan Lowe','392-1046 Tincidunt Ave',4);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (4,'Drew Curtis','Ap #510-8725 Nunc Rd.',3);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (5,'Rhoda Vargas','P.O. Box 527, 2768 Rutrum Rd.',1);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (6,'Zena Gilliam','P.O. Box 911, 6356 Nunc. St.',3);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (7,'Lane Hopkins','Ap #700-171 Cursus St.',0);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (8,'Alvin Wall','6223 Porttitor Street',4);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (9,'Zeph Riley','Ap #473-2312 Consequat Ave',2);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (10,'Nomlanga Bonner','Ap #540-6833 Nullam St.',4);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (11,'Graham Franklin','Ap #671-9298 Lectus St.',4);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (12,'Madeson Britt','Ap #179-912 Mus. St.',2);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (13,'Erich Wagner','Ap #386-9631 Dignissim Road',4);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (14,'Amir Allison','P.O. Box 501, 1201 Bibendum Road',4);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (15,'Damian Hinton','Ap #171-2097 Pharetra Rd.',0);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (16,'Calista Conway','2081 Sit St.',2);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (17,'Evan Harding','508-1602 Eros Road',1);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (18,'Blake Herring','821-630 Mattis. Road',0);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (19,'Lysandra Dickson','7317 Cras Avenue',4);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (20,'Ivana Castro','P.O. Box 675, 9235 Morbi Road',1);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (21,'Kim Matthews','P.O. Box 969, 5680 Sapien. Rd.',1);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (22,'Maisie Keller','P.O. Box 297, 3927 Morbi Rd.',0);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (23,'Judith Russell','Ap #275-2283 Nullam Road',0);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (24,'Clementine Rosario','P.O. Box 112, 4514 Vivamus Road',3);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (25,'Ariana Short','P.O. Box 471, 3252 Libero. Rd.',2);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (26,'Delilah Wood','4670 Tempor Ave',4);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (27,'Keely Franks','2774 Id, St.',2);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (28,'Lael Moon','P.O. Box 685, 6554 Non Rd.',2);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (29,'Tobias Stevenson','674 Dolor St.',3);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (30,'Regan Martinez','3920 Nisi. Street',4);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (31,'Maya Woodard','4682 Est, Rd.',4);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (32,'Sandra Moreno','P.O. Box 746, 6721 Sociosqu Rd.',1);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (33,'Cyrus Keith','Ap #149-1593 Cubilia Rd.',0);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (34,'Zephr Fleming','Ap #465-7954 Nisi Rd.',4);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (35,'Orlando Meyers','972-9698 Malesuada Street',4);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (36,'Angelica Durham','366-2975 Amet, St.',1);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (37,'Fuller Maldonado','P.O. Box 448, 8819 Dolor. Road',4);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (38,'Nevada Slater','437-6468 Dolor Rd.',0);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (39,'Mercedes Carver','757-9388 Arcu Road',0);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (40,'Ursa Robinson','P.O. Box 359, 478 Leo. Rd.',3);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (41,'Felicia Coleman','P.O. Box 188, 7735 Sit Rd.',3);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (42,'Jane Woodward','787 Libero Road',2);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (43,'Cooper Oneal','985-1884 Ac St.',2);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (44,'Rowan Le','Ap #140-3658 Tristique Street',3);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (45,'Xenos Goodwin','P.O. Box 927, 2211 Eu Ave',0);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (46,'Maxine Frederick','P.O. Box 982, 8290 Curae; Street',0);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (47,'Ryder Molina','Ap #446-4638 Non St.',1);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (48,'Arden Mcmahon','Ap #778-5022 Duis Street',3);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (49,'Leigh Preston','622-8547 Risus Avenue',0);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (50,'Xander Morton','2944 Vulputate Ave',3);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (51,'Cynthia Vang','Ap #462-2061 Nonummy Rd.',2);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (52,'Adam Decker','P.O. Box 678, 3039 Cursus Rd.',1);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (53,'Cally Klein','6005 Auctor, Ave',1);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (54,'Clark Sears','4848 Pharetra. Rd.',4);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (55,'Tate Patrick','P.O. Box 126, 9364 Interdum. Street',0);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (56,'Leigh Bryan','640-1194 Et Ave',3);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (57,'Hedwig Young','Ap #609-8109 Lorem Rd.',0);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (58,'Vance Singleton','P.O. Box 822, 7034 Eu, Ave',3);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (59,'Paula Love','Ap #142-1697 At Rd.',4);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (60,'Yoshi Mullen','816 Aliquam St.',4);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (61,'Cheryl Glenn','791-6569 Id Rd.',1);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (62,'Samantha West','P.O. Box 600, 2395 Sit Rd.',3);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (63,'Damian English','Ap #182-567 Egestas St.',0);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (64,'Dylan Macdonald','P.O. Box 674, 296 Eu, Ave',3);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (65,'Sonia Moses','5457 Lacus. St.',4);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (66,'Cynthia Sargent','Ap #439-5786 Non, Ave',0);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (67,'Jolie Hicks','6973 Neque St.',4);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (68,'Alea Sawyer','5063 Tristique Avenue',1);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (69,'Nero Hanson','871-2413 Phasellus Street',3);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (70,'Harrison Hardin','P.O. Box 909, 7062 Egestas, Ave',3);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (71,'Teegan Lowery','992-5733 Quis St.',1);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (72,'Levi Ruiz','659-5631 Dapibus Av.',0);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (73,'Brielle Holcomb','Ap #207-6695 Ullamcorper. Av.',1);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (74,'Baxter Nash','P.O. Box 359, 7347 Suspendisse Road',3);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (75,'Joan Pope','794-6851 Quis Rd.',4);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (76,'Ciara Manning','Ap #990-9867 Elementum Rd.',4);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (77,'Mary Burnett','P.O. Box 699, 6076 Ornare St.',1);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (78,'Alyssa Dominguez','P.O. Box 227, 823 Vitae Street',1);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (79,'Tate Bentley','P.O. Box 447, 294 Non, Street',2);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (80,'Jenna Clarke','P.O. Box 255, 1252 Accumsan Street',2);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (81,'Zena Ferrell','P.O. Box 124, 8221 Scelerisque Rd.',2);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (82,'Jarrod Sawyer','247-2646 Aliquet, Ave',3);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (83,'Gisela Mcconnell','726-2650 Fermentum St.',1);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (84,'Helen Mclaughlin','8441 Elit Road',0);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (85,'Tyler Witt','Ap #958-1401 Euismod Road',1);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (86,'Sylvester Marsh','Ap #795-1510 A, Ave',3);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (87,'Dustin Becker','P.O. Box 687, 7523 Pulvinar Avenue',0);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (88,'Ruth Tyson','P.O. Box 524, 9140 Dolor Rd.',4);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (89,'Blythe Daugherty','640-6514 Id, Av.',4);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (90,'Violet Haney','899-7150 Commodo Street',1);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (91,'Felix Jackson','767-6377 Ante Rd.',2);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (92,'Dean Hunter','4574 Magna. Rd.',4);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (93,'Jaime Ayers','843 At, Rd.',3);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (94,'Fiona Cherry','886-8148 Etiam Rd.',0);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (95,'Kai Randall','444-3706 Sed Avenue',4);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (96,'Brent Holmes','3272 Commodo St.',3);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (97,'Carissa Cameron','Ap #900-6039 Arcu Street',0);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (98,'Stephanie Love','Ap #967-7191 A Rd.',0);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (99,'Carol Mccarty','5345 Vestibulum Street',1);\n"
                    + "INSERT INTO Member (LibTicketNumber,Name,Address,DelayCount) VALUES (100,'Nomlanga Gilmore','Ap #423-8064 Tristique Street',2);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1655092186299','Driscoll Carr','porta',1951,78,41,61,0);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1601042390699','Laura Castaneda','sed',1880,73,73,45,1);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1608030887499','Ira Marsh','a odio semper cursus.',1848,45,45,45,1);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1611041408599','Kyra Fox','Fusce mi lorem, vehicula',1833,51,51,48,1);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1690112664599','Jennifer Savage','In',1949,71,71,36,0);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1677030771399','Rose Beard','iaculis, lacus pede',2008,25,15,44,0);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1611100593299','Xanthus Carson','lacus. Quisque imperdiet, erat nonummy',1928,59,59,57,0);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1644052975399','Meredith Crawford','bibendum. Donec',1820,48,3,25,1);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1693051366599','Chava Emerson','sed',1970,75,72,87,0);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1636061811499','Dawn Potter','amet',1818,56,2,9,1);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1666090112699','Erin Beck','eget mollis lectus pede',1835,78,78,10,1);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1639051478199','Jonah Wheeler','metus. Vivamus euismod',1836,90,90,3,1);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1673042333799','Drew Sears','fermentum',1847,82,82,45,1);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1697081711699','Deacon Norris','Donec',1857,100,81,6,0);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1610100487799','Gail Bonner','ipsum. Curabitur consequat, lectus',1817,46,42,50,0);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1651071866899','Rebekah Alford','malesuada ut,',1925,22,3,50,1);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1616071736299','Octavia Rivers','lectus quis massa. Mauris',1865,5,5,23,0);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1643112995399','Madison Mills','eu erat',2007,75,40,81,0);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1624101613899','Wynter Huff','rhoncus. Proin',2009,52,52,22,1);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1614052712099','TaShya Mcintyre','eu',1949,38,38,83,0);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1638112794599','Pamela Hines','magna, malesuada vel, convallis',2015,40,23,83,0);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1675120501899','Desiree Conley','elit, pretium et, rutrum',2004,38,3,12,0);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1651102042799','Merrill Dillon','Suspendisse',1956,69,69,43,1);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1614062315099','Barclay Parrish','tempus non, lacinia at,',1964,31,31,49,1);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1618122423799','Giselle Mccullough','massa',1891,57,25,77,1);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1697041474599','Stacy Jefferson','quam.',1810,72,65,87,1);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1603112279399','Nerea Hodges','rutrum',1934,7,7,2,1);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1662081369399','Tyler Snow','nibh.',1982,88,72,1,1);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1655050438399','Justin Moore','orci luctus et',1877,79,77,98,1);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1684030433499','Nasim Dixon','auctor, velit eget laoreet',1857,78,28,9,0);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1666062299199','Victoria Atkins','Donec fringilla.',1835,57,57,85,0);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1632040149999','Veronica Barlow','auctor. Mauris vel turpis. Aliquam',1942,53,53,94,0);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1670070678199','Irene Glenn','Vivamus nisi.',1947,41,37,58,0);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1697120446099','Henry Ramirez','id enim. Curabitur',1821,67,5,44,1);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1669042576699','Shea Sanchez','enim. Curabitur',1843,17,17,13,1);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1689040393999','Caldwell Parker','ligula.',1992,13,13,90,0);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1635010578199','Molly Estes','libero nec',2017,52,6,40,0);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1605071662599','Xandra Roy','lectus ante dictum',1817,21,21,42,0);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1634110328799','Bryar Wells','rutrum urna,',1902,56,4,94,0);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1675020325199','Sean Riddle','eget, dictum placerat, augue. Sed',1854,29,29,95,1);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1653071902799','Aiko Hopper','sapien imperdiet ornare.',1975,9,4,37,1);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1604070248299','Lenore Pitts','bibendum fermentum metus.',1853,77,26,85,0);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1619072374399','Leonard Parsons','lacus pede sagittis',2005,36,36,72,0);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1603012547599','Dai Kennedy','Quisque',2000,57,9,1,1);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1676121770799','Kim Walls','eu',1831,12,7,95,1);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1652070911799','Benjamin Martin','Quisque tincidunt',1953,54,54,52,0);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1623120793499','Glenna Garcia','Donec elementum, lorem',1895,25,25,93,1);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1637121965999','Kenneth Espinoza','Duis',2006,21,21,19,1);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1647091604499','Willa Case','Suspendisse ac metus',1942,90,77,89,1);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1646100187299','Ezra Church','Donec',1851,46,46,85,0);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1681092224899','Tallulah Santos','ultrices',1855,7,7,63,0);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1645122210899','Keely Cross','cursus purus. Nullam',1924,80,28,24,1);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1652021633699','Brianna Cohen','est, congue a, aliquet',1986,54,16,29,1);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1685122630099','Martin Rivas','dis parturient montes,',1803,87,45,96,0);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1672032569899','Kirk Shields','amet massa.',1897,25,25,47,1);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1669120203299','Damon Clements','adipiscing lacus.',2009,55,0,15,0);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1686051560599','Renee Salas','elit,',1953,78,10,84,1);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1636020214699','Tanya Hawkins','ornare',1804,14,14,4,1);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1693111062799','Kiara Chambers','Nullam feugiat placerat velit. Quisque',1998,62,21,7,0);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1692023058799','Venus Dyer','vehicula et, rutrum',1864,28,28,82,0);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1650101912699','Keaton Odom','non',1882,67,67,75,1);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1667020711699','Francis Hester','lectus',1956,19,19,12,1);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1665060575199','Brooke Sargent','amet ante. Vivamus non',1810,9,4,1,1);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1612081043199','Zenia Russell','penatibus et',1927,48,36,83,1);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1636082573199','Rose Hays','lectus justo eu',1933,56,17,19,0);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1621040361899','Gretchen Tucker','dolor vitae dolor. Donec',2017,53,53,88,1);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1615021342999','Nerea Prince','imperdiet non, vestibulum',1836,88,67,93,0);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1659061078899','Larissa Estrada','egestas a, scelerisque',1825,27,27,67,0);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1684050693099','Hamilton Sweeney','velit justo nec',1816,90,51,17,1);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1674022668599','Blair Olsen','dignissim pharetra.',1870,50,12,14,1);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1619092287699','Carson Rice','Integer sem',1894,68,33,79,0);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1630062068099','Aimee Allen','habitant morbi tristique senectus',1865,57,11,71,0);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1624032512899','Hedwig Lawson','ac arcu.',2012,1,1,13,0);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1677100355399','Ira Mccarty','aliquet magna',1871,45,40,70,0);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1619022454599','Gisela Mercer','neque. Nullam ut',1892,5,5,13,0);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1654093066199','Flavia Bowen','penatibus et magnis',1871,27,27,53,1);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1695122863199','Merritt Bender','sapien, gravida non, sollicitudin a,',1971,71,22,67,1);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1601012710499','Cyrus Mercado','dui. Fusce',2015,24,21,12,1);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1678121169399','Dora Ortega','Donec egestas.',1910,68,33,55,0);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1638052519999','Kasper Snyder','amet diam',1926,35,35,36,1);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1611090929699','Hayley Arnold','montes, nascetur ridiculus mus.',2013,44,31,9,1);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1698080616299','Rafael Jefferson','amet',1829,41,41,20,0);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1604061115599','Olivia Hahn','Suspendisse aliquet',1921,48,48,71,0);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1609012518599','Lael Stuart','nec urna et',1880,49,49,72,1);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1677081757199','Dakota Webb','habitant',1852,5,3,58,1);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1620070849499','Belle Simon','dolor. Nulla semper tellus id',1925,50,50,87,1);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1641100626399','Kimberly Kramer','ultricies sem magna',1998,6,3,72,0);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1665050240299','Vera Steele','imperdiet',1898,19,19,45,0);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1682021409299','Logan Trevino','Nunc ac sem ut',1920,43,43,91,0);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1600010736799','Prescott Howe','nostra, per inceptos hymenaeos. Mauris',1890,21,21,8,0);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1694021576899','Knox Cooley','pede et risus. Quisque libero',1835,81,63,65,0);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1631081218099','Ryan Carey','tincidunt, neque vitae',1971,25,2,8,1);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1644032329099','Wynter Marshall','placerat, orci',1910,70,26,33,1);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1669091479999','Briar Paul','sit',1982,3,3,70,1);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1679042891399','Xanthus Moreno','massa lobortis ultrices.',1815,85,85,14,1);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1649063073099','Quamar Bryant','sodales',1950,25,25,98,1);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1687092823199','Cedric Herrera','tincidunt. Donec vitae',1883,25,25,25,1);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1662080536099','Grace Salinas','dolor. Nulla',1931,6,6,86,1);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1614042496699','Willow Mcdonald','ligula. Aenean gravida nunc',1977,33,19,63,0);\n"
                    + "INSERT INTO Books (ISBN,Author,Title,ReleaseYear,Quantity,Available,NumOfBorrowings,Locked) VALUES ('1673040329899','Inga Fry','in, tempus eu,',1910,52,26,98,0);");
        } catch (SQLException ex) {
            System.out.println("There was an error while creating the database's structure");
        }
    }

}
