package view;

import model.LibraryManager;
import utilities.UserInfo;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.MouseAdapter;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;

/**
 * Class that represents the window of this program.
 * @author Attila
 */
public class Window extends JFrame {
    
    private LibraryManager libraryManager;
    
    private JPanel contentPanel;    
    private StartPagePanel startPagePanel;
    private JPanel menuPanel;
    private MembersPanel membersPanel;
    private BooksPanel booksPanel;
    private BorrowingsPanel borrowingsPanel;

    private MenuBar menuBar;
    private JToolBar toolBar;

    private JButton membersButton;
    private JButton booksButton;
    private JButton borrowingsButton;

    /**
     * Constructor for creating a new Window.
     */
    public Window() {
        //libraryManager = new LibraryManager();

        initComponents();
        //showConnectionDialog();
    }
    
    /**
     * Function for showing a ConnectionDialog for connecting to the database.
     */
    private void showConnectionDialog() {
        ConnectToDatabaseDialog dialog = new ConnectToDatabaseDialog(this, true);
        
        if (!dialog.isVisible()) {
            dialog.setVisible(true);
            UserInfo info = dialog.getUserInfo();
            try {
                libraryManager = new LibraryManager(info.username, info.password);
                //libraryManager.newMember("Attila Kovács", "Budapest");
                //libraryManager.deleteMember(libraryManager.getMembers().get(101));
                //libraryManager.updateMember(101, "Attila", null);
                
                /*Date now = new Date();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(now);
                calendar.add(Calendar.DAY_OF_YEAR, 7);
                Date mid = calendar.getTime();
                calendar.add(Calendar.DAY_OF_YEAR, 7);
                Date end = calendar.getTime();
                libraryManager.database.insertPenalty(new Penalty(12, now, mid, end));*/
                
                /*ArrayList<Book> books = new ArrayList<>();
                books.add(libraryManager.getBooks().get("1692021081499"));
                books.add(libraryManager.getBooks().get("1614062510899"));
                books.add(libraryManager.getBooks().get("1608091432099"));
                libraryManager.newBorrowing(libraryManager.getMembers().get(101), books);*/
                
                //libraryManager.deleteBorrowing(libraryManager.getBorrowings().get(2));
                //libraryManager.deleteBorrowing(libraryManager.getBorrowings().get(3));
                
            } catch (Exception ex) {
                //String warning = "Invalid username/password." + ex.getMessage();
                JOptionPane.showMessageDialog(this,
                        ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                
                showConnectionDialog();
            }
        }
    }

    /**
     * Function for showing a ConfirmDialog for confirming the quit from the program.
     */
    private void showExitConfirmation() {
        int n = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to quit?",
                "Confirmation", JOptionPane.YES_NO_OPTION);
        if (n == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    /**
     * Action, that we can refresh the data from the database with.
     */
    private final Action refreshFromDatabaseAction = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                //Lekerjuk az osszes uj adatot az adatbazisbol (meghivjuk a libraryManager refresh fuggvenyet)
                //melyben meghivjuk az adatbazis get fuggvenyeit
                int n = JOptionPane.showConfirmDialog(Window.this,
                        "Are you sure you want to refresh the data from the database?",
                        "Confirmation", JOptionPane.YES_NO_OPTION);
                if (n == JOptionPane.YES_OPTION) {
                    libraryManager.refreshDataFromDatabase();
                    refreshPanels();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(Window.this,
                        ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    };
    
    /**
     * Action, that we can refresh the local table's datas with.
     */
    private final Action refreshTablesAction = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            //Minden tablat frissitunk
            refreshPanels();
        }
    };

    /**
     * Action, that we can exit the program with.
     */
    private final Action exitAction = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            showExitConfirmation();
        }
    };
    
    /**
     * Function for refreshing the panels' content.
     */
    private void refreshPanels() {
        
        membersPanel.refreshTable();
        booksPanel.refreshTable();
        borrowingsPanel.refreshTable();
    }

    /**
     * Function for initializing this JFrame's components.
     */
    private void initComponents() {
        
        showConnectionDialog();
        
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();
        int i = (int) (width * 0.946);
        int j = (int) (height * 0.888);
        
        setTitle("Library");
        setLayout(new BorderLayout());

        contentPanel = new javax.swing.JPanel(new CardLayout());
        startPagePanel = new StartPagePanel();
        contentPanel.add(startPagePanel, "Start");
        membersPanel = new MembersPanel(this, libraryManager);
        contentPanel.add(membersPanel, "Members");
        booksPanel = new BooksPanel(this, libraryManager);
        contentPanel.add(booksPanel, "Books");
        borrowingsPanel = new BorrowingsPanel(this, libraryManager);
        contentPanel.add(borrowingsPanel, "Borrowings");
            
        toolBar = new javax.swing.JToolBar();
        menuBar = new MenuBar(refreshFromDatabaseAction, refreshTablesAction, exitAction);
        menuPanel = new javax.swing.JPanel();
        
        membersButton = new javax.swing.JButton();
        booksButton = new javax.swing.JButton();        
        borrowingsButton = new javax.swing.JButton();
        
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        toolBar.setRollover(true);
        toolBar.setFloatable(false);

        membersButton.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        membersButton.setText("Members");
        membersButton.setBorderPainted(false);
        membersButton.setContentAreaFilled(false);
        membersButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        membersButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        membersButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                membersButton.setBackground(new Color(0, 0, 0, 50));
                membersButton.setContentAreaFilled(true);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                membersButton.setContentAreaFilled(false);
            }
        });
        membersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                /*if (membersPanel.getTabbedPaneCompCount() == 1) {
                    contentPanel.remove(membersPanel);
                    
                    membersPanel = new MembersPanel();
                    contentPanel.add(membersPanel, "Members");
                }*/
                
                CardLayout cardLayout = (CardLayout)contentPanel.getLayout();
                cardLayout.show(contentPanel, "Members");
            }
        });

        booksButton.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        booksButton.setText("Books");
        booksButton.setBorderPainted(false);
        booksButton.setContentAreaFilled(false);
        booksButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        booksButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        booksButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                booksButton.setBackground(new Color(0, 0, 0, 50));
                booksButton.setContentAreaFilled(true);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                booksButton.setContentAreaFilled(false);
            }
        });
        booksButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CardLayout cardLayout = (CardLayout)contentPanel.getLayout();
                cardLayout.show(contentPanel, "Books");
            }
        });

        borrowingsButton.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        borrowingsButton.setText("Borrowings");
        borrowingsButton.setBorderPainted(false);
        borrowingsButton.setContentAreaFilled(false);
        borrowingsButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        borrowingsButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        borrowingsButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                borrowingsButton.setBackground(new Color(0, 0, 0, 50));
                borrowingsButton.setContentAreaFilled(true);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                borrowingsButton.setContentAreaFilled(false);
            }
        });
        borrowingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CardLayout cardLayout = (CardLayout)contentPanel.getLayout();
                cardLayout.show(contentPanel, "Borrowings");
            }
        });

        javax.swing.GroupLayout menuPanelLayout = new javax.swing.GroupLayout(menuPanel);
        menuPanel.setLayout(menuPanelLayout);
        menuPanelLayout.setHorizontalGroup(
                menuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(membersButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(booksButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(borrowingsButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        menuPanelLayout.setVerticalGroup(
                menuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(menuPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(membersButton, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(booksButton, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(borrowingsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(j, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout contentPanelLayout = new javax.swing.GroupLayout(contentPanel);
        //contentPanel.setLayout(contentPanelLayout);
        contentPanelLayout.setHorizontalGroup(
                contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 0, Short.MAX_VALUE)
        );
        contentPanelLayout.setVerticalGroup(
                contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 0, Short.MAX_VALUE)
        );

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(toolBar, javax.swing.GroupLayout.PREFERRED_SIZE, width, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(menuPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(contentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(toolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(menuPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(contentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                showExitConfirmation();
            }
        });
        
        setBounds(0, 0, i, j);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);        
        setVisible(true);
        //frame.setResizable(false);    //Hogy mindig ekkora maradjon
    }

}
