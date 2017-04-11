package view;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.Date;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import model.LibraryManager;
import utilities.Borrowing;

/**
 * Class for the visual representation of the archived Borrowings.
 * @author Attila
 */
public class ArchivedBorrowingsPanel extends JPanel {
    
    private final LibraryManager libraryManager;
    
    private final JFrame window;
    private JScrollPane dataScrollPane;
    private JTable dataTable;
    private DefaultTableModel defaultModel;
    
    /**
     * Constructor for creating a new ArchivedBorrowingsPanel.
     * @param window The window that contains this panel.
     * @param libraryManager The LibraryManager of this program.
     */
    public ArchivedBorrowingsPanel(JFrame window, LibraryManager libraryManager) {
        this.libraryManager = libraryManager;
        this.window = window;
        initComponents();
    }
    
    /**
     * Function for initializing this panel's components.
     */
    private void initComponents() {
        
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();
        int i = (int) (width * 0.946);
        int j = (int) (height * 0.888);

        dataScrollPane = new javax.swing.JScrollPane();
        dataTable = new javax.swing.JTable();

        setPreferredSize(new java.awt.Dimension(i, j)); //1816, 959

        //Tabla beallitasa
        String[] columns = new String[]{"Borrowing number", "Borrower's ticket number", "ISBNs", "Date of borrowing", "Due date", "Return date", "Archived"};
        defaultModel = new DefaultTableModel(columns, 0);
        dataTable = new JTable(defaultModel) {
            @Override
            public Class getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return Integer.class;
                    case 1:
                        return Integer.class;
                    case 2:
                        return String.class;
                    case 3:
                        return Date.class;
                    case 4:
                        return Date.class;
                    case 5:
                        return Date.class;
                    case 6:
                        return String.class;
                    default:
                        return Boolean.class;
                }
            }
            
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;//column == 6;
            }
        };
        
        //Ne lehessen az oszlopok sorrendjet atrendezni
        dataTable.getTableHeader().setReorderingAllowed(false);
        
        //A datumokat tartalmazo osztlopokat kozepre zarjuk
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        dataTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        dataTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        dataTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        dataTable.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);
        
        //Tabla feltoltese
        for (Borrowing b : libraryManager.getArchivedBorrowings().values()) {
            defaultModel.addRow(new Object[]{b.getBorrowingNumber(), b.getLibTicketNum(), b.getBookISBNs(), b.getBorrowDate(), b.getDueDate(), b.getReturnDate(), (b.isArchive()? "Yes" : "No")});
        }
        
        dataScrollPane.setViewportView(dataTable);

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(this);
        this.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
            .addComponent(dataScrollPane)          //724
        );
        mainPanelLayout.setVerticalGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addComponent(dataScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, (int)(j * 0.966), (int)(j * 0.966))  //818
            ));
    }
    
}
