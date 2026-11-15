import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.List;
import javax.swing.*;

/**
 * Modern Hospital Dashboard with sidebar navigation, live stats, and multi-threading.
 *
 * Layout:
 * ┌──────────────────────────────────────────────┐
 * │  Header (role, user, logout)                 │
 * ├──────┬───────────────────────────────────────┤
 * │ Side │  Content Area (CardLayout)            │
 * │ bar  │                                       │
 * ├──────┴───────────────────────────────────────┤
 * │  Status Bar (threads, tasks, db)             │
 * └──────────────────────────────────────────────┘
 */
public class HospitalUI {

    private static boolean nativeBridgeLoaded = false;
    static {
        try {
            System.loadLibrary("BillingBridge");
            nativeBridgeLoaded = true;
        } catch (Throwable t) {
            System.err.println("[Warning] Native BillingBridge library could not be loaded: " + t.getMessage());
            nativeBridgeLoaded = false;
        }
    }

    private final String role;
    private final String username;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JLabel statusLabel;
    private JPanel sidebarPanel;
    private String currentView = "";

    public HospitalUI(String role, String username) {
        this.role = role;
        this.username = username;
    }

    // Keep old constructor for backward compatibility
    public HospitalUI(String role) {
        this(role, role.toLowerCase());
    }

