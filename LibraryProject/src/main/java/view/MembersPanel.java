package view;

import java.awt.Color;
import java.awt.Component;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import model.LibraryManager;
import utilities.Member;
import utilities.NewMemberInfo;

/**
 * Class for the visual representation of the Members.
 * @author Attila
 */
public class MembersPanel extends JPanel {

    private final LibraryManager libraryManager;

    //private ArrayList<MemberPanel> memberPanels;
    private final JFrame window;
    private JTabbedPane tabbedPane;
    private JPanel mainPanel;
    private JScrollPane dataScrollPane;
    private JTable dataTable;
    private DefaultTableModel defaultModel;
    private JButton newMemberButton;
    private JButton delSelectedMemButton;

    /**
     * Constructor for creating a new MembersPanel.
     * @param window The window that contains this panel.
     * @param libraryManager The LibraryManager of this program.
     */
    public MembersPanel(JFrame window, LibraryManager libraryManager) {
        this.libraryManager = libraryManager;
        this.window = window;
        initComponents();
    }

    /**
     * Function for showing a NewMemberDialog for creating a new Member.
     */
    private void showNewMemberDialog() {
        NewMemberDialog dialog = new NewMemberDialog(window, true);

        if (!dialog.isVisible()) {
            dialog.setVisible(true);
            NewMemberInfo info = dialog.getMemberInfo();

            try {
                //Ha nem kaptunk NewMemberInfo-t, s az adatok hosszabbak 0-nal, akkor nem a Cancel-re kattintottak
                if (info.name.length() > 0 && info.address.length() > 0) {
                    libraryManager.newMember(info.name, info.address);

                    Member m = libraryManager.getMembers().lastEntry().getValue();
                    defaultModel.addRow(new Object[]{m.getLibTicketNum(), m.getName(), m.getAddress(), m.getDelayCount(), m.getBorrowings().size(), "", false});
                }

            } catch (Exception ex) {
                //String warning = "Invalid username/password." + ex.getMessage();
                JOptionPane.showMessageDialog(this,
                        ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);

                showNewMemberDialog();
            }
        }
    }
    
