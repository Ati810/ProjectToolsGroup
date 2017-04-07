package view;

import java.awt.Event;
import java.awt.event.KeyEvent;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

/**
 * Class that represents a custom JMenuBar.
 * @author Attila
 */
public class MenuBar extends JMenuBar {
        
    /**
     * Constructor for creating a new MenuBar
     * @param refreshFromDatabaseAction Action, that we can refresh the data from the database with.
     * @param refreshTablesAction Action, that we can refresh the local table's datas with.
     * @param exitAction Action, that we can exit the program with.
     */
    public MenuBar(Action refreshFromDatabaseAction, Action refreshTablesAction, Action exitAction) {
        JMenu menu = new JMenu("Menu");
        menu.setMnemonic('M');
        
        //Tablak frissitese
        JMenuItem refreshTables = new JMenuItem(refreshTablesAction);
        refreshTables.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, Event.CTRL_MASK));
        refreshTables.setText("Refresh tables");
        refreshTables.setMnemonic('t');
        menu.add(refreshTables);
        
        //Adatok frissitese az adatbazisbol
        JMenuItem refreshFromDatabase = new JMenuItem(refreshFromDatabaseAction);
        refreshFromDatabase.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, Event.CTRL_MASK + Event.ALT_MASK));
        refreshFromDatabase.setText("Refresh data from database");
        refreshFromDatabase.setMnemonic('d');
        menu.add(refreshFromDatabase);
        
        //Kilepes
        JMenuItem exit = new JMenuItem(exitAction);
        exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, Event.CTRL_MASK));
        exit.setText("Exit");
        exit.setMnemonic('E');
        menu.add(exit);
        
        add(menu);
    }
    
}
