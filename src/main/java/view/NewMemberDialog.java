package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import utilities.NewMemberInfo;

/**
 * Class that represents a dialog for getting the information of a new Member.
 * @author Attila
 */
public class NewMemberDialog extends JDialog {
    
    private JFrame parent;
    private JLabel addressLabel;
    private JTextField addressTextField;
    private JButton cancelButton;
    private JLabel nameLabel;
    private JTextField nameTextField;
    private JButton saveButton;
    private JLabel titleLabel;
    
    /**
     * Constructor for creating a new NewMemberDialog.
     * @param parent The JFrame that will be the owner of this dialog.
     * @param modal Whether this dialog should be modal or not.
     */
    public NewMemberDialog(JFrame parent, boolean modal) {
        super(parent, modal);
        this.parent = parent;
        initComponents();
    }
    
    /**
     * Getter function for retrieving the NewMemberInfo from this dialog's stored datas.
     * @return The NewMemberInfo, created from this dialog's stored datas.
     */
    public NewMemberInfo getMemberInfo() {
        return new NewMemberInfo(nameTextField.getText(), addressTextField.getText());
    }
    
    /**
     * Listener class for handling the confirm action on this dialog.
     */
    private class ConfirmListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String warning = "Please fill in every fields!";
            
            if (e.getSource().equals(cancelButton)) {
                warning = "Cancel";
                nameTextField.setText("");
                addressTextField.setText("");
                NewMemberDialog.this.dispose();
            }
            
            String name = nameTextField.getText();
            String address = addressTextField.getText();
            
            if (isTextValid(name, address)) {
                NewMemberDialog.this.setVisible(false);
            } else {
                //Ha nem Cancel a figyelmeztetes, akkor nem toltottek ki mindent, s ugy szeretnenek Member-t letrehozni
                if (!warning.equals("Cancel")) {
                    JOptionPane.showMessageDialog(parent,
                        warning,
                        "Missing data", JOptionPane.ERROR_MESSAGE);
                    //nameTextField.setText("");
                    nameTextField.requestFocusInWindow();
                }
            }
        }
    }

    /**
     * Function for retrieving if the given name and address is valid.
     * @param name The name to check for validity.
     * @param address The address to check for validity.
     * @return
     */
    public boolean isTextValid(String name, String address) {
        try {
            if (name.length() > 0 && address.length() > 1) {
                return true;
            }
        } catch (Exception e) {
            // one of the few times it's OK to ignore an exception
        }
        return false;
    }
    
    /**
     * Function for initializing this dialog's components.
     */
    private void initComponents() {

        nameLabel = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        addressLabel = new javax.swing.JLabel();
        addressTextField = new javax.swing.JTextField();
        saveButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        titleLabel = new javax.swing.JLabel();

        setTitle("New member");

        nameLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        nameLabel.setText("Name");

        nameTextField.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        addressLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        addressLabel.setText("Address");

        addressTextField.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        saveButton.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        saveButton.setText("Save");
        /*saveButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!nameTextField.getText().isEmpty() && !addressTextField.getText().isEmpty()) {
                    //Esetleg lehet fancy-zni, hogy amig nem eleg hosszu a nev / cim, vagy nem is tolti ki, akkor piros keret lesz korulotte (a megjeleno uzenet helyett).
                } else {
                    JOptionPane.showMessageDialog(getOwner(), "Please fill in every fields!",
                    "Warning", JOptionPane.WARNING_MESSAGE);
                }
            }
        });*/

        cancelButton.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        cancelButton.setText("Cancel");
        /*cancelButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });*/

        titleLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        titleLabel.setText("Create a new member");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(98, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(cancelButton)
                        .addGap(57, 57, 57)
                        .addComponent(saveButton)
                        .addGap(108, 108, 108))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(addressLabel)
                            .addComponent(nameLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(addressTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(99, 99, 99))))
            .addGroup(layout.createSequentialGroup()
                .addGap(138, 138, 138)
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
                    .addComponent(addressTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addressLabel))
                .addGap(59, 59, 59)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(saveButton)
                    .addComponent(cancelButton))
                .addGap(51, 51, 51))
        );

        pack();
        
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);
        
        ActionListener confirmListener = new NewMemberDialog.ConfirmListener();
        saveButton.addActionListener(confirmListener); // add listener
        cancelButton.addActionListener(confirmListener);
        nameTextField.addActionListener(confirmListener);
        addressTextField.addActionListener(confirmListener);
    }
    
}
