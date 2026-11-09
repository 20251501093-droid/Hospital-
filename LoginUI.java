import java.awt.*;
import java.util.Arrays;
import javax.swing.*;

/**
 * Modern gradient login screen with polished styling.
 * Uses SwingWorker for background authentication (non-blocking UI).
 */
public class LoginUI {

    public static void main(String[] args) {
        UITheme.applyLookAndFeel();
        // Setup database tables in background before showing UI
        HospitalTaskManager.getInstance().runInBackground(
            () -> AuthManager.setupStaffTable(),
            () -> SwingUtilities.invokeLater(() -> new LoginUI().createAndShowGUI())
        );
        // Show UI immediately while setup runs
        SwingUtilities.invokeLater(() -> new LoginUI().createAndShowGUI());
    }

    public void createAndShowGUI() {
        JFrame frame = new JFrame("Hospital Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);
        frame.setMinimumSize(new Dimension(800, 550));
        frame.setLocationRelativeTo(null);

        // Gradient background panel
        JPanel bgPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, UITheme.GRADIENT_START,
                        getWidth(), getHeight(), UITheme.GRADIENT_END);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };

        // Login card (centered white box)
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Shadow
                g2.setColor(new Color(0, 0, 0, 30));
                g2.fillRoundRect(5, 5, getWidth() - 6, getHeight() - 6, 20, 20);
                // Card
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 6, getHeight() - 6, 20, 20);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(400, 440));
        card.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // Hospital icon (Unicode cross)
        JLabel icon = new JLabel("\u2695", SwingConstants.CENTER); // ⚕
        icon.setFont(new Font("SansSerif", Font.PLAIN, 48));
        icon.setForeground(UITheme.ACCENT);
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Title
        JLabel title = new JLabel("Hospital Management");
        title.setFont(UITheme.TITLE_FONT);
        title.setForeground(UITheme.TEXT_PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Subtitle
        JLabel subtitle = new JLabel("Secure Staff Portal");
        subtitle.setFont(UITheme.BODY_FONT);
        subtitle.setForeground(UITheme.TEXT_SECONDARY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Username field
        PlaceholderTextField userField = new PlaceholderTextField("Username");
        userField.setMaximumSize(new Dimension(Integer.MAX_VALUE, UITheme.FIELD_HEIGHT));

        // Password field
        PlaceholderTextField.PlaceholderPasswordField passField =
            new PlaceholderTextField.PlaceholderPasswordField("Password");
        passField.setMaximumSize(new Dimension(Integer.MAX_VALUE, UITheme.FIELD_HEIGHT));

        // Login button
        RoundedButton loginBtn = new RoundedButton("Sign In", UITheme.ACCENT);
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Status label
        JLabel statusLabel = new JLabel(" ");
        statusLabel.setFont(UITheme.SMALL_FONT);
        statusLabel.setForeground(UITheme.DANGER);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Credential hints
        JLabel hint = new JLabel("<html><center>Demo: admin/admin123 · dr_house/doc123 · tech_lisa/tech123</center></html>");
        hint.setFont(new Font("SansSerif", Font.PLAIN, 10));
        hint.setForeground(UITheme.TEXT_MUTED);
        hint.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Layout
        card.add(icon);
        card.add(Box.createVerticalStrut(10));
        card.add(title);
        card.add(Box.createVerticalStrut(5));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(30));
        card.add(userField);
        card.add(Box.createVerticalStrut(12));
        card.add(passField);
        card.add(Box.createVerticalStrut(8));
        card.add(statusLabel);
        card.add(Box.createVerticalStrut(12));
        card.add(loginBtn);
        card.add(Box.createVerticalStrut(15));
        card.add(hint);

        bgPanel.add(card);
        frame.setContentPane(bgPanel);
        frame.setVisible(true);

        // Ensure DB setup is done
        AuthManager.setupStaffTable();

        // Login action
        Runnable doLogin = () -> {
            String username = userField.getText().trim();
            char[] passwordChars = passField.getPassword();
            String password = new String(passwordChars);

            if (username.isEmpty() || password.isEmpty()) {
                statusLabel.setText("Please enter username and password");
                return;
            }

            loginBtn.setEnabled(false);
            loginBtn.setText("Authenticating...");
            statusLabel.setText(" ");

            // THREADING: Authenticate in background using SwingWorker
            HospitalTaskManager.getInstance().runInBackground(
                () -> AuthManager.authenticate(username, password),
                role -> {
                    // Success — on EDT
                    ActivityLog.getInstance().log("AUTH", username + " logged in as " + role);
                    frame.dispose();
                    SwingUtilities.invokeLater(() -> new HospitalUI(role, username).createAndShowGUI());
                },
                error -> {
                    // Failure — on EDT
                    loginBtn.setEnabled(true);
                    loginBtn.setText("Sign In");
                    statusLabel.setText("Invalid credentials. Please try again.");
                    ActivityLog.getInstance().log("AUTH", "Failed login attempt for: " + username);
                }
            );

            // Clear password from memory
            Arrays.fill(passwordChars, '\0');
        };

        loginBtn.addActionListener(e -> doLogin.run());

        // Enter key triggers login
        passField.addActionListener(e -> doLogin.run());
        userField.addActionListener(e -> passField.requestFocus());
    }
}
