import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;

/**
 * Custom rounded button with modern styling.
 * Supports hover and pressed state visual feedback.
 */
public class RoundedButton extends JButton {
    private Color bgColor;
    private Color hoverColor;
    private Color pressedColor;
    private Color textColor = UITheme.TEXT_WHITE;
    private boolean hovering = false;
    private boolean pressing = false;

    public RoundedButton(String text, Color bgColor) {
        super(text);
        this.bgColor = bgColor;
        this.hoverColor = bgColor.darker();
        this.pressedColor = hoverColor.darker();

        setFont(UITheme.BUTTON_FONT);
        setForeground(textColor);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(getPreferredSize().width + 30, 42));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hovering = true;
                repaint();
            }
            @Override
            public void mouseExited(MouseEvent e) {
                hovering = false;
                pressing = false;
                repaint();
            }
            @Override
            public void mousePressed(MouseEvent e) {
                pressing = true;
                repaint();
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                pressing = false;
                repaint();
            }
        });
    }

    public RoundedButton(String text) {
        this(text, UITheme.ACCENT);
    }

    public void setButtonColor(Color c) {
        this.bgColor = c;
        this.hoverColor = c.darker();
        this.pressedColor = hoverColor.darker();
        repaint();
    }

    public void setTextColor(Color c) {
        this.textColor = c;
        setForeground(c);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color bg = pressing ? pressedColor : hovering ? hoverColor : bgColor;

        if (!isEnabled()) {
            bg = new Color(189, 195, 199);
        }

        g2.setColor(bg);
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(),
                UITheme.BUTTON_RADIUS * 2, UITheme.BUTTON_RADIUS * 2));

        g2.setFont(getFont());
        g2.setColor(isEnabled() ? textColor : Color.WHITE);
        FontMetrics fm = g2.getFontMetrics();
        int textX = (getWidth() - fm.stringWidth(getText())) / 2;
        int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
        g2.drawString(getText(), textX, textY);

        g2.dispose();
    }
}
