package utilities;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author kovgeri01
 */
public class PenaltyTest {
    @Test
    public void inSecondWeekTest() throws Exception {
        
        Calendar cal = new GregorianCalendar();
        cal.add(Calendar.SECOND,+10);
        Penalty testPenalty = new Penalty(0);
        testPenalty.getMidDate().setTime(cal.getTime().getTime());
        assertEquals(false,testPenalty.inSecondWeek());
        cal.clear();
        cal.setTime(new Date());
        cal.add(Calendar.SECOND, -1);
        testPenalty.getMidDate().setTime(cal.getTime().getTime());
        assertEquals(true,testPenalty.inSecondWeek());
        cal.clear();
        cal.setTime(new Date());
        cal.add(Calendar.YEAR, -1);
        testPenalty.getMidDate().setTime(cal.getTime().getTime());
        assertEquals(true,testPenalty.inSecondWeek());
        cal.clear();
        cal.setTime(new Date());
        cal.add(Calendar.YEAR, +1);
        cal.add(Calendar.SECOND, -10);
        testPenalty.getMidDate().setTime(cal.getTime().getTime());
        assertEquals(false,testPenalty.inSecondWeek());
    }
}
