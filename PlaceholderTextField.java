import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * Text field with placeholder text that disappears on focus/typing.
 * Also includes PlaceholderPasswordField as a static inner class.
 */
public class PlaceholderTextField extends JTextField {
    private String placeholder;
    private Color placeholderColor = UITheme.TEXT_MUTED;

    public PlaceholderTextField(String placeholder) {
        this.placeholder = placeholder;
        setFont(UITheme.BODY_FONT);
        setBackground(UITheme.INPUT_BG);
        setForeground(UITheme.TEXT_PRIMARY);
        setCaretColor(UITheme.TEXT_PRIMARY);
        setPreferredSize(new Dimension(getPreferredSize().width, UITheme.FIELD_HEIGHT));
        setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(UITheme.INPUT_BORDER, 1, true),
            BorderFactory.createEmptyBorder(5, 12, 5, 12)
        ));

        // Focus border highlight
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(UITheme.INPUT_FOCUS, 2, true),
                    BorderFactory.createEmptyBorder(4, 11, 4, 11)
                ));
                repaint();
            }
            @Override
            public void focusLost(FocusEvent e) {
                setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(UITheme.INPUT_BORDER, 1, true),
                    BorderFactory.createEmptyBorder(5, 12, 5, 12)
                ));
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (getText().isEmpty() && !hasFocus()) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setColor(placeholderColor);
            g2.setFont(getFont().deriveFont(Font.ITALIC));
            Insets insets = getInsets();
            g2.drawString(placeholder, insets.left, getHeight() / 2 + g2.getFontMetrics().getAscent() / 2 - 2);
            g2.dispose();
        }
    }

    // ==================== INNER CLASS: Password Field ====================

    /**
     * Password field with placeholder support.
     */
    public static class PlaceholderPasswordField extends JPasswordField {
        private String placeholder;

        public PlaceholderPasswordField(String placeholder) {
            this.placeholder = placeholder;
            setFont(UITheme.BODY_FONT);
            setBackground(UITheme.INPUT_BG);
            setForeground(UITheme.TEXT_PRIMARY);
            setCaretColor(UITheme.TEXT_PRIMARY);
            setPreferredSize(new Dimension(getPreferredSize().width, UITheme.FIELD_HEIGHT));
            setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(UITheme.INPUT_BORDER, 1, true),
                BorderFactory.createEmptyBorder(5, 12, 5, 12)
            ));

            addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(UITheme.INPUT_FOCUS, 2, true),
                        BorderFactory.createEmptyBorder(4, 11, 4, 11)
                    ));
                    repaint();
                }
                @Override
                public void focusLost(FocusEvent e) {
                    setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(UITheme.INPUT_BORDER, 1, true),
                        BorderFactory.createEmptyBorder(5, 12, 5, 12)
                    ));
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (getPassword().length == 0 && !hasFocus()) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setColor(UITheme.TEXT_MUTED);
                g2.setFont(getFont().deriveFont(Font.ITALIC));
                Insets insets = getInsets();
                g2.drawString(placeholder, insets.left, getHeight() / 2 + g2.getFontMetrics().getAscent() / 2 - 2);
                g2.dispose();
            }
        }
    }
}
