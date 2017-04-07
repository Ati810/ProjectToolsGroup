package utilities;

import java.util.Calendar;
import java.util.Date;

/**
 * Class that represents a Penalty.
 * @author Attila
 */
public class Penalty {
    
    private int libTicketNum;

    private Date startDate;
    
    private Date midDate;

    private Date endDate;

    //Programon beluli Penalty letrehozasahoz.

    /**
     * Constructor for creating a new Penalty.
     * @param libTicketNum The libTicketNum of the Member who has this Penalty.
     */
    public Penalty(int libTicketNum) {
        this.libTicketNum = libTicketNum;
        
        startDate = new Date();
        
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.DAY_OF_YEAR, 7);
        midDate = calendar.getTime();   //midDate = startDate + 1 het
        
        calendar.add(Calendar.DAY_OF_YEAR, 7);
        endDate = calendar.getTime();   //endDate = startDate + 2 het
    }
    
    //Ezt a konstruktort hasznaljuk, amikor az adatbazisbol kerjuk le az adatokat.

    /**
     * Constructor for creating a new Penalty.
     * @param libTicketNum The libTicketNum of the Member who has this Penalty.
     * @param startDate The date when this Penalty has started. Till midDate, we can't borrow any Books.
     * @param midDate From this date, we can borrow maximum 2 Books.
     * @param endDate The date when this Penalty has ended. Now, we can borrow the maximum amount of Books (i.e. 4).
     */
    public Penalty(int libTicketNum, Date startDate, Date midDate, Date endDate) {
        this.libTicketNum = libTicketNum;
        this.startDate = startDate;
        this.midDate = midDate;
        this.endDate = endDate;
    }
    
    /**
     * Getter function for the Member's library ticket number, who has this Penalty.
     * @return The Member's library ticket number as an int.
     */
    public int getLibTicketNum() {
        return libTicketNum;
    }

    /**
     * Getter function for this Penalty's start date.
     * @return The Penalty's start date as a Date.
     */
    public Date getStartDate() {
        return startDate;
    }
    
    /**
     * Getter function for this Penalty's mid date.
     * @return The Penalty's mid date as a Date.
     */
    public Date getMidDate() {
        return midDate;
    }

    /**
     * Getter function for this Penalty's end date.
     * @return The Penalty's end date as a Date.
     */
    public Date getEndDate() {
        return endDate;
    }
    
    //Igazat ad vissza, ha a buntetesunk masodik heteben jarunk (ekkor 2 konyvet kolcsonozhetunk)

    /**
     * Function for retrieving if this Penalty has passed its mid date.
     * @return Whether this Penalty has passed its mid date or it hasn't.
     */
        public boolean inSecondWeek() {
        Date now = new Date();
        
        return now.after(midDate);
    }

}
