import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * Centralized UI theme for the Hospital Management System.
 * All colors, fonts, and dimensions are defined here for consistency.
 */
public class UITheme {

    // ==================== COLORS ====================
    // Sidebar
    public static final Color SIDEBAR_BG        = new Color(27, 40, 56);
    public static final Color SIDEBAR_SELECTED   = new Color(52, 152, 219);
    public static final Color SIDEBAR_HOVER      = new Color(44, 62, 80);
    public static final Color SIDEBAR_TEXT       = new Color(236, 240, 241);

    // Accent
    public static final Color ACCENT             = new Color(52, 152, 219);
    public static final Color ACCENT_HOVER       = new Color(41, 128, 185);
    public static final Color ACCENT_DARK        = new Color(31, 97, 141);

    // Status
    public static final Color SUCCESS            = new Color(39, 174, 96);
    public static final Color SUCCESS_HOVER      = new Color(30, 132, 73);
    public static final Color DANGER             = new Color(231, 76, 60);
    public static final Color DANGER_HOVER       = new Color(192, 57, 43);
    public static final Color WARNING            = new Color(243, 156, 18);

    // Backgrounds
    public static final Color BG_MAIN            = new Color(240, 242, 245);
    public static final Color CARD_BG            = Color.WHITE;
    public static final Color HEADER_BG          = Color.WHITE;
    public static final Color STATUS_BAR_BG      = new Color(27, 40, 56);

    // Text
    public static final Color TEXT_PRIMARY       = new Color(44, 62, 80);
    public static final Color TEXT_SECONDARY     = new Color(127, 140, 141);
    public static final Color TEXT_WHITE         = new Color(236, 240, 241);
    public static final Color TEXT_MUTED         = new Color(189, 195, 199);

    // Input
    public static final Color INPUT_BG           = new Color(248, 249, 250);
    public static final Color INPUT_BORDER       = new Color(222, 226, 230);
    public static final Color INPUT_FOCUS        = new Color(52, 152, 219);

    // Login gradient
    public static final Color GRADIENT_START     = new Color(44, 62, 80);
    public static final Color GRADIENT_END       = new Color(52, 152, 219);

    // ==================== FONTS ====================
    public static final Font TITLE_FONT          = new Font("SansSerif", Font.BOLD, 26);
    public static final Font HEADING_FONT        = new Font("SansSerif", Font.BOLD, 18);
    public static final Font SUBHEADING_FONT     = new Font("SansSerif", Font.BOLD, 15);
    public static final Font BODY_FONT           = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font SMALL_FONT          = new Font("SansSerif", Font.PLAIN, 12);
    public static final Font MONO_FONT           = new Font("Monospaced", Font.PLAIN, 13);
    public static final Font SIDEBAR_FONT        = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font BUTTON_FONT         = new Font("SansSerif", Font.BOLD, 14);
    public static final Font STAT_NUMBER_FONT    = new Font("SansSerif", Font.BOLD, 32);
    public static final Font STAT_LABEL_FONT     = new Font("SansSerif", Font.PLAIN, 12);

    // ==================== DIMENSIONS ====================
    public static final int SIDEBAR_WIDTH        = 240;
    public static final int HEADER_HEIGHT        = 65;
    public static final int STATUS_BAR_HEIGHT    = 32;
    public static final int CARD_RADIUS          = 15;
    public static final int BUTTON_RADIUS        = 8;
    public static final int FIELD_HEIGHT         = 42;

    // ==================== UTILITY METHODS ====================

    /** Creates a white rounded card panel with shadow effect */
    public static JPanel createCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Shadow
                g2.setColor(new Color(0, 0, 0, 15));
                g2.fillRoundRect(3, 3, getWidth() - 4, getHeight() - 4, CARD_RADIUS, CARD_RADIUS);
                // Card
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 4, CARD_RADIUS, CARD_RADIUS);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        return card;
    }

    /** Creates a stat card with a large number and label */
    public static JPanel createStatCard(String number, String label, Color accentColor) {
        JPanel card = createCard();
        card.setLayout(new BorderLayout());
        card.setPreferredSize(new Dimension(180, 110));

        // Color strip at top
        JPanel strip = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(accentColor);
                g2.fillRoundRect(0, 0, getWidth(), 6, 3, 3);
                g2.dispose();
            }
        };
        strip.setOpaque(false);
        strip.setPreferredSize(new Dimension(0, 6));
        card.add(strip, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(8, 5, 5, 5));

        JLabel numLabel = new JLabel(number);
        numLabel.setFont(STAT_NUMBER_FONT);
        numLabel.setForeground(TEXT_PRIMARY);
        numLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel descLabel = new JLabel(label);
        descLabel.setFont(STAT_LABEL_FONT);
        descLabel.setForeground(TEXT_SECONDARY);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(numLabel);
        content.add(Box.createVerticalStrut(2));
        content.add(descLabel);
        card.add(content, BorderLayout.CENTER);

        return card;
    }

    /** Styles a text field with modern appearance */
    public static void styleFormField(JTextField field) {
        field.setFont(BODY_FONT);
        field.setBackground(INPUT_BG);
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(TEXT_PRIMARY);
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, FIELD_HEIGHT));
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(INPUT_BORDER, 1, true),
            BorderFactory.createEmptyBorder(5, 12, 5, 12)
        ));
    }

    /** Creates a styled section title label */
    public static JLabel createSectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(HEADING_FONT);
        label.setForeground(TEXT_PRIMARY);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        return label;
    }

    /** Creates a styled form label */
    public static JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(BODY_FONT);
        label.setForeground(TEXT_SECONDARY);
        return label;
    }

    /** Apply system look and feel with anti-aliasing */
    public static void applyLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
    }
}
