package view;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.TreeMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import utilities.Book;
import utilities.Member;
import utilities.NewBorrowingInfo;

/**
 * Class that represents a dialog for getting the information of a new Borrowing.
 * @author Attila
 */
public class NewBorrowingDialog extends JDialog {
    
    private TreeMap<Integer, Member> members;
    private TreeMap<String, Book> books;
    
    private JFrame parent;
    private JLabel booksLabel;
    private JScrollPane booksScrollPane;
    private JTable booksTable;
    private DefaultTableModel booksDefaultModel;
    private JButton cancelButton;
    private JButton createButton;
    private JLabel membersLabel;
    private JScrollPane membersScrollPane;
    private JTable membersTable;
    private DefaultTableModel membersDefaultModel;
    
    /**
     * Constructor for creating a new NewBorrowingDialog.
     * @param parent The JFrame that will be the owner of this dialog.
     * @param modal Whether this dialog should be modal or not.
     * @param members A TreeMap of Members to choose a borrower from.
     * @param books A TreeMap of Books to choose from for the new Borrowing.
     */
    public NewBorrowingDialog(JFrame parent, boolean modal, TreeMap<Integer, Member> members, TreeMap<String, Book> books) {
        super(parent, modal);
        this.parent = parent;
        this.members = members;
        this.books = books;
        initComponents();
    }
    
    /**
     * Getter function for retrieving the NewBorrowingInfo from this dialog's stored datas.
     * @return The NewBorrowingInfo, created from this dialog's stored datas.
     */
    public NewBorrowingInfo getBorrowingInfo() {
        //Eloszor vegigmegyunk a membersTable-on, s lekerjuk a kivalasztott Member libTicketNum-jat,
        //majd osszegyujtjuk a kivalasztott konyveket, s eltaroljuk egy ArrayList-ben.
        Integer memNum = null;
        
        for (int i = 0; i != membersTable.getRowCount(); ++i) {
            if (((Boolean)membersTable.getValueAt(i, 3)) == true) {
                memNum = ((Integer)membersTable.getValueAt(i, 0));
            }
        }
        
        //Ha nem volt kivalasztott szemely, akkor mar itt null-t adunk vissza, hogy ne menjunk feleslegesen vegig a konyveken
        if (memNum == null) {
            return null;
        }
        
        ArrayList<Book> selectedBooks = new ArrayList<>();
        
        for (int i = 0; i != booksTable.getRowCount(); ++i) {
            if (((Boolean)booksTable.getValueAt(i, 6)) == true) {
                selectedBooks.add(books.get(((String)booksTable.getValueAt(i, 0))));
            }
        }
        
        //Ha nem valasztott ki konyvet, akkor is null-t adunk vissza
        if (selectedBooks.isEmpty()) {
            return null;
        }
        
        return new NewBorrowingInfo(members.get(memNum), selectedBooks);
    }
    
    /**
     * Listener class for handling the confirm action on this dialog.
     */
    private class ConfirmListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String warning = "Please select a member and books!";
            
            if (e.getSource().equals(cancelButton)) {
                warning = "Cancel";
                unselectBooks();
                NewBorrowingDialog.this.dispose();
            }
            
