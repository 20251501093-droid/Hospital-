import java.awt.*;
import javax.swing.*;

/**
 * Loading overlay dialog with spinning animation.
 * Shows a semi-transparent overlay with a spinner and message.
 */
public class LoadingDialog {
    private JDialog dialog;
    private Timer animationTimer;
    private int angle = 0;

    public LoadingDialog(JFrame parent, String message) {
        dialog = new JDialog(parent, true);
        dialog.setUndecorated(true);
        dialog.setSize(200, 160);
        dialog.setLocationRelativeTo(parent);
        dialog.setBackground(new Color(0, 0, 0, 0));

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Semi-transparent background
                g2.setColor(new Color(44, 62, 80, 230));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                // Spinning arc
                g2.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int cx = getWidth() / 2;
                int cy = 60;
                int r = 25;

                g2.setColor(new Color(255, 255, 255, 60));
                g2.drawOval(cx - r, cy - r, r * 2, r * 2);

                g2.setColor(UITheme.ACCENT);
                g2.drawArc(cx - r, cy - r, r * 2, r * 2, angle, 90);

                // Message text
                g2.setColor(Color.WHITE);
                g2.setFont(UITheme.BODY_FONT);
                FontMetrics fm = g2.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(message)) / 2;
                g2.drawString(message, textX, 115);

                g2.dispose();
            }
        };
        panel.setOpaque(false);
        dialog.setContentPane(panel);

        animationTimer = new Timer(40, e -> {
            angle = (angle + 10) % 360;
            dialog.repaint();
        });
    }

    /** Show the loading overlay (blocks until hideLoading is called from another thread) */
    public void showLoading() {
        animationTimer.start();
        // Show on EDT without blocking the calling thread
        dialog.setVisible(true);
    }

    /** Show loading in a non-blocking way */
    public void showLoadingAsync() {
        animationTimer.start();
        SwingUtilities.invokeLater(() -> dialog.setVisible(true));
    }

    /** Hide the loading overlay */
    public void hideLoading() {
        SwingUtilities.invokeLater(() -> {
            animationTimer.stop();
            dialog.setVisible(false);
            dialog.dispose();
        });
    }
}
