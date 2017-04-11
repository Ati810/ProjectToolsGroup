package view;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Class for the visual representation of the start page.
 * @author Attila
 */
public class StartPagePanel extends JPanel {

    private JLabel versionLabel;

    private JLabel creatorLabel;

    private JLabel iconLabel;

    /**
     * Constructor for creating a new StartPagePanel.
     */
    public StartPagePanel() {

        initComponents();

    }

    /**
     * Function for initializing this panel's components.
     */
    private void initComponents() {
        versionLabel = new JLabel("v1.0");
        creatorLabel = new JLabel("© Attila Kovács");
        iconLabel = new JLabel();

        versionLabel.setForeground(Color.WHITE);
        creatorLabel.setForeground(Color.WHITE);

        iconLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        //TODO path not good
//        iconLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/main/java/view/Library.png")));

        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();
        int i = (int) (width * 0.946);
        int j = (int) (height * 0.888);

        setSize(i, j);  //1816 x 959

        System.out.println(this.getWidth() + " x " + this.getHeight());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(versionLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(creatorLabel))
                .addGroup(javax.swing.GroupLayout.Alignment.CENTER, layout.createSequentialGroup()
                        .addComponent(iconLabel))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                .addComponent(iconLabel)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, /*97*/ (int)(j * 0.1), Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(versionLabel)
                                .addComponent(creatorLabel)))
        );

        add(versionLabel);
        add(creatorLabel);
        add(iconLabel);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        int w = getWidth();
        int h = getHeight();
        Color color1 = new Color(0, 93, 154);
        Color color2 = new Color(8, 160, 255);
        GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, w, h);
    }

}
