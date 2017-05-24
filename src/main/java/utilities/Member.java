package utilities;

import java.util.ArrayList;

/**
 * Class that represents a Member.
 * @author Attila
 */
public class Member {

    private int libTicketNum;
    
    private String name;

    private String address;

    private int delayCount;

    private ArrayList<Borrowing> borrowings;

    private Penalty penalty;

    //Programon beluli Member letrehozasahoz (ekkor generalodik a libTicketNum).

    /**
     *
     * @param libTicketNum
     * @param name
     * @param address
     */
        public Member(int libTicketNum, String name, String address) {
        this.libTicketNum = libTicketNum;
        this.name = name;
        this.address = address;
        this.delayCount = 0;
        this.borrowings = new ArrayList<>();
        this.penalty = null;
    }

    //Ezt a konstruktort hasznaljuk, amikor az adatbazisbol kerjuk le az adatokat.

    /**
     *
     * @param libTicketNum
     * @param name
     * @param address
     * @param delayCount
     * @param borrowings
     * @param penalty
     */
        public Member(int libTicketNum, String name, String address, int delayCount, ArrayList<Borrowing> borrowings, Penalty penalty) {
        this.libTicketNum = libTicketNum;
        this.name = name;
        this.address = address;
        this.delayCount = delayCount;
        this.borrowings = new ArrayList<>();
        this.penalty = penalty;
    }

    /**
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     */
    public String getAddress() {
        return address;
    }

    /**
     *
     * @param address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     *
     * @return
     */
    public int getLibTicketNum() {
        return libTicketNum;
    }
    
    /**
     *
     * @return
     */
    public int getDelayCount() {
        return delayCount;
    }
    
    /**
     *
     * @param delayCount
     */
    public void setDelayCount(int delayCount) {
        this.delayCount = delayCount;
    }

    /**
     *
     * @return
     */
    public ArrayList<Borrowing> getBorrowings() {
        return borrowings;
    }

    /**
     *
     * @param borrowing
     */
    public void addBorrowing(Borrowing borrowing) {
        borrowings.add(borrowing);
    }

    /**
     *
     * @return
     */
    public boolean hasPenalty() {
        if (penalty != null) {
            return true;
        }

        return false;
    }

    /**
     *
     * @return
     */
    public Penalty getPenalty() {
        return penalty;
    }
    
    /**
     *
     * @param penalty
     */
    public void setPenalty(Penalty penalty) {
        this.penalty = penalty;
    }

}