            if (getBorrowingInfo() != null && !warning.equals("Cancel")) {
                NewBorrowingDialog.this.setVisible(false);
            } else {
                //Ha nem Cancel a figyelmeztetes, akkor nem toltottek ki mindent, s ugy szeretnenek Member-t letrehozni
                if (!warning.equals("Cancel")) {
                    JOptionPane.showMessageDialog(parent,
                        warning,
                        "Missing data", JOptionPane.ERROR_MESSAGE);
                    //nameTextField.setText("");
                    //nameTextField.requestFocusInWindow();
                }
            }
        }
    }
    
    /**
     * Function for unselecting every Books on this dialog.
     */
    private void unselectBooks() {
        for (int i = 0; i != booksTable.getRowCount(); ++i) {
            booksTable.setValueAt(false, i, 6);
        }
    }
    
    /**
     * Function for initializing this dialog's components.
     */
    private void initComponents() {

        membersScrollPane = new javax.swing.JScrollPane();
        membersTable = new javax.swing.JTable();
        booksScrollPane = new javax.swing.JScrollPane();
        booksTable = new javax.swing.JTable();
        membersLabel = new javax.swing.JLabel();
        booksLabel = new javax.swing.JLabel();
        cancelButton = new javax.swing.JButton();
        createButton = new javax.swing.JButton();
        
        setTitle("New borrowing");
        
        //Tabla beallitasa
        String[] memberColumns = new String[]{"Library ticket number", "Name", "Penalty", "Select"};
        membersDefaultModel = new DefaultTableModel(memberColumns, 0);
        membersTable = new JTable(membersDefaultModel) {
            @Override
            public Class getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return Integer.class;
                    case 1:
                        return String.class;
                    case 2:
                        return String.class;
                    case 3:
                        return Boolean.class;
                    default:
                        return Boolean.class;
                }
            }

            //Ha van a tagnak Penalty-ja, akkor pirosra szinezzuk az ezt jelzo cellat
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
                Component comp = super.prepareRenderer(renderer, row, col);
                
                Member member = members.get((Integer)membersTable.getValueAt(row, 0));
                
                if (col == 2) {
                    if (member.hasPenalty()) {
                        if (member.getPenalty().inSecondWeek()) {
                            comp.setBackground(Color.orange);
                        } else {
                            comp.setBackground(Color.red);
                        }
                    } else {
                        comp.setBackground(Color.white);
                    }
                } else if (col == 3) {
                    //Ha az elso hetes bunteteset tolti a tag, akkor nem kolcsonozhet
                    if (member.hasPenalty() && !member.getPenalty().inSecondWeek()) {
                        ((JCheckBox) comp).setEnabled(false);
                        ((JCheckBox) comp).setSelected(false);
                    } else {
                        ((JCheckBox) comp).setEnabled(true);
                    }
                } else {
                    comp.setBackground(Color.white);
                }
                
                return comp;
            }
            
            @Override
            public boolean isCellEditable(int row, int column) {
                //Csak a harmadik oszlop (ahol a JCheckBox-van) modosithato, ha nincs buntetese a tagnak
                if (column == 3) {
                    Member member = members.get((Integer)membersTable.getValueAt(row, 0));
                    
                    if (member.hasPenalty() && !member.getPenalty().inSecondWeek()) {
                        return false;
                    } else {
                        return true;
                    }
                }
                
                return column == 3 /*false*/;
            }
            
            //A Penalty oszlop tool tip szovegenek beallitasa
            @Override
            public String getToolTipText(MouseEvent e) {
                String tip = null;
                java.awt.Point p = e.getPoint();
                int rowIndex = rowAtPoint(p);
                int colIndex = columnAtPoint(p);
                int realColumnIndex = convertColumnIndexToModel(colIndex);

                Member member = members.get((Integer)membersTable.getValueAt(rowIndex, 0));

                if (realColumnIndex == 2) { //Penalty oszlop
                    if (member.hasPenalty()) {
                        if (member.getPenalty().inSecondWeek()) {
                            tip = "This person is in the second week of his/her penalty.";
                        } else {
                            tip = "This person is in the first week of his/her penalty.";
                        }
                    } else {
                        tip = "This person doesn't have a penalty.";
                    }
                }
                
                return tip;
            }
            
            //Csak egyetlen egy tag jelolheto ki
            private boolean ImInLoop = false;
            
            @Override
            public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
                if (columnIndex == 3) {
                    if (!ImInLoop && !(Boolean)super.getValueAt(rowIndex, 3)) {
                        ImInLoop = true;
                        Boolean bol = (Boolean) aValue;
                        super.setValueAt(aValue, rowIndex, columnIndex);
                        for (int i = 0; i < this.getRowCount(); i++) {
                            if (i != rowIndex) {
                                super.setValueAt(!bol, i, columnIndex);
                            }
                        }
                        ImInLoop = false;
                        
                        //Ha megvaltozik a kijelolt tag, akkor kiszedunk minden konyv kijelolest,
                        //mivel mas tag mas mennyisegu konyvet kolcsonozhet
                        unselectBooks();
                    }
                } else {
                    super.setValueAt(aValue, rowIndex, columnIndex);
                }
            }
        };
        
        //Ne lehessen az oszlopok sorrendjet atrendezni
        membersTable.getTableHeader().setReorderingAllowed(false);
        
        //Tabla feltoltese
        for (Member m : members.values()) {
            membersDefaultModel.addRow(new Object[]{m.getLibTicketNum(), m.getName(), "", false});
        }
        
        membersScrollPane.setViewportView(membersTable);
        
        //Tabla beallitasa
        String[] bookColumns = new String[]{"ISBN", "Author", "Title", "Release year", "Available", "Locked", "Select"};
        booksDefaultModel = new DefaultTableModel(bookColumns, 0);
        booksTable = new JTable(booksDefaultModel) {
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
                        return String.class;
                    case 6:
                        return Boolean.class;
                    default:
                        return Boolean.class;
                }
            }

            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
                Component comp = super.prepareRenderer(renderer, row, col);
                
                if (col == 6) {
                    //Ha 0 az elerheto darabszam, vagy ha zarolt a konyv, akkor nem lehet kivalasztani
                    if ((Integer)booksTable.getValueAt(row, 4) == 0) {
                        ((JCheckBox) comp).setEnabled(false);
                        ((JCheckBox) comp).setSelected(false);
                    } else if (((String)booksTable.getValueAt(row, 5)).equals("<html><b><font color=\"red\">Yes</font></b></html>")) {
                        ((JCheckBox) comp).setEnabled(false);
                        ((JCheckBox) comp).setSelected(false);
                    } else {
                        ((JCheckBox) comp).setEnabled(true);
                    }
                }
                
                return comp;
            }
            
            @Override
            public boolean isCellEditable(int row, int column) {
                //Csak a hatodik oszlop (ahol a JCheckBox-van) modosithato
                if (column == 6) {                   
                    if ((Integer)booksTable.getValueAt(row, 4) == 0) {
                        return false;
                    } else if (((String)booksTable.getValueAt(row, 5)).equals("<html><b><font color=\"red\">Yes</font></b></html>")) {
                        return false;
                    }
                }
                
                return column == 6;
            }
            
            //Alapvetoen 4 konyvet lehet kolcsonozni, de ez kettore csokken a buntetes masodik heteben
            @Override
            public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
                Integer rowNum = null;
                Member member = null;
                
                for (int i = 0; i != membersTable.getRowCount(); ++i) {
                    if ((Boolean) membersTable.getValueAt(i, 3) == true) {
                        rowNum = i;
                    }
                }
                
                if (rowNum != null) {
                    member = members.get(rowNum + 1);
                }
                
                //Member member = members.get((Integer)membersTable.getSelectedRow() + 1);
                
                if (columnIndex == 6 && member != null) {
                    //Ha igaz, akkor kell a setValueAt, hogy ki tudja szedni a jelolest
                    if ((Boolean)booksTable.getValueAt(rowIndex, 6) == true) {
                        super.setValueAt(aValue, rowIndex, columnIndex);
                    } else {
                        int booksCount = 0;
                        
                        for (int i = 0; i != booksTable.getRowCount(); ++i) {
                            if ((Boolean)booksTable.getValueAt(i, 6) == true) {
                                ++booksCount;
                            }
                        }
                        
                        //Ha nincs buntetese, akkor 4 konyvet kolcsonozhet
                        if (!member.hasPenalty() && booksCount < 4) {
                            super.setValueAt(aValue, rowIndex, columnIndex);
                        //Ha van buntetese, de a masodik hetben jar, akkor 2 konyvet kolcsonozhet
                        } else if (member.hasPenalty() && member.getPenalty().inSecondWeek() && booksCount < 2) {
                            super.setValueAt(aValue, rowIndex, columnIndex);
                        }
                    }
                    
                } else {
                    super.setValueAt(aValue, rowIndex, columnIndex);
                }
            }
        };
        
        //Ne lehessen az oszlopok sorrendjet atrendezni
        booksTable.getTableHeader().setReorderingAllowed(false);
        
        //A Locked-et jelzo osztlopot kozepre zarjuk
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        booksTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        
        //Tabla feltoltese
        for (Book b : books.values()) {
            booksDefaultModel.addRow(new Object[]{b.getISBN(), b.getAuthor(), b.getTitle(), b.getReleaseYear(), b.getAvailable(),
                                    (b.isLocked() ? "<html><b><font color=\"red\">Yes</font></b></html>" : ""), false});
        }
        
        booksScrollPane.setViewportView(booksTable);

        membersLabel.setText("Members");

        booksLabel.setText("Books");

        cancelButton.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        cancelButton.setText("Cancel");

        createButton.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        createButton.setText("Create new borrowing");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(cancelButton)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(membersScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 590, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(membersLabel)))
                .addGap(18, 30, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(createButton)
                    .addComponent(booksScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 590, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(booksLabel)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(booksLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(booksScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(membersLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(membersScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 57, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(createButton))
                .addGap(39, 39, 39))
        );

        pack();
        
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);
        
        ActionListener confirmListener = new NewBorrowingDialog.ConfirmListener();
        createButton.addActionListener(confirmListener); // add listener
        cancelButton.addActionListener(confirmListener);
        //nameTextField.addActionListener(confirmListener);
        //addressTextField.addActionListener(confirmListener);
    }
    
}
