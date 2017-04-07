package view;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import model.LibraryManager;
import utilities.Borrowing;
import utilities.Member;

/**
 * Class for the visual representation of a Member.
 * @author Attila
 */
public class MemberPanel extends JPanel {
    
    private final LibraryManager libraryManager;
    private Member member;
    DefaultTableModel parentDefaultModel;
    
    private JLabel addressLabel;
    private JTextField addressTextField;
    private JScrollPane borrowingsScrollPane;
    private JTable borrowingsTable;
    private DefaultTableModel defaultModel;
    private JLabel delayCountDataLabel;
    private JLabel delayCountLabel;
    private JButton editButton;
    private JLabel libTicketNumDataLabel;
    private JLabel libTicketNumLabel;
    private JLabel nameLabel;
    private JTextField nameTextField;
    private JLabel penaltyDataLabel;
    private JLabel penaltyLabel;
    private JButton saveButton;
    private JButton refreshButton;
    
    /**
     * Constructor for creating a new MemberPanel.
     * @param libraryManager The LibraryManager of this program.
     * @param member The Member whose informations are represented in this panel.
     * @param parentDefaultModel The DefaultTableModel of the parent. 
     */
    public MemberPanel(LibraryManager libraryManager, Member member, DefaultTableModel parentDefaultModel) {
        this.libraryManager = libraryManager;
        this.member = member;
        this.parentDefaultModel = parentDefaultModel;
        
        initComponents();
    }
    
    /**
     * Function for refreshing the table's content.
     */
    public void refreshTable() {
        //Tabla tartalmanak torlese
        defaultModel.setRowCount(0);
        
        //Tabla feltoltese
        for (Borrowing b : member.getBorrowings()) {
            defaultModel.addRow(new Object[]{b.getBorrowingNumber(), b.getBookISBNs(), b.getBorrowDate(), b.getDueDate(), b.getReturnDate(), (b.isArchive()? "Yes" : "No")});
        }
    }
    