    public void createAndShowGUI() {
        JFrame frame = new JFrame(Patient.HOSPITAL_BRANCH + " — Hospital Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 750);
        frame.setMinimumSize(new Dimension(1000, 650));
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        // Header
        frame.add(buildHeader(frame), BorderLayout.NORTH);

        // Sidebar
        sidebarPanel = buildSidebar();
        frame.add(sidebarPanel, BorderLayout.WEST);

        // Content area with CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(UITheme.BG_MAIN);

        // Add panels based on role
        contentPanel.add(buildDashboardPanel(), "Dashboard");

        if (role.equals("Admin")) {
            contentPanel.add(buildRegistrationPanel(), "Registration");
            contentPanel.add(buildBillingPanel(), "Billing");
            contentPanel.add(buildOOPDemoPanel(), "Demo");
        } else if (role.equals("Doctor")) {
            contentPanel.add(buildDoctorPanel(), "Records");
            contentPanel.add(buildPrescriptionPanel(), "Prescriptions");
        } else if (role.equals("Pharmacist")) {
            contentPanel.add(buildPharmacyPanel(), "Pharmacy");
        }

        contentPanel.add(buildThreadingDemoPanel(), "Threading");

        frame.add(contentPanel, BorderLayout.CENTER);

        // Status bar
        frame.add(buildStatusBar(), BorderLayout.SOUTH);

        switchView("Dashboard");
        frame.setVisible(true);

        ActivityLog.getInstance().log("SYSTEM", role + " dashboard loaded for " + username);
    }

    // ======================== HEADER ========================
    private JPanel buildHeader(JFrame frame) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.HEADER_BG);
        header.setPreferredSize(new Dimension(0, UITheme.HEADER_HEIGHT));
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(0, 20, 0, 20)
        ));

        JLabel titleLabel = new JLabel("\u2695  Hospital Management System");
        titleLabel.setFont(UITheme.HEADING_FONT);
        titleLabel.setForeground(UITheme.TEXT_PRIMARY);
        header.add(titleLabel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setOpaque(false);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        JLabel userLabel = new JLabel("\uD83D\uDC64 " + username + " (" + role + ")");
        userLabel.setFont(UITheme.BODY_FONT);
        userLabel.setForeground(UITheme.TEXT_SECONDARY);

        RoundedButton logoutBtn = new RoundedButton("Logout", UITheme.DANGER);
        logoutBtn.setPreferredSize(new Dimension(90, 34));
        logoutBtn.addActionListener(e -> {
            ActivityLog.getInstance().log("AUTH", username + " logged out");
            frame.dispose();
            SwingUtilities.invokeLater(() -> new LoginUI().createAndShowGUI());
        });

        rightPanel.add(userLabel);
        rightPanel.add(logoutBtn);
        header.add(rightPanel, BorderLayout.EAST);

        return header;
    }

    // ======================== SIDEBAR ========================
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(UITheme.SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(UITheme.SIDEBAR_WIDTH, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        addSidebarButton(sidebar, "\uD83D\uDCCA", "Dashboard", "Dashboard");

        if (role.equals("Admin")) {
            addSidebarButton(sidebar, "\uD83D\uDCDD", "Patient Registration", "Registration");
            addSidebarButton(sidebar, "\uD83D\uDCB3", "Discharge Billing", "Billing");
            addSidebarButton(sidebar, "\uD83C\uDF93", "Demo", "Demo");
        } else if (role.equals("Doctor")) {
            addSidebarButton(sidebar, "\uD83D\uDCC1", "Medical Records", "Records");
            addSidebarButton(sidebar, "\uD83D\uDCDD", "Issue Prescription", "Prescriptions");
        } else if (role.equals("Pharmacist")) {
            addSidebarButton(sidebar, "\uD83D\uDC8A", "Pharmacy Queue", "Pharmacy");
        }

        addSidebarButton(sidebar, "\u2699\uFE0F", "Threading Demo", "Threading");

        sidebar.add(Box.createVerticalGlue());

        // Version info at bottom
        JLabel version = new JLabel("v2.0 — Multithreaded");
        version.setFont(UITheme.SMALL_FONT);
        version.setForeground(new Color(100, 120, 140));
        version.setAlignmentX(Component.CENTER_ALIGNMENT);
        version.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        sidebar.add(version);

        return sidebar;
    }

    private void addSidebarButton(JPanel sidebar, String icon, String text, String viewName) {
        JButton btn = new JButton(icon + "  " + text);
        btn.setFont(UITheme.SIDEBAR_FONT);
        btn.setForeground(UITheme.SIDEBAR_TEXT);
        btn.setBackground(UITheme.SIDEBAR_BG);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        btn.setPreferredSize(new Dimension(UITheme.SIDEBAR_WIDTH, 48));
        btn.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 10));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!viewName.equals(currentView)) {
                    btn.setBackground(UITheme.SIDEBAR_HOVER);
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (!viewName.equals(currentView)) {
                    btn.setBackground(UITheme.SIDEBAR_BG);
                }
            }
        });

        btn.addActionListener(e -> switchView(viewName));
        sidebar.add(btn);
    }

    private void switchView(String viewName) {
        currentView = viewName;
        cardLayout.show(contentPanel, viewName);
        // Update sidebar selection colors
        for (Component c : sidebarPanel.getComponents()) {
            if (c instanceof JButton) {
                JButton btn = (JButton) c;
                if (btn.getText().contains(viewName) || (viewName.equals("Dashboard") && btn.getText().contains("Dashboard"))) {
                    btn.setBackground(UITheme.SIDEBAR_SELECTED);
                } else {
                    btn.setBackground(UITheme.SIDEBAR_BG);
                }
            }
        }
    }

    // ======================== STATUS BAR ========================
    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(UITheme.STATUS_BAR_BG);
        bar.setPreferredSize(new Dimension(0, UITheme.STATUS_BAR_HEIGHT));
        bar.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));

        statusLabel = new JLabel("\u2022 Connected to SQLite  |  Threads: 4  |  RSA-2048 Active");
        statusLabel.setFont(UITheme.SMALL_FONT);
        statusLabel.setForeground(UITheme.TEXT_WHITE);

        JLabel rxLabel = new JLabel("Rx Queue: " + PrescriptionQueue.getInstance().getPendingCount());
        rxLabel.setFont(UITheme.SMALL_FONT);
        rxLabel.setForeground(UITheme.TEXT_WHITE);

        // Update status bar periodically
        Timer statusTimer = new Timer(2000, e -> {
            int active = HospitalTaskManager.getInstance().getActiveTaskCount();
            int done = HospitalTaskManager.getInstance().getCompletedTaskCount();
            statusLabel.setText("\u2022 SQLite Connected  |  Workers: " + active + " active, " + done + " completed  |  RSA-2048");
            rxLabel.setText("Rx Queue: " + PrescriptionQueue.getInstance().getPendingCount());
        });
        statusTimer.start();

        bar.add(statusLabel, BorderLayout.WEST);
        bar.add(rxLabel, BorderLayout.EAST);

        return bar;
    }

    // ======================== DASHBOARD ========================
    private JPanel buildDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(UITheme.BG_MAIN);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // Welcome header
        JLabel welcome = new JLabel("Welcome back, " + username);
        welcome.setFont(UITheme.TITLE_FONT);
        welcome.setForeground(UITheme.TEXT_PRIMARY);

        JLabel roleLabel = new JLabel("Logged in as " + role + "  •  " + Patient.HOSPITAL_BRANCH);
        roleLabel.setFont(UITheme.BODY_FONT);
        roleLabel.setForeground(UITheme.TEXT_SECONDARY);

        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.add(welcome);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(roleLabel);

        // Stats cards row
        JPanel statsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        statsRow.setOpaque(false);
        statsRow.add(UITheme.createStatCard(String.valueOf(Patient.getTotalPatients()), "Total Patients", UITheme.ACCENT));
        statsRow.add(UITheme.createStatCard("3", "Staff Members", UITheme.SUCCESS));
        statsRow.add(UITheme.createStatCard(String.valueOf(PrescriptionQueue.getInstance().getPendingCount()), "Pending Rx", UITheme.WARNING));
        statsRow.add(UITheme.createStatCard("4", "Worker Threads", new Color(155, 89, 182)));

        JPanel topPanel = new JPanel(new BorderLayout(0, 20));
        topPanel.setOpaque(false);
        topPanel.add(headerPanel, BorderLayout.NORTH);
        topPanel.add(statsRow, BorderLayout.CENTER);

        panel.add(topPanel, BorderLayout.NORTH);

        // Activity log feed
        JPanel logCard = UITheme.createCard();
        logCard.setLayout(new BorderLayout(0, 10));
        logCard.add(UITheme.createSectionTitle("Recent Activity"), BorderLayout.NORTH);

        JTextArea logArea = new JTextArea();
        logArea.setFont(UITheme.MONO_FONT);
        logArea.setBackground(new Color(248, 249, 250));
        logArea.setForeground(UITheme.TEXT_PRIMARY);
        logArea.setEditable(false);
        logArea.setLineWrap(true);
        logArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Show existing log entries
        refreshActivityLog(logArea);

        // Listen for new entries
        ActivityLog.getInstance().addListener(entry -> refreshActivityLog(logArea));

        JScrollPane scroll = new JScrollPane(logArea);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.INPUT_BORDER));
        logCard.add(scroll, BorderLayout.CENTER);

        panel.add(logCard, BorderLayout.CENTER);

        return panel;
    }

    private void refreshActivityLog(JTextArea logArea) {
        StringBuilder sb = new StringBuilder();
        for (ActivityLog.ActivityEntry entry : ActivityLog.getInstance().getRecent(20)) {
            sb.append(entry.toString()).append("\n");
        }
        logArea.setText(sb.toString());
    }

    // ======================== REGISTRATION (Admin) ========================
    private JPanel buildRegistrationPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(UITheme.BG_MAIN);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JPanel card = UITheme.createCard();
        card.setLayout(new BorderLayout(0, 15));

        JLabel title = UITheme.createSectionTitle("Register New Patient (RSA-2048 Encrypted)");
        card.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(6, 2, 15, 12));
        form.setOpaque(false);

        PlaceholderTextField nameField = new PlaceholderTextField("Full name");
        PlaceholderTextField idField = new PlaceholderTextField("e.g. P10025");
        PlaceholderTextField ageField = new PlaceholderTextField("Age");
        PlaceholderTextField contactField = new PlaceholderTextField("Phone number");
        PlaceholderTextField bloodField = new PlaceholderTextField("e.g. A+, B-, O+");

        form.add(UITheme.createFormLabel("Patient Name")); form.add(nameField);
        form.add(UITheme.createFormLabel("Patient ID")); form.add(idField);
        form.add(UITheme.createFormLabel("Age")); form.add(ageField);
        form.add(UITheme.createFormLabel("Contact")); form.add(contactField);
        form.add(UITheme.createFormLabel("Blood Group")); form.add(bloodField);

        RoundedButton saveBtn = new RoundedButton("Encrypt & Save Patient", UITheme.SUCCESS);
        JLabel statusLbl = new JLabel(" ");
        statusLbl.setFont(UITheme.SMALL_FONT);

        form.add(statusLbl); form.add(saveBtn);

        card.add(form, BorderLayout.CENTER);
        panel.add(card, BorderLayout.CENTER);

        saveBtn.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String id = idField.getText().trim();
                int age = Integer.parseInt(ageField.getText().trim());
                String contact = contactField.getText().trim();
                String blood = bloodField.getText().trim();

                if (name.isEmpty() || id.isEmpty()) {
                    statusLbl.setForeground(UITheme.DANGER);
                    statusLbl.setText("Name and ID are required.");
                    return;
                }

                saveBtn.setEnabled(false);
                saveBtn.setText("Encrypting...");
                statusLbl.setForeground(UITheme.TEXT_SECONDARY);
                statusLbl.setText("RSA-2048 encrypting in background thread...");

                Patient newPatient = new Patient(name, contact, id, age);
                Patient.MedicalRecord record = newPatient.new MedicalRecord(blood);

                // THREADING: Background encryption & DB save via SwingWorker
                HospitalTaskManager.getInstance().runInBackground(
                    () -> {
                        DatabaseManager.getInstance().saveSecurePatient(newPatient, record.getBloodGroup());
                        return newPatient;
                    },
                    result -> {
                        statusLbl.setForeground(UITheme.SUCCESS);
                        statusLbl.setText("\u2713 Patient '" + name + "' encrypted & saved! (Thread: " +
                            Thread.currentThread().getName() + ")");
                        saveBtn.setEnabled(true);
                        saveBtn.setText("Encrypt & Save Patient");
                        ActivityLog.getInstance().log("PATIENT", "Registered: " + name + " (ID: " + id + ")");
                        nameField.setText(""); idField.setText(""); ageField.setText("");
                        contactField.setText(""); bloodField.setText("");
                    },
                    error -> {
                        statusLbl.setForeground(UITheme.DANGER);
                        statusLbl.setText("Error: " + error.getMessage());
                        saveBtn.setEnabled(true);
                        saveBtn.setText("Encrypt & Save Patient");
                    }
                );
            } catch (NumberFormatException ex) {
                statusLbl.setForeground(UITheme.DANGER);
                statusLbl.setText("Age must be a number.");
            }
        });

        return panel;
    }

    // ======================== BILLING (Admin - C++ Bridge) ========================
    private JPanel buildBillingPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(UITheme.BG_MAIN);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JPanel card = UITheme.createCard();
        card.setLayout(new BorderLayout(0, 15));
        card.add(UITheme.createSectionTitle("C++ Native Discharge Billing (SWIG Bridge)"), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(4, 2, 15, 12));
        form.setOpaque(false);

        PlaceholderTextField billIdField = new PlaceholderTextField("Bill ID");
        billIdField.setText("B1001");
        PlaceholderTextField amountField = new PlaceholderTextField("Amount");
        amountField.setText("1500.00");
        PlaceholderTextField vipField = new PlaceholderTextField("VIP Lounge");
        vipField.setText("Platinum Lounge");

        form.add(UITheme.createFormLabel("Bill ID")); form.add(billIdField);
        form.add(UITheme.createFormLabel("Base Amount (\u0024)")); form.add(amountField);
        form.add(UITheme.createFormLabel("VIP Add-on")); form.add(vipField);

        RoundedButton calcBtn = new RoundedButton("Generate via C++ Bridge", new Color(178, 34, 34));
        form.add(new JLabel()); form.add(calcBtn);

        card.add(form, BorderLayout.NORTH);

        JTextArea receiptArea = new JTextArea();
        receiptArea.setFont(UITheme.MONO_FONT);
        receiptArea.setBackground(new Color(30, 30, 30));
        receiptArea.setForeground(new Color(0, 255, 0));
        receiptArea.setEditable(false);
        receiptArea.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JScrollPane scroll = new JScrollPane(receiptArea);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        card.add(scroll, BorderLayout.CENTER);

        panel.add(card, BorderLayout.CENTER);

        calcBtn.addActionListener(e -> {
            calcBtn.setEnabled(false);
            calcBtn.setText("Processing...");

            HospitalTaskManager.getInstance().runInBackground(
                () -> {
                    if (!nativeBridgeLoaded) {
                        throw new IllegalStateException("Native library (libBillingBridge) is not compiled or loaded for this operating system. Please run 'build.sh' or build with CMake/SWIG.");
                    }
                    return BillingBridge.processDischarge(
                        billIdField.getText(), Double.parseDouble(amountField.getText()), vipField.getText());
                },
                result -> {
                    receiptArea.setText(result);
                    calcBtn.setEnabled(true);
                    calcBtn.setText("Generate via C++ Bridge");
                    ActivityLog.getInstance().log("BILLING", "Discharge processed: " + billIdField.getText());
                },
                error -> {
                    receiptArea.setText("C++ Bridge Error: " + error.getMessage());
                    calcBtn.setEnabled(true);
                    calcBtn.setText("Generate via C++ Bridge");
                }
            );
        });

        return panel;
    }

    // ======================== MEDICAL RECORDS (Doctor) ========================
    private JPanel buildDoctorPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(UITheme.BG_MAIN);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JPanel card = UITheme.createCard();
        card.setLayout(new BorderLayout(0, 15));
        card.add(UITheme.createSectionTitle("Search & Decrypt Medical Records"), BorderLayout.NORTH);

        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchRow.setOpaque(false);

        PlaceholderTextField searchField = new PlaceholderTextField("Enter Patient ID");
        searchField.setPreferredSize(new Dimension(250, UITheme.FIELD_HEIGHT));
        RoundedButton searchBtn = new RoundedButton("Decrypt Record", new Color(139, 0, 0));

        searchRow.add(searchField);
        searchRow.add(searchBtn);

        JPanel topSection = new JPanel(new BorderLayout(0, 15));
        topSection.setOpaque(false);
        topSection.add(card.getComponent(0), BorderLayout.NORTH); // title
        topSection.add(searchRow, BorderLayout.CENTER);
        card.add(topSection, BorderLayout.NORTH);

        JTextArea display = new JTextArea("Enter a Patient ID to decrypt their RSA-encrypted record.\n\nDecryption runs in a background thread.");
        display.setFont(UITheme.MONO_FONT);
        display.setBackground(new Color(40, 44, 52));
        display.setForeground(new Color(152, 195, 121));
        display.setEditable(false);
        display.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JScrollPane scroll = new JScrollPane(display);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        card.add(scroll, BorderLayout.CENTER);

        panel.add(card, BorderLayout.CENTER);

        searchBtn.addActionListener(e -> {
            String id = searchField.getText().trim();
            if (id.isEmpty()) return;

            searchBtn.setEnabled(false);
            searchBtn.setText("Decrypting...");
            display.setText("Decrypting record in background thread...\n");

            HospitalTaskManager.getInstance().runInBackground(
                () -> DatabaseManager.getInstance().fetchAndDecryptPatient(id),
                data -> {
                    display.setText("=== SECURE MEDICAL RECORD (RSA-2048 Decrypted) ===\n\n");
                    display.append("  Patient ID:  " + id + "\n");
                    display.append("  Name:        " + data[0] + "  (decrypted)\n");
                    display.append("  Age:         " + data[1] + "  (decrypted)\n");
                    display.append("  Contact:     " + data[2] + "  (decrypted)\n");
                    display.append("  Blood Group: " + data[3] + "  (decrypted)\n\n");
                    display.append("  [Thread: " + Thread.currentThread().getName() + "]\n");
                    searchBtn.setEnabled(true);
                    searchBtn.setText("Decrypt Record");
                    ActivityLog.getInstance().log("PATIENT", "Record decrypted for ID: " + id);
                },
                error -> {
                    if (error instanceof PatientNotFoundException) {
                        display.setText("PATIENT NOT FOUND\n\n" + error.getMessage());
                    } else {
                        display.setText("ERROR\n\n" + error.getMessage());
                    }
                    searchBtn.setEnabled(true);
                    searchBtn.setText("Decrypt Record");
                }
            );
        });

        return panel;
    }

    // ======================== PRESCRIPTIONS (Doctor - Producer) ========================
    private JPanel buildPrescriptionPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(UITheme.BG_MAIN);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JPanel card = UITheme.createCard();
        card.setLayout(new BorderLayout(0, 15));
        card.add(UITheme.createSectionTitle("Issue Prescription (Producer → Queue → Pharmacist)"), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(5, 2, 15, 12));
        form.setOpaque(false);

        PlaceholderTextField rxIdField = new PlaceholderTextField("Auto-generated");
        rxIdField.setText("RX-" + (System.currentTimeMillis() % 10000));
        PlaceholderTextField patIdField = new PlaceholderTextField("Patient ID");
        PlaceholderTextField medsField = new PlaceholderTextField("e.g. Paracetamol, Amoxicillin");
        PlaceholderTextField dosageField = new PlaceholderTextField("e.g. 500mg twice daily");

        form.add(UITheme.createFormLabel("Prescription ID")); form.add(rxIdField);
        form.add(UITheme.createFormLabel("Patient ID")); form.add(patIdField);
        form.add(UITheme.createFormLabel("Medications (comma-separated)")); form.add(medsField);
        form.add(UITheme.createFormLabel("Dosage")); form.add(dosageField);

        RoundedButton issueBtn = new RoundedButton("Issue & Send to Pharmacy Queue", UITheme.ACCENT);
        form.add(new JLabel()); form.add(issueBtn);

        card.add(form, BorderLayout.NORTH);

        JTextArea output = new JTextArea();
        output.setFont(UITheme.MONO_FONT);
        output.setBackground(new Color(40, 44, 52));
        output.setForeground(new Color(152, 195, 121));
        output.setEditable(false);
        output.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JScrollPane scroll = new JScrollPane(output);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        card.add(scroll, BorderLayout.CENTER);

        panel.add(card, BorderLayout.CENTER);

        issueBtn.addActionListener(e -> {
            try {
                String rxId = rxIdField.getText().trim();
                String patId = patIdField.getText().trim();
                if (rxId.isEmpty() || patId.isEmpty()) {
                    throw new InvalidPrescriptionException("Prescription ID and Patient ID are required.");
                }

                List<String> meds = Arrays.asList(medsField.getText().split(",\\s*"));
                if (meds.isEmpty() || meds.get(0).trim().isEmpty()) {
                    throw new InvalidPrescriptionException("At least one medication is required.");
                }

                Prescription rx = new Prescription(rxId, patId, "DR-" + username,
                    meds, dosageField.getText(), java.time.LocalDate.now().toString());

                // THREADING: Add to producer-consumer queue (thread-safe)
                PrescriptionQueue.getInstance().addPrescription(rx);

                output.setText("=== PRESCRIPTION ISSUED (IMMUTABLE) ===\n\n");
                output.append("  " + rx.toString() + "\n\n");
                output.append("  [PRODUCER-CONSUMER] Prescription added to BlockingQueue.\n");
                output.append("  Pharmacist will see it immediately in the Pharmacy Queue.\n");
                output.append("  Pending in queue: " + PrescriptionQueue.getInstance().getPendingCount() + "\n");

                ActivityLog.getInstance().log("RX", "Prescription " + rxId + " issued for " + patId);

                // Auto-generate next ID
                rxIdField.setText("RX-" + (System.currentTimeMillis() % 10000));
                patIdField.setText(""); medsField.setText(""); dosageField.setText("");

            } catch (InvalidPrescriptionException ex) {
                output.setText("VALIDATION ERROR\n\n" + ex.getMessage());
            }
        });

        return panel;
    }

    // ======================== PHARMACY (Pharmacist - Consumer) ========================
    private JPanel buildPharmacyPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(UITheme.BG_MAIN);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JPanel card = UITheme.createCard();
        card.setLayout(new BorderLayout(0, 15));
        card.add(UITheme.createSectionTitle("Pharmacy Queue (Consumer — Receives from Doctor)"), BorderLayout.NORTH);

        JTextArea queueDisplay = new JTextArea("Waiting for prescriptions from doctors...\n\n" +
            "[CONSUMER] This panel listens to the BlockingQueue.\n" +
            "New prescriptions appear automatically via QueueListener.\n");
        queueDisplay.setFont(UITheme.MONO_FONT);
        queueDisplay.setBackground(new Color(40, 44, 52));
        queueDisplay.setForeground(new Color(152, 195, 121));
        queueDisplay.setEditable(false);
        queueDisplay.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JScrollPane scroll = new JScrollPane(queueDisplay);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnRow.setOpaque(false);

        RoundedButton refreshBtn = new RoundedButton("Refresh Queue", UITheme.ACCENT);
        RoundedButton dispenseBtn = new RoundedButton("Dispense Next", UITheme.SUCCESS);
        btnRow.add(refreshBtn);
        btnRow.add(dispenseBtn);

        card.add(btnRow, BorderLayout.SOUTH);
        card.add(scroll, BorderLayout.CENTER);
        panel.add(card, BorderLayout.CENTER);

        Runnable refreshQueue = () -> {
            List<Prescription> pending = PrescriptionQueue.getInstance().peekAll();
            if (pending.isEmpty()) {
                queueDisplay.setText("=== PHARMACY QUEUE ===\n\nNo pending prescriptions.\nWaiting for doctors to issue new ones...\n");
            } else {
                StringBuilder sb = new StringBuilder("=== PHARMACY QUEUE (" + pending.size() + " pending) ===\n\n");
                int i = 1;
                for (Prescription rx : pending) {
                    sb.append("  ").append(i++).append(". ").append(rx.toString()).append("\n");
                }
                sb.append("\n[IMMUTABLE] Prescriptions cannot be altered by the pharmacist.\n");
                queueDisplay.setText(sb.toString());
            }
        };

        refreshBtn.addActionListener(e -> refreshQueue.run());

        dispenseBtn.addActionListener(e -> {
            Prescription rx = PrescriptionQueue.getInstance().pollPrescription();
            if (rx != null) {
                ActivityLog.getInstance().log("RX", "Dispensed: " + rx.getPrescriptionId());
                refreshQueue.run();
                queueDisplay.append("\n\u2713 Dispensed: " + rx.getPrescriptionId() + " — " +
                    String.join(", ", rx.getMedications()) + "\n");
            } else {
                queueDisplay.append("\nNo prescriptions to dispense.\n");
            }
        });

        // Live listener — auto-refresh when a new prescription arrives
        PrescriptionQueue.getInstance().addQueueListener(rx -> {
            refreshQueue.run();
            queueDisplay.append("\n\u26A0 NEW PRESCRIPTION ARRIVED: " + rx.getPrescriptionId() + "\n");
        });

        refreshQueue.run();
        return panel;
    }

    // ======================== THREADING DEMO ========================
    private JPanel buildThreadingDemoPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(UITheme.BG_MAIN);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JPanel card = UITheme.createCard();
        card.setLayout(new BorderLayout(0, 15));
        card.add(UITheme.createSectionTitle("Multi-Threading Demonstration"), BorderLayout.NORTH);

        JTextArea output = new JTextArea();
        output.setFont(UITheme.MONO_FONT);
        output.setBackground(new Color(30, 30, 30));
        output.setForeground(new Color(0, 255, 128));
        output.setEditable(false);
        output.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        btnRow.setOpaque(false);

        RoundedButton swBtn = new RoundedButton("SwingWorker", UITheme.ACCENT);
        RoundedButton poolBtn = new RoundedButton("Thread Pool", UITheme.SUCCESS);
        RoundedButton atomicBtn = new RoundedButton("AtomicInteger", new Color(155, 89, 182));
        RoundedButton queueBtn = new RoundedButton("BlockingQueue", UITheme.WARNING);

        btnRow.add(swBtn); btnRow.add(poolBtn); btnRow.add(atomicBtn); btnRow.add(queueBtn);

        card.add(btnRow, BorderLayout.NORTH);

        JPanel progressPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        progressPanel.setOpaque(false);
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setPreferredSize(new Dimension(400, 25));
        progressPanel.add(new JLabel("Progress: "));
        progressPanel.add(progressBar);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.add(progressPanel, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(output);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        bottomPanel.add(scroll, BorderLayout.CENTER);

        card.add(bottomPanel, BorderLayout.CENTER);
        panel.add(card, BorderLayout.CENTER);

        // SwingWorker Demo
        swBtn.addActionListener(e -> {
            output.setText("=== SWINGWORKER DEMO ===\n\n");
            output.append("[Concept] SwingWorker runs heavy tasks off the EDT.\n");
            output.append("[Concept] publish()/process() safely updates UI during execution.\n\n");
            progressBar.setValue(0);

            SwingWorker<String, Integer> worker = new SwingWorker<>() {
                @Override
                protected String doInBackground() throws Exception {
                    for (int i = 0; i <= 100; i += 10) {
                        Thread.sleep(200);
                        publish(i);
                    }
                    return "RSA-2048 Encryption Complete!";
                }

                @Override
                protected void process(List<Integer> chunks) {
                    int latest = chunks.get(chunks.size() - 1);
                    progressBar.setValue(latest);
                    output.append("  [" + Thread.currentThread().getName() + "] Progress: " + latest + "%\n");
                }

                @Override
                protected void done() {
                    try {
                        output.append("\n  Result: " + get() + "\n");
                        output.append("  [Thread] Back on EDT: " + Thread.currentThread().getName() + "\n");
                    } catch (Exception ex) {
                        output.append("  Error: " + ex.getMessage() + "\n");
                    }
                }
            };
            worker.execute();
        });

        // Thread Pool Demo
        poolBtn.addActionListener(e -> {
            output.setText("=== THREAD POOL DEMO (ExecutorService) ===\n\n");
            output.append("[Concept] Fixed pool of 4 worker threads handles tasks concurrently.\n");
            output.append("[Concept] Tasks are queued and distributed to available workers.\n\n");

            for (int i = 1; i <= 6; i++) {
                final int taskId = i;
                HospitalTaskManager.getInstance().runInBackground(
                    () -> {
                        String threadName = Thread.currentThread().getName();
                        Thread.sleep(500 + (int)(Math.random() * 1000));
                        return "Task-" + taskId + " completed on [" + threadName + "]";
                    },
                    result -> output.append("  \u2713 " + result + "\n"),
                    error -> output.append("  \u2717 Task-" + taskId + " failed: " + error.getMessage() + "\n")
                );
                output.append("  \u279C Submitted Task-" + taskId + " to thread pool\n");
            }
        });

        // AtomicInteger Demo
        atomicBtn.addActionListener(e -> {
            output.setText("=== ATOMICINTEGER DEMO (Thread-Safe Counter) ===\n\n");
            output.append("[Concept] AtomicInteger provides lock-free thread-safe operations.\n");
            output.append("[Concept] Patient.totalPatients uses AtomicInteger.\n\n");

            output.append("  Current patient count: " + Patient.getTotalPatients() + "\n\n");
            output.append("  Creating 5 patients from different threads simultaneously...\n\n");

            for (int i = 1; i <= 5; i++) {
                final int id = i;
                HospitalTaskManager.getInstance().runInBackground(
                    () -> {
                        Patient p = new Patient("Thread-Patient-" + id, "555-" + id, "TP-" + id, 20 + id);
                        return "  [" + Thread.currentThread().getName() + "] Created patient TP-" + id +
                               " | Total now: " + Patient.getTotalPatients();
                    },
                    result -> output.append(result + "\n"),
                    error -> output.append("  Error: " + error.getMessage() + "\n")
                );
            }
        });

        // BlockingQueue Demo
        queueBtn.addActionListener(e -> {
            output.setText("=== BLOCKINGQUEUE DEMO (Producer-Consumer) ===\n\n");
            output.append("[Concept] Doctor (producer) adds prescriptions to queue.\n");
            output.append("[Concept] Pharmacist (consumer) retrieves them.\n");
            output.append("[Concept] LinkedBlockingQueue handles all synchronization.\n\n");

            output.append("  Producing 3 prescriptions...\n");
            for (int i = 1; i <= 3; i++) {
                Prescription rx = new Prescription("DEMO-RX-" + i, "PAT-00" + i, "DR-DEMO",
                    java.util.List.of("Med-A-" + i, "Med-B-" + i), "As needed", java.time.LocalDate.now().toString());
                PrescriptionQueue.getInstance().addPrescription(rx);
                output.append("  \u279C Produced: " + rx.getPrescriptionId() + "\n");
            }

            output.append("\n  Queue size: " + PrescriptionQueue.getInstance().getPendingCount() + "\n");
            output.append("\n  Consuming all...\n");

            Prescription rx;
            while ((rx = PrescriptionQueue.getInstance().pollPrescription()) != null) {
                output.append("  \u2713 Consumed: " + rx.getPrescriptionId() + " — " +
                    String.join(", ", rx.getMedications()) + "\n");
            }

            output.append("\n  Queue size after consumption: " + PrescriptionQueue.getInstance().getPendingCount() + "\n");
        });

        return panel;
    }

    // ======================== OOP DEMO (Admin) ========================
    private JPanel buildOOPDemoPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(UITheme.BG_MAIN);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JPanel card = UITheme.createCard();
        card.setLayout(new BorderLayout(0, 10));
        card.add(UITheme.createSectionTitle("Interactive OOP Concept Demos"), BorderLayout.NORTH);

        JTextArea output = new JTextArea();
        output.setFont(UITheme.MONO_FONT);
        output.setBackground(new Color(30, 30, 30));
        output.setForeground(new Color(0, 255, 128));
        output.setEditable(false);
        output.setLineWrap(true);
        output.setWrapStyleWord(true);
        output.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        btnPanel.setOpaque(false);

        String[][] demos = {
            {"Polymorphism", "ACCENT"}, {"equals/hashCode", "SUCCESS"},
            {"Immutable", "WARNING"}, {"Composition", "DANGER"},
            {"Singleton", "ACCENT"}, {"Exceptions", "SUCCESS"}
        };

        Color[] colors = {UITheme.ACCENT, UITheme.SUCCESS, UITheme.WARNING, UITheme.DANGER,
                          new Color(155, 89, 182), new Color(26, 188, 156)};

        for (int i = 0; i < demos.length; i++) {
            RoundedButton btn = new RoundedButton(demos[i][0], colors[i]);
            btn.setPreferredSize(new Dimension(130, 36));
            btnPanel.add(btn);

            final int idx = i;
            btn.addActionListener(e -> runOOPDemo(output, idx));
        }

        card.add(btnPanel, BorderLayout.NORTH);
        JScrollPane scroll = new JScrollPane(output);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        card.add(scroll, BorderLayout.CENTER);

        panel.add(card, BorderLayout.CENTER);
        return panel;
    }

    private void runOOPDemo(JTextArea output, int demoIndex) {
        switch (demoIndex) {
            case 0: // Polymorphism
                output.setText("=== ABSTRACTION & POLYMORPHISM ===\n\n");
                output.append("Person is ABSTRACT — cannot do 'new Person()'.\nEach subclass overrides getRole() differently.\n\n");
                Person[] people = {
                    new Doctor("Dr. House", "9876543210", "D001", "Diagnostics", 500.0),
                    new Receptionist("Jane", "1234567890", "S001", "Front Desk", 1),
                    new Pharmacist("Bob", "5555555555", "S002", "Pharmacy", "PH-100"),
                    new LabTechnician("Alice", "4444444444", "S003", "Pathology", "Hematology")
                };
                output.append("Dynamic dispatch via Person reference:\n\n");
                for (Person p : people) output.append("  " + p.toString() + "\n");
                output.append("\n4 different behaviors from same method call.\n");
                break;

            case 1: // equals/hashCode
                output.setText("=== OBJECT EQUALITY (equals & hashCode) ===\n\n");
                Patient p1 = new Patient("Krish", "123", "PAT-001", 20);
                Patient p2 = new Patient("Krish Patel", "456", "PAT-001", 21);
                Patient p3 = new Patient("Ravi", "789", "PAT-002", 25);
                output.append("p1 = Patient(id='PAT-001'), p2 = Patient(id='PAT-001'), p3 = Patient(id='PAT-002')\n\n");
                output.append("p1 == p2       : " + (p1 == p2) + " (reference)\n");
                output.append("p1.equals(p2)  : " + p1.equals(p2) + "  ← same ID!\n");
                output.append("p1.equals(p3)  : " + p1.equals(p3) + "\n\n");
                output.append("p1.hashCode()  = " + p1.hashCode() + "\n");
                output.append("p2.hashCode()  = " + p2.hashCode() + "  ← matches!\n");
                output.append("p3.hashCode()  = " + p3.hashCode() + "\n");
                break;

            case 2: // Immutable
                output.setText("=== IMMUTABLE CLASS (Prescription) ===\n\n");
                Prescription rx = new Prescription("RX-5001", "PAT-001", "D001",
                    List.of("Paracetamol", "Amoxicillin"), "500mg", "2026-10-13");
                output.append("Created: " + rx + "\n\nTrying to modify medications list...\n");
                try { rx.getMedications().add("Ibuprofen"); output.append("BUG!\n"); }
                catch (UnsupportedOperationException ex) {
                    output.append("UnsupportedOperationException! List is unmodifiable.\n\n");
                }
                output.append("Rules: final class, final fields, no setters, defensive copy.\n");
                break;

            case 3: // Composition
                output.setText("=== COMPOSITION (has-a) ===\n\n");
                Patient patient = new Patient("Krish", "9876543210", "PAT-001", 20);
                Doctor doctor = new Doctor("Dr. House", "1234567890", "D001", "Diagnostics", 500.0);
                Appointment apt = new Appointment("APT-001", patient, doctor, "2026-10-14 10:00");
                output.append("Appointment HAS-A Patient and HAS-A Doctor:\n\n  " + apt + "\n\n");
                apt.updateStatus("Completed");
                output.append("After update: " + apt + "\n");
                break;

            case 4: // Singleton
                output.setText("=== SINGLETON PATTERN (DatabaseManager) ===\n\n");
                DatabaseManager db1 = DatabaseManager.getInstance();
                DatabaseManager db2 = DatabaseManager.getInstance();
                output.append("getInstance() called twice:\n");
                output.append("  Hash 1: " + System.identityHashCode(db1) + "\n");
                output.append("  Hash 2: " + System.identityHashCode(db2) + "\n");
                output.append("  Same?   " + (db1 == db2) + "\n\n");
                output.append("Bill Pugh Holder Idiom — lazy, thread-safe.\n");
                break;

            case 5: // Exceptions
                output.setText("=== CUSTOM EXCEPTION HIERARCHY ===\n\n");
                output.append("Exception → HospitalException → PatientNotFound / AuthFailed / InvalidRx\n\n");
                try { DatabaseManager.getInstance().fetchAndDecryptPatient("GHOST-999"); }
                catch (PatientNotFoundException ex) {
                    output.append("Caught PatientNotFoundException:\n  " + ex.getMessage() + "\n\n");
                } catch (HospitalException ex) { output.append(ex.getMessage() + "\n\n"); }
                try { AuthManager.authenticate("hacker", "pass"); }
                catch (AuthenticationFailedException ex) {
                    output.append("Caught AuthenticationFailedException:\n  " + ex.getMessage() + "\n");
                }
                break;
        }
    }
}
