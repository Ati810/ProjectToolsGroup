package view;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Date;
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
import utilities.Borrowing;
import utilities.NewBorrowingInfo;

/**
 * Class for the visual representation of the Borrowings.
 * @author Attila
 */
public class BorrowingsPanel extends JPanel {
    
    private final LibraryManager libraryManager;
    
    private final JFrame window;
    private JTabbedPane tabbedPane;
    private JPanel mainPanel;
    private JScrollPane dataScrollPane;
    private JTable dataTable;
    private DefaultTableModel defaultModel;
    private JButton newBorrowingButton;
    private JButton archivedBorrowingsButton;
    private JButton deleteBorrowingsButton;
    
    /**
     * Constructor for creating a new BorrowingsPanel.
     * @param window The window that contains this panel.
     * @param libraryManager The LibraryManager of this program.
     */
    public BorrowingsPanel(JFrame window, LibraryManager libraryManager) {
        this.libraryManager = libraryManager;
        this.window = window;
        initComponents();
    }
    
    /**
     * Function for showing a ConfirmDialog for accepting/declining the delete of the selected Borrowing(s).
     */
    private void showDeleteBorrowingsDialog() {
        String space = "                  ";
        String basicText = "";
        ArrayList<Borrowing> deleteBorrowings = new ArrayList<>();
        ArrayList<Integer> rowNums = new ArrayList<>();

        for (int i = 0; i != dataTable.getRowCount(); ++i) {            
            if ((Boolean)defaultModel.getValueAt(i, 7) == true) {
                int borrowingNum = (Integer)defaultModel.getValueAt(i, 0);
                int libTicketNum = (Integer)defaultModel.getValueAt(i, 1);
                String name = libraryManager.getMembers().get(libTicketNum).getName();
                Borrowing borrowing = libraryManager.getBorrowings().get(borrowingNum);
                
                basicText += "Are you sure, that " + name + " brought back the following books?\n";
                
                for (String s : borrowing.getBookISBNs()) {
                    basicText += space + s + ", " + libraryManager.getBooks().get(s).getTitle() + "\n";
                }
                
                basicText += "\n";
                
                deleteBorrowings.add(borrowing);
                rowNums.add(i);
            }
        }
        
        //Csak akkor jelenik meg az ablak, ha van torlendo kolcsonzes
        if (deleteBorrowings.size() > 0) {
            int n = JOptionPane.showConfirmDialog(window,
                basicText,
                "Confirmation", JOptionPane.YES_NO_OPTION);
            
            if (n == JOptionPane.YES_OPTION) {
                try {
                    int i = 0;
                    for (Borrowing b : deleteBorrowings) {
                        libraryManager.deleteBorrowing(b);
                        defaultModel.removeRow(rowNums.get(i) - i); //Mivel torlesnel csokken a sorok szama, igy ki kell vonni a tarolt sorszambol, hogy mar mennyit toroltunk
                        ++i;
                    }
                    
                } catch (Exception e) {
                    //e.printStackTrace();
                    JOptionPane.showMessageDialog(this,
                        e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    /**
     * Function for showing a NewBorrowingDialog for creating a new Borrowing.
     */
    private void showNewBorrowingDialog() {
        NewBorrowingDialog dialog = new NewBorrowingDialog(window, true, libraryManager.getMembers(), libraryManager.getBooks());

        if (!dialog.isVisible()) {
            dialog.setVisible(true);
            NewBorrowingInfo info = dialog.getBorrowingInfo();

            try {
                //Ha nem null-t kaptunk vissza, akkor helyesek az adatok
                if (info != null) {
                    libraryManager.newBorrowing(info.member, info.books);

                    Borrowing b = libraryManager.getBorrowings().lastEntry().getValue();
                    defaultModel.addRow(new Object[]{b.getBorrowingNumber(), b.getLibTicketNum(), b.getBookISBNs(), b.getBorrowDate(), b.getDueDate(), 
                                        b.getReturnDate(), (b.isArchive()? "Yes" : "No"), false});
                }

            } catch (Exception ex) {
                //String warning = "Invalid username/password." + ex.getMessage();
                JOptionPane.showMessageDialog(this,
                        ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);

                showNewBorrowingDialog();
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
        for (Borrowing b : libraryManager.getBorrowings().values()) {
            defaultModel.addRow(new Object[]{b.getBorrowingNumber(), b.getLibTicketNum(), b.getBookISBNs(), b.getBorrowDate(), b.getDueDate(), b.getReturnDate(), (b.isArchive()? "Yes" : "No"), false});
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
        newBorrowingButton = new javax.swing.JButton();
        archivedBorrowingsButton = new javax.swing.JButton();
        deleteBorrowingsButton = new javax.swing.JButton();

        setPreferredSize(new java.awt.Dimension(i, j));            //1816, 959

        tabbedPane.setPreferredSize(new java.awt.Dimension(i, j)); //1826, 959
        tabbedPane.addContainerListener(new ContainerAdapter() {
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
        });

        //Tabla beallitasa
        String[] columns = new String[]{"Borrowing number", "Borrower's ticket number", "ISBNs", "Date of borrowing", "Due date", "Return date", "Archived", "Delete"};
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
                    case 7:
                        return Boolean.class;
                    default:
                        return Boolean.class;
                }
            }
            
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7;
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
        for (Borrowing b : libraryManager.getBorrowings().values()) {
            defaultModel.addRow(new Object[]{b.getBorrowingNumber(), b.getLibTicketNum(), b.getBookISBNs(), b.getBorrowDate(), b.getDueDate(), b.getReturnDate(), (b.isArchive()? "Yes" : "No"), false});
        }
        
        dataScrollPane.setViewportView(dataTable);

        newBorrowingButton.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        newBorrowingButton.setText("New borrowing");
        newBorrowingButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                //Ezt kell megcsinalni, ha rakattintunk egy tag sorara
                /*tabbedPane.addTab("Member", new MemberPanel());
                
                int paneCount = tabbedPane.getComponentCount();
                
                //Csak 9-ig szamozunk (amikor letrejon egy MemberPanel, akkor a tabbedPane-be 1x bekerul egy
                //plusz komponens is (az x), igy pl. 2 panel eseten 3 elem lesz a tabbedPane-ben)
                if (paneCount <= 10) {
                    tabbedPane.setMnemonicAt(paneCount - 2, paneCount + 47);
                }*/
                
                //new NewBorrowingDialog(window, true, libraryManager.getMembers(), libraryManager.getBooks());
                showNewBorrowingDialog();
            }
        });

        archivedBorrowingsButton.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        archivedBorrowingsButton.setText("Archived borrowings");
        archivedBorrowingsButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                //Csak akkor nyilik meg egy ArchivedBorrowingsPanel, ha meg nincs ilyenunk
                boolean l = false;
                
                for (int i = 0; i != tabbedPane.getComponentCount() - 1 && !l; ++i) {
                    l = tabbedPane.getTitleAt(i).equals("Archived borrowings");
                }
                
                if (!l) {
                    tabbedPane.addTab("Archived borrowings", new ArchivedBorrowingsPanel(window, libraryManager));
                
                    int paneCount = tabbedPane.getComponentCount();

                    //Csak 9-ig szamozunk (amikor letrejon egy MemberPanel, akkor a tabbedPane-be 1x bekerul egy
                    //plusz komponens is (az x), igy pl. 2 panel eseten 3 elem lesz a tabbedPane-ben)
                    if (paneCount <= 10) {
                        tabbedPane.setMnemonicAt(paneCount - 2, paneCount + 47);
                    }
                }
            }
        });
        
        deleteBorrowingsButton.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        deleteBorrowingsButton.setText("Delete selected borrowings");
        deleteBorrowingsButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                //A kijelolt kolcsonzesek torlese
                showDeleteBorrowingsDialog();
                //Az infot egy for-al osszegyujtjuk, majd egy string-et keszitunk belole.
                /*String space = "                  ";
                String basicText = "Are you sure, that " + "'Member's name' " + "brought back the following books?\n" +
                                   space + "'A books name'\n" + space + "'A books name'\n" + space + "'A books name'\n\n" +
                                   "Are you sure, that " + "'Another Member's name' " + "brought back the following books?\n" +
                                   space + "'A books name'\n" + space + "'A books name'\n" + space + "'A books name'\n\n";
                
                int n = JOptionPane.showConfirmDialog(window,
                        basicText,
                        "Confirmation", JOptionPane.YES_NO_OPTION);
                if (n == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }*/
            }
        });

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
            .addComponent(dataScrollPane)
            .addGroup(mainPanelLayout.createSequentialGroup()
                //.addGap((int)(j * 0.754), (int)(j * 0.754), (int)(j * 0.754))  //724, 724, 724
                .addComponent(newBorrowingButton)
                .addGap((int)(j * 0.086), (int)(j * 0.086), (int)(j * 0.086))  //83, 83, 83
                .addComponent(archivedBorrowingsButton)
                /*.addContainerGap((int)(j * 0.754), Short.MAX_VALUE)*/
                .addGap((int)(j * 0.086), (int)(j * 0.086), (int)(j * 0.086))  //83, 83, 83
                .addComponent(deleteBorrowingsButton))           //724
        );
        mainPanelLayout.setVerticalGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addComponent(dataScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, (int)(j * 0.86), /*javax.swing.GroupLayout.PREFERRED_SIZE*/(int)(j * 0.86))  //818
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, (int)(j * 0.038), /*Short.MAX_VALUE*/(int)(j * 0.038))                         //37
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(newBorrowingButton, javax.swing.GroupLayout.PREFERRED_SIZE, (int)(j * 0.046), javax.swing.GroupLayout.PREFERRED_SIZE)        //45
                    .addComponent(archivedBorrowingsButton, javax.swing.GroupLayout.PREFERRED_SIZE, (int)(j * 0.046), javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(deleteBorrowingsButton, javax.swing.GroupLayout.PREFERRED_SIZE, (int)(j * 0.046), javax.swing.GroupLayout.PREFERRED_SIZE))  //45
                .addGap((int)(j * 0.032), (int)(j * 0.032), (int)(j * 0.032)))    //31, 31, 31
        );

        tabbedPane.addTab("Borrowings", mainPanel);
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