    /**
     * Action for refreshing the local table.
     */
    private final Action refreshTableAction = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            //Frissitsuk a helyi tablat
            refreshTable();
        }
    };
    
    /**
     * Function for initializing this panel's components.
     */
    private void initComponents() {
        
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();
        int i = (int) (width * 0.946);
        int j = (int) (height * 0.888);

        nameLabel = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        addressLabel = new javax.swing.JLabel();
        addressTextField = new javax.swing.JTextField();
        libTicketNumLabel = new javax.swing.JLabel();
        libTicketNumDataLabel = new javax.swing.JLabel();
        delayCountLabel = new javax.swing.JLabel();
        delayCountDataLabel = new javax.swing.JLabel();
        penaltyLabel = new javax.swing.JLabel();
        penaltyDataLabel = new javax.swing.JLabel();
        borrowingsScrollPane = new javax.swing.JScrollPane();
        editButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        refreshButton = new javax.swing.JButton();

        setBorder(javax.swing.BorderFactory.createTitledBorder("Datas"));
        setPreferredSize(new java.awt.Dimension(i, j));

        nameLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        nameLabel.setText("Name");

        nameTextField.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        nameTextField.setText(member.getName());
        nameTextField.setEnabled(false);

        addressLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        addressLabel.setText("Address");

        addressTextField.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        addressTextField.setText(member.getAddress());
        addressTextField.setEnabled(false);

        libTicketNumLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        libTicketNumLabel.setText("Library ticket number");

        libTicketNumDataLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        libTicketNumDataLabel.setText(Integer.toString(member.getLibTicketNum()));

        delayCountLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        delayCountLabel.setText("Delays");

        delayCountDataLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        delayCountDataLabel.setText(Integer.toString(member.getDelayCount()));

        penaltyLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        penaltyLabel.setText("Penalty");

        penaltyDataLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        penaltyDataLabel.setText(member.hasPenalty() ? "<html><b><font color=\"red\">" + member.getPenalty().getStartDate().toString() + "</font> - " +
                                                       "<font color=\"orange\">" + member.getPenalty().getMidDate().toString() + "</font> - " + 
                                                       "<font color=\"green\">" + member.getPenalty().getEndDate().toString() + "</font></b></html>"
                                                     : "No");

        borrowingsScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder("Borrowings"));
        
        //Tabla beallitasa
        String[] columns = new String[]{"Borrowing number", "ISBNs", "Date of borrowing", "Due date", "Return date", "Archived"};
        defaultModel = new DefaultTableModel(columns, 0);
        borrowingsTable = new JTable(defaultModel) {
            @Override
            public Class getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return Integer.class;
                    case 1:
                        return String.class;
                    case 2:
                        return Date.class;
                    case 3:
                        return Date.class;
                    case 4:
                        return Date.class;
                    case 5:
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
        borrowingsTable.getTableHeader().setReorderingAllowed(false);
        
        //A datumokat tartalmazo osztlopokat kozepre zarjuk
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        borrowingsTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        borrowingsTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        borrowingsTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        borrowingsTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        
        //Tabla feltoltese
        for (Borrowing b : member.getBorrowings()) {
            defaultModel.addRow(new Object[]{b.getBorrowingNumber(), b.getBookISBNs(), b.getBorrowDate(), b.getDueDate(), b.getReturnDate(), (b.isArchive()? "Yes" : "No")});
        }
        
        borrowingsScrollPane.setViewportView(borrowingsTable);

        editButton.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        editButton.setText("Edit");
        editButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                nameTextField.setEnabled(true);
                addressTextField.setEnabled(true);
            }
        });

        saveButton.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        saveButton.setText("Save changes");
        saveButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (nameTextField.isEnabled()) {
                    try {
                        libraryManager.updateMember(member.getLibTicketNum(), 
                                                    (nameTextField.getText().equals("") ? null : nameTextField.getText()), 
                                                    (addressTextField.getText().equals("") ? null : addressTextField.getText()));
                        
                        nameTextField.setText(member.getName());
                        addressTextField.setText(member.getAddress());
                        
                        parentDefaultModel.removeRow(member.getLibTicketNum() - 1);
                        parentDefaultModel.addRow(new Object[]{member.getLibTicketNum(), member.getName(), member.getAddress(), member.getDelayCount(), member.getBorrowings().size(),
                                                 (member.hasPenalty() ? "Yes" : "No"), false});
                        
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(MemberPanel.this,
                                ex.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                        
                        nameTextField.setText(member.getName());
                        addressTextField.setText(member.getAddress());
                    }
                }
            }
        });
        
        refreshButton.setText("Refresh");
        refreshButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshTable();
            }
        });
        
        /*this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if ((e.getKeyCode() == KeyEvent.VK_R) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
                    System.out.println("woot!");
                }
            }
        });*/
        /*MemberPanel.this.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_MASK), "refreshTableAction");
        MemberPanel.this.getActionMap().put("refreshTableAction", refreshTableAction);
        this.setFocusTraversalPolicyProvider(true);
        this.setFocusCycleRoot(true);*/

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(penaltyLabel)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(delayCountLabel)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap((int)(i * 0.046), (int)(i * 0.046), (int)(i * 0.046)) //84, 84, 84
                                .addComponent(nameLabel))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(addressLabel)))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addGap((int)(i * 0.013), (int)(i * 0.013), (int)(i * 0.013)) //24, 24, 24
                            .addComponent(libTicketNumLabel))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(addressTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                    .addComponent(nameTextField)
                    .addComponent(libTicketNumDataLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(delayCountDataLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(penaltyDataLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap((int)(i * 0.032), (int)(i * 0.032), (int)(i * 0.032)) //59, 59, 59
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(editButton)
                    .addComponent(saveButton)))
                .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(refreshButton))
                //.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(borrowingsScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, i, Short.MAX_VALUE)    //1804
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap((int)(j * 0.052), (int)(j * 0.052), (int)(j * 0.052)) //50, 50, 50
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(nameLabel)
                    .addComponent(editButton))
                .addGap((int)(j * 0.0187), (int)(j * 0.0187), (int)(j * 0.0187)) //18, 18, 18
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addressLabel)
                    .addComponent(addressTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(saveButton))
                .addGap((int)(j * 0.0187), (int)(j * 0.0187), (int)(j * 0.0187)) //18, 18, 18
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(libTicketNumLabel)
                    .addComponent(libTicketNumDataLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap((int)(j * 0.0187), (int)(j * 0.0187), (int)(j * 0.0187)) //18, 18, 18
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(delayCountDataLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(delayCountLabel))
                .addGap((int)(j * 0.0187), (int)(j * 0.0187), (int)(j * 0.0187)) //18, 18, 18
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(penaltyDataLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(penaltyLabel))
                .addGap(44, 44, 44)
                .addComponent(refreshButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                //.addGap((int)(j * 0.076), (int)(j * 0.076), (int)(j * 0.076)) //73, 73, 73
                .addComponent(borrowingsScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, (int)(j * 0.65), /*Short.MAX_VALUE*/(int)(j * 0.65)))    //632
        );
    }
}
