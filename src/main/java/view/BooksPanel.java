package view;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import model.LibraryManager;
import utilities.Book;

/**
 * Class for the visual representation of the Books.
 * @author Attila
 */
public class BooksPanel extends JPanel {
    
    private final LibraryManager libraryManager;
    
    private final JFrame window;
    private JTabbedPane tabbedPane;
    private JPanel mainPanel;
    private JScrollPane dataScrollPane;
    private JTable dataTable;
    private DefaultTableModel defaultModel;
    private JButton lockUnlockBooksButton;
    private JButton selectDeselectButton;
    
    /**
     * Constructor for creating a new BooksPanel.
     * @param window The window that contains this panel.
     * @param libraryManager The LibraryManager of this program.
     */
    public BooksPanel(JFrame window, LibraryManager libraryManager) {
        this.libraryManager = libraryManager;
        this.window = window;
        initComponents();
    }
    
    /**
     * Function for showing a ConfirmDialog for accepting/declining the lock/unlock of the selected Book(s).
     */
    private void showLockUnlockBooksDialog() {
        String space = "                  ";
        String basicText = "Are you sure, that you want to lock/unlock the following book(s)?\n";
        ArrayList<Book> lockUnlockBooks = new ArrayList<>();
        ArrayList<Integer> rowNums = new ArrayList<>();

        for (int i = 0; i != dataTable.getRowCount(); ++i) {            
            if ((Boolean) defaultModel.getValueAt(i, 8) == true) {
                String ISBN = (String)defaultModel.getValueAt(i, 0);
                Book book = libraryManager.getBooks().get(ISBN);
                
                basicText += space + ISBN + ", " + book.getTitle() + "\n";
                lockUnlockBooks.add(book);
                rowNums.add(i);
            }
        }
        
        //Csak akkor jelenik meg az ablak, ha van torlendo tag
        if (lockUnlockBooks.size() > 0) {
            int n = JOptionPane.showConfirmDialog(window,
                basicText,
                "Confirmation", JOptionPane.YES_NO_OPTION);
            
            if (n == JOptionPane.YES_OPTION) {
                try {
                    libraryManager.lockUnlockBooks(lockUnlockBooks);
                    
                    //Frissitjuk a megvaltozott adatokat a tablan
                    for (int i = 0; i != rowNums.size(); ++i) {
                        defaultModel.setValueAt((lockUnlockBooks.get(i).isLocked() ? "<html><b><font color=\"red\">Yes</font></b></html>" : ""), rowNums.get(i), 7);
                    }
                    
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this,
                        e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    /**
     * Function for refreshing the table's content.
     */
    public void refreshTable() {
        //Tabla tartalmanak torlese
        defaultModel.setRowCount(0);
        
        //Tabla feltoltese
        for (Book b : libraryManager.getBooks().values()) {
            defaultModel.addRow(new Object[]{b.getISBN(), b.getAuthor(), b.getTitle(), b.getReleaseYear(), b.getQuantity(), b.getAvailable(),
                                b.getNumOfBorrowings(), (b.isLocked() ? "<html><b><font color=\"red\">Yes</font></b></html>" : ""), false});
        }
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

        tabbedPane = /*new javax.swing.JTabbedPane()*/ new JTabbedPaneCloseButton();
        mainPanel = new javax.swing.JPanel();
        dataScrollPane = new javax.swing.JScrollPane();
        dataTable = new javax.swing.JTable();
        lockUnlockBooksButton = new javax.swing.JButton();
        selectDeselectButton = new javax.swing.JButton();

        setPreferredSize(new java.awt.Dimension(i, j));            //1816, 959

        tabbedPane.setPreferredSize(new java.awt.Dimension(i, j)); //1826, 959
        //Ha nem lesz megnyithato ful, akkor torolheto
        /*tabbedPane.addContainerListener(new ContainerAdapter() {
            //Ha bazarjuk a panelokat, akkor frissiteni kell a billentyukombinaciot
            @Override
            public void componentRemoved(ContainerEvent e) {
                int paneCount = tabbedPane.getComponentCount();
                
                //Ha tobb panel van 10-nel, akkor csak az elso 9-en megyunk vegig
                if (paneCount > 10) {
                    for (int i = 0; i != 9; ++i) {
                        tabbedPane.setMnemonicAt(i, i + 49);
                    }
                //Kulonben, amennyi elem van a tabbedPane-ben
                } else {
                    for (int i = 0; i != paneCount - 1; ++i) {
                        tabbedPane.setMnemonicAt(i, i + 49);
                    }
                }
            }
        });*/
        
        //Tabla beallitasa
        String[] columns = new String[]{"ISBN", "Author", "Title", "Release year", "Quantity", "Available", "Number of borrowings", "Locked", "Lock/unlock"};
        defaultModel = new DefaultTableModel(columns, 0);        
        dataTable = new JTable(defaultModel) {
            @Override
            public Class getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return String.class;
                    case 1:
                        return String.class;
                    case 2:
                        return String.class;
                    case 3:
                        return Integer.class;
                    case 4:
                        return Integer.class;
                    case 5:
                        return Integer.class;
                    case 6:
                        return Integer.class;
                    case 7:
                        return String.class;
                    case 8:
                        return Boolean.class;
                    default:
                        return Boolean.class;
                }
            }
            
            @Override
            public boolean isCellEditable(int row, int column) {
                //Csak a nyolcadik oszlop szerkesztheto
                return column == 8;
            }
        };
        
        //Ne lehessen az oszlopok sorrendjet atrendezni
        dataTable.getTableHeader().setReorderingAllowed(false);
        
        //A Locked-et jelzo osztlopot kozepre zarjuk
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        dataTable.getColumnModel().getColumn(7).setCellRenderer(centerRenderer);
        
        //Tabla feltoltese
        for (Book b : libraryManager.getBooks().values()) {
            defaultModel.addRow(new Object[]{b.getISBN(), b.getAuthor(), b.getTitle(), b.getReleaseYear(), b.getQuantity(), b.getAvailable(),
                                b.getNumOfBorrowings(), (b.isLocked() ? "<html><b><font color=\"red\">Yes</font></b></html>" : ""), false});
        }
        
        dataScrollPane.setViewportView(dataTable);

        lockUnlockBooksButton.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lockUnlockBooksButton.setText("Lock / unlock selected books");
        lockUnlockBooksButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                //A kivalasztott konyveket zaroljuk vagy feloldjuk (ha zarolva van, akkor feloldjuk, kulonben zaroljuk)
                //Ezt lehet szinnel is jelezni (zold - fel van oldva, piros - zarolt)
                
                //Az infot egy for-al osszegyujtjuk, majd egy string-et keszitunk belole.
                /*String space = "                  ";
                String basicText = "Are you sure, that you want to lock/unlock the following book(s)?\n" +
                                   space + "'A book's title'\n" + space + "'A book's title'\n" + space + "'A book's title'\n\n";
                
                int n = JOptionPane.showConfirmDialog(window,
                        basicText,
                        "Confirmation", JOptionPane.YES_NO_OPTION);
                if (n == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }*/
                
                showLockUnlockBooksDialog();
            }
        });

