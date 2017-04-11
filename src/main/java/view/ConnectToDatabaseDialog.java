package view;

import utilities.UserInfo;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * Class that represents a dialog for getting the login information for the database.
 * @author Attila
 */
public class ConnectToDatabaseDialog extends JDialog {
    
    private JFrame parent;    
    private JButton cancelButton;
    private JButton connectButton;
    private JLabel nameLabel;
    private JTextField nameTextField;
    private JPasswordField passwordField;
    private JLabel passwordLabel;
    private JLabel titleLabel;
    
    /**
     * Constructor for creating a new ConnectToDatabaseDialog.
     * @param parent The JFrame that will be the owner of this dialog.
     * @param modal Whether this dialog should be modal or not.
     */
    public ConnectToDatabaseDialog(JFrame parent, boolean modal) {
        super(parent, modal);
        this.parent = parent;
        initComponents();
    }
    
    /**
     * Getter function for retrieving the UserInfo, created from this dialog's stored datas.
     * @return The UserInfo, created from this dialog's stored datas.
     */
    public UserInfo getUserInfo() {
        return new UserInfo(nameTextField.getText(), new String(passwordField.getPassword()));
    }
    
    /**
     * Listener class for handling the confirm action on this dialog.
     */
    private class ConfirmListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String name = nameTextField.getText();
            String password = new String(passwordField.getPassword());
            
            if (isTextValid(name, password)) {
                ConnectToDatabaseDialog.this.setVisible(false);
            } else {
                // show warning
                String warning = "The entered data was incorrect.";
                JOptionPane.showMessageDialog(parent,
                        warning,
                        "Invalid Input", JOptionPane.ERROR_MESSAGE);
                nameTextField.setText("");
                nameTextField.requestFocusInWindow();
                passwordField.setText("");
            }
        }
    }

    /**
     * Function for retrieving if the given name and password is valid.
     * @param name The name to check for validity.
     * @param password The password to check for validity.
     * @return
     */
    private boolean isTextValid(String name, String password) {
        try {
            if (name.length() > 0 && password.length() > 0) {
                return true;
            }
        } catch (Exception e) {
            // One of the few times it's OK to ignore an exception
        }
        return false;
    }
    
    /**
     * Function for showing a ConfirmDialog for confirming the quit from the program.
     */
    private void showExitConfirmation() {
        int n = JOptionPane.showConfirmDialog(parent,
                "Without connecting to the database, you won't be able to use the program.\n" +
                "Are you sure you want to quit?",
                "Confirmation", JOptionPane.YES_NO_OPTION);
        if (n == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
    
    /**
     * Function for initializing this dialog's components.
     */
    private void initComponents() {

        nameLabel = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        passwordLabel = new javax.swing.JLabel();
        passwordField = new javax.swing.JPasswordField();
        connectButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        titleLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Connection");

        nameLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        nameLabel.setText("User name");

        nameTextField.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        passwordLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        passwordLabel.setText("Password");

        passwordField.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        connectButton.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        connectButton.setText("Connect");

        cancelButton.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                showExitConfirmation();
            }
        });

        titleLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        titleLabel.setText("Connect to database");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(83, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(cancelButton)
                        .addGap(67, 67, 67)
                        .addComponent(connectButton)
                        .addGap(88, 88, 88))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(passwordLabel)
                            .addComponent(nameLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(nameTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                            .addComponent(passwordField))
                        .addGap(99, 99, 99))))
            .addGroup(layout.createSequentialGroup()
                .addGap(142, 142, 142)
                .addComponent(titleLabel)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(63, Short.MAX_VALUE)
                .addComponent(titleLabel)
                .addGap(29, 29, 29)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(nameLabel))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(passwordLabel)
                    .addComponent(passwordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(59, 59, 59)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(connectButton)
                    .addComponent(cancelButton))
                .addGap(51, 51, 51))
        );

        pack();
        setLocationRelativeTo(parent);
        
        ActionListener confirmListener = new ConnectToDatabaseDialog.ConfirmListener();
        connectButton.addActionListener(confirmListener);
        nameTextField.addActionListener(confirmListener);
        passwordField.addActionListener(confirmListener);
    }
    
}