    /**
     * Function for showing a ConfirmDialog for accepting/declining the delete of the selected Member(s).
     */
    private void showDeleteMembersDialog() {
        String space = "                  ";
        String basicText = "Are you sure, that you want to delete the following members(s)?\n";
        ArrayList<Member> delMembers = new ArrayList<>();
        ArrayList<Integer> rowNums = new ArrayList<>();

        //A kijelolt tagok torlese
        for (int i = 0; i != dataTable.getRowCount(); ++i) {
            //Ha ki van jelolve a Delete osztlopban a checkbox...
            if ((Boolean) defaultModel.getValueAt(i, 6) == true) {
                //es nincs aktiv kolcsonzese a tagnak...
                if ((Integer) defaultModel.getValueAt(i, 4) == 0) {
                    //akkor eltaroljuk a torlendo tagokat
                    int libTicketNum = (Integer)defaultModel.getValueAt(i, 0);
                    Member member = libraryManager.getMembers().get(libTicketNum);
                    basicText += space + member.getName() + " (" + member.getLibTicketNum() + ")" + "\n";
                    
                    delMembers.add(member);
                    rowNums.add(i);
                }
            }
        }
        
        //Csak akkor jelenik meg az ablak, ha van torlendo tag
        if (delMembers.size() > 0) {
            int n = JOptionPane.showConfirmDialog(window,
                basicText,
                "Confirmation", JOptionPane.YES_NO_OPTION);
            
            if (n == JOptionPane.YES_OPTION) {
                try {
                    int j = 0;
                    for (Member m : delMembers) {
                        libraryManager.deleteMember(m);
                        
                        defaultModel.removeRow(rowNums.get(j) - j);
                        ++j;
                        
                        //Ha meg van nyitva fulkent, akkor onnan is el kell tavolitani
                        String title = Integer.toString(m.getLibTicketNum());
                        int i;
                        boolean l = false;

                        for (i = 0; i != tabbedPane.getComponentCount() - 1 && !l; ++i) {
                            l = tabbedPane.getTitleAt(i).equals(title);
                        }
                        
                        if (l) tabbedPane.remove(i - 1);
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
     * Function for opening a new tab as a MemberPanel with the given Member.
     * @param member The Member whose informations are need to be represented in the MemberPanel.
     */
    private void showMemberPanel(Member member) {
        //Csak akkor nyilik meg a tag adatlapja ha meg nincs megnyitva
        //String title = member.getLibTicketNum() + " " + member.getName();
        String title = Integer.toString(member.getLibTicketNum());
        boolean l = false;

        for (int i = 0; i != tabbedPane.getComponentCount() - 1 && !l; ++i) {
            l = tabbedPane.getTitleAt(i).equals(title);
        }
        
        if (!l) {
            tabbedPane.addTab(title, new MemberPanel(libraryManager, member, defaultModel));

            int paneCount = tabbedPane.getComponentCount();

            //Csak 9-ig szamozunk (amikor letrejon egy MemberPanel, akkor a tabbedPane-be 1x bekerul egy
            //plusz komponens is (az x), igy pl. 2 panel eseten 3 elem lesz a tabbedPane-ben)
            if (paneCount <= 10) {
                tabbedPane.setMnemonicAt(paneCount - 2, paneCount + 47);
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
        for (Member m : libraryManager.getMembers().values()) {
            defaultModel.addRow(new Object[]{m.getLibTicketNum(), m.getName(), m.getAddress(), m.getDelayCount(), m.getBorrowings().size(), "", false});
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
        newMemberButton = new javax.swing.JButton();
        delSelectedMemButton = new javax.swing.JButton();

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
        //Has penalty - yes, no; Delete - amit kijelolunk, azt torolhetjuk
        String[] columns = new String[]{"Library ticket number", "Name", "Address", "Number of delays", "Number of active borrowings", "Penalty", "Delete"};
        defaultModel = new DefaultTableModel(columns, 0);
        dataTable = new JTable(defaultModel) {
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

            //Ha van a tagnak Penalty-ja, akkor pirosra szinezzuk az ezt jelzo cellat
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
                Component comp = super.prepareRenderer(renderer, row, col);
                Object value = getModel().getValueAt(row, col);
                
                Member member = libraryManager.getMembers().get((Integer)dataTable.getValueAt(row, 0));
                
                if (col == 5) {
                    if (member.hasPenalty()) {
                        if (member.getPenalty().inSecondWeek()) {
                            comp.setBackground(Color.orange);
                        } else {
                            comp.setBackground(Color.red);
                        }
                    } else {
                        comp.setBackground(Color.white);
                    }
                } else if (col == 6) {
                    if ((Integer) dataTable.getValueAt(row, 4) >= 1) {
                        //Component delComp = super.prepareRenderer(renderer, row, 6);
                        //delComp.setEnabled(false);
                        //delComp.setVisible(false);
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
                //Csak a hatodik oszlop (ahol a JCheckBox-van) modosithato
                return column == 6;
            }
            
            //A Penalty oszlop tool tip szovegenek beallitasa
            @Override
            public String getToolTipText(MouseEvent e) {
                String tip = null;
                java.awt.Point p = e.getPoint();
                int rowIndex = rowAtPoint(p);
                int colIndex = columnAtPoint(p);
                int realColumnIndex = convertColumnIndexToModel(colIndex);

                Member member = libraryManager.getMembers().get((Integer)dataTable.getValueAt(rowIndex, 0));

                if (realColumnIndex == 5) { //Penalty oszlop
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
        };
        
        //Ha ketszer kattintunk egy tagra, akkor megnyilik az adatlapja egy uj ablakban
        dataTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent me) {
                JTable table =(JTable) me.getSource();
                Point p = me.getPoint();
                int row = table.rowAtPoint(p);
                if (me.getClickCount() == 2) {
                    //System.out.println(libraryManager.getMembers().get(row + 1).getLibTicketNum());
                    showMemberPanel(libraryManager.getMembers().get(row + 1));
                }
            }
        });
        
        //Ne lehessen az oszlopok sorrendjet atrendezni
        dataTable.getTableHeader().setReorderingAllowed(false);

        //A Penalty-t jelzo osztlopot kozepre zarjuk
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        dataTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);

        //Tabla feltoltese
        for (Member m : libraryManager.getMembers().values()) {
            defaultModel.addRow(new Object[]{m.getLibTicketNum(), m.getName(), m.getAddress(), m.getDelayCount(), m.getBorrowings().size(), "", false});
        }
        
        dataScrollPane.setViewportView(dataTable);

        newMemberButton.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        newMemberButton.setText("New member");
        newMemberButton.addActionListener(new java.awt.event.ActionListener() {
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

                showNewMemberDialog();
            }
        });

        delSelectedMemButton.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        delSelectedMemButton.setText("Delete selected members");
        delSelectedMemButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showDeleteMembersDialog();
            }
        });

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
                mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                .addComponent(dataScrollPane)
                .addGroup(mainPanelLayout.createSequentialGroup()
                        //.addGap((int)(j * 0.754), (int)(j * 0.754), (int)(j * 0.754))  //724, 724, 724
                        .addComponent(newMemberButton)
                        .addGap((int) (j * 0.086), (int) (j * 0.086), (int) (j * 0.086)) //83, 83, 83
                        .addComponent(delSelectedMemButton)
                /*.addContainerGap((int)(j * 0.754), Short.MAX_VALUE)*/) //724
        );
        mainPanelLayout.setVerticalGroup(
                mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(dataScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, (int) (j * 0.86), /*javax.swing.GroupLayout.PREFERRED_SIZE*/ (int) (j * 0.86)) //818
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, (int) (j * 0.038), /*Short.MAX_VALUE*/ (int) (j * 0.038)) //37
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(newMemberButton, javax.swing.GroupLayout.PREFERRED_SIZE, (int) (j * 0.046), javax.swing.GroupLayout.PREFERRED_SIZE) //45
                                .addComponent(delSelectedMemButton, javax.swing.GroupLayout.PREFERRED_SIZE, (int) (j * 0.046), javax.swing.GroupLayout.PREFERRED_SIZE)) //45
                        .addGap((int) (j * 0.032), (int) (j * 0.032), (int) (j * 0.032))) //31, 31, 31
        );

        tabbedPane.addTab("Members", mainPanel);
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(tabbedPane, /*javax.swing.GroupLayout.DEFAULT_SIZE*/ i, i, /*Short.MAX_VALUE*/ i) //1816
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(tabbedPane, /*javax.swing.GroupLayout.DEFAULT_SIZE*/ j, /*javax.swing.GroupLayout.DEFAULT_SIZE*/ j, /*Short.MAX_VALUE*/ j)
        );
    }

}