        selectDeselectButton.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        selectDeselectButton.setText("Select/deselect all books");
        selectDeselectButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                for (int i = 0; i != dataTable.getRowCount(); ++i) {            
                    defaultModel.setValueAt(((Boolean) defaultModel.getValueAt(i, 8) == true ? false : true), i, 8);
                }
            }
        });

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
            .addComponent(dataScrollPane)
            .addGroup(mainPanelLayout.createSequentialGroup()
                //.addGap((int)(j * 0.754), (int)(j * 0.754), (int)(j * 0.754))  //724, 724, 724
                .addComponent(lockUnlockBooksButton)
                .addGap((int)(j * 0.086), (int)(j * 0.086), (int)(j * 0.086))  //83, 83, 83
                .addComponent(selectDeselectButton)
                /*.addContainerGap((int)(j * 0.754), Short.MAX_VALUE)*/)           //724
        );
        mainPanelLayout.setVerticalGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addComponent(dataScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, (int)(j * 0.86), /*javax.swing.GroupLayout.PREFERRED_SIZE*/(int)(j * 0.86))  //818
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, (int)(j * 0.038), /*Short.MAX_VALUE*/(int)(j * 0.038))                         //37
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lockUnlockBooksButton, javax.swing.GroupLayout.PREFERRED_SIZE, (int)(j * 0.046), javax.swing.GroupLayout.PREFERRED_SIZE)        //45
                    .addComponent(selectDeselectButton, javax.swing.GroupLayout.PREFERRED_SIZE, (int)(j * 0.046), javax.swing.GroupLayout.PREFERRED_SIZE))  //45
                .addGap((int)(j * 0.032), (int)(j * 0.032), (int)(j * 0.032)))    //31, 31, 31
        );

        tabbedPane.addTab("Books", mainPanel);
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabbedPane, /*javax.swing.GroupLayout.DEFAULT_SIZE*/i, i, /*Short.MAX_VALUE*/i)  //1816
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabbedPane, /*javax.swing.GroupLayout.DEFAULT_SIZE*/j, /*javax.swing.GroupLayout.DEFAULT_SIZE*/j, /*Short.MAX_VALUE*/j)
        );
    }

}
