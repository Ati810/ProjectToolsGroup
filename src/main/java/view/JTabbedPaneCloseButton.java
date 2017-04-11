package view;

import javax.swing.*;
import javax.swing.plaf.metal.MetalIconFactory;
import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

/**
 * A class which creates a JTabbedPane and auto sets a close button when you add a tab.
 * @author 6dc
 */
public class JTabbedPaneCloseButton extends JTabbedPane {

    /**
     * Constructor for creating a new JTabbedPaneCloseButton.
     */
    public JTabbedPaneCloseButton() {
        super();
    }
    
    /**
     * Override Addtab in order to add the close Button every time.
     * @param title The title to be displayed in this tab.
     * @param icon The icon to be displayed in this tab.
     * @param component The component to be displayed when this tab is clicked.
     * @param tip The tooltip to be displayed for this tab.
     */
    @Override
    public void addTab(String title, Icon icon, Component component, String tip) {
        super.addTab(title, icon, component, tip);
        int count = this.getTabCount() - 1;

        //Csak az alpanelokat lehessen bezarni
        if (count != 0) {
            setTabComponentAt(count, new CloseButtonTab(component, title, icon));
        }
        //setTabComponentAt(count, new CloseButtonTab(component, title, icon));
    }
    
    /**
     * Override Addtab in order to add the close Button every time.
     * @param title The title to be displayed in this tab.
     * @param icon The icon to be displayed in this tab.
     * @param component The component to be displayed when this tab is clicked.
     */
    @Override
    public void addTab(String title, Icon icon, Component component) {
        addTab(title, icon, component, null);
    }

    /**
     * Override Addtab in order to add the close Button every time.
     * @param title The title to be displayed in this tab.
     * @param component The component to be displayed when this tab is clicked.
     */
    @Override
    public void addTab(String title, Component component) {
        addTab(title, null, component);
    }

    /**
     * Add a tab without an exit button.
     * @param title The title to be displayed in this tab.
     * @param icon The icon to be displayed in this tab.
     * @param component The component to be displayed when this tab is clicked.
     * @param tip The tooltip to be displayed for this tab.
     */
    public void addTabNoExit(String title, Icon icon, Component component, String tip) {
        super.addTab(title, icon, component, tip);
    }

    /**
     * Add a tab without an exit button.
     * @param title
     * @param icon
     * @param component
     */
    public void addTabNoExit(String title, Icon icon, Component component) {
        addTabNoExit(title, icon, component, null);
    }

    /**
     * Add a tab without an exit button.
     * @param title
     * @param component
     */
    public void addTabNoExit(String title, Component component) {
        addTabNoExit(title, null, component);
    }

    /**
     * Class that represents a closable tab with a button.
     */  
    public class CloseButtonTab extends JPanel {

        private Component tab;

        /**
         * Constructor for creating a new CloseButtonTab.
         * @param tab
         * @param title
         * @param icon
         */
        public CloseButtonTab(final Component tab, String title, Icon icon) {
            this.tab = tab;
            setOpaque(false);
            FlowLayout flowLayout = new FlowLayout(FlowLayout.CENTER, 3, 3);
            setLayout(flowLayout);
            JLabel jLabel = new JLabel(title);
            jLabel.setIcon(icon);
            add(jLabel);
            JButton button = new JButton(MetalIconFactory.getInternalFrameCloseIcon(5));
            button.setMargin(new Insets(0, 0, 0, 0));
            button.addMouseListener(new CloseListener(tab));
            add(button);
        }
    }

    /**
     * Listener class for handling the close of the panel.
     */
    public class CloseListener implements MouseListener {

        private Component tab;

        /**
         *
         * @param tab
         */
        public CloseListener(Component tab) {
            this.tab = tab;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getSource() instanceof JButton) {
                JButton clickedButton = (JButton) e.getSource();
                JTabbedPane tabbedPane = (JTabbedPane) clickedButton.getParent().getParent().getParent();

                //Ha a legelso panelt zarnank be, akkor ra kell kerdezni, hogy az osszes alpaneljat bezarjuk-e,
                //hiszen a fopanel nelkul nem tudunk alpanelokat megnyitni.
                if (tabbedPane.getComponentAt(0).equals(tab)) {
                    showCloseConfirmation(tabbedPane);
                } else {
                    tabbedPane.remove(tab);
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            if (e.getSource() instanceof JButton) {
                JButton clickedButton = (JButton) e.getSource();
                //   clickedButton.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY,3));
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            if (e.getSource() instanceof JButton) {
                JButton clickedButton = (JButton) e.getSource();
                //   clickedButton.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY,3));
            }
        }
    }
    
    /**
     * Function for showing a ConfirmDialog for confirming the quit from the program.
     */
    private void showCloseConfirmation(JTabbedPane tabbedPane) {
        int n = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to close the " + tabbedPane.getTitleAt(0) + " panel, and every sub-panels?",
                "Confirmation", JOptionPane.YES_NO_OPTION);
        if (n == JOptionPane.YES_OPTION) {
            tabbedPane.removeAll();
        }
    }
}
