import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Manages staff authentication with SHA-256 hashed passwords.
 * Uses DatabaseManager Singleton for all DB connections.
 * Throws AuthenticationFailedException (custom exception) on login failure.
 */
public class AuthManager {

    // Uses Singleton DatabaseManager (no direct DriverManager calls)
    private static final DatabaseManager db = DatabaseManager.getInstance();

    public static void setupStaffTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS Staff ("
                + "username TEXT PRIMARY KEY, "
                + "password_hash TEXT, "
                + "role TEXT)";

        boolean needsSeeding = false;

        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS count FROM Staff")) {
                if (rs.next() && rs.getInt("count") == 0) {
                    needsSeeding = true;
                }
            }
        } catch (SQLException e) {
            System.out.println("Auth DB Error: " + e.getMessage());
        }

        if (needsSeeding) {
            registerStaff("admin", "admin123", "Admin");
            registerStaff("dr_house", "doc123", "Doctor");
            registerStaff("tech_lisa", "tech123", "Pharmacist");
            System.out.println("[Security] Default SHA-256 hashed staff accounts generated.");
        }
    }

    public static void registerStaff(String username, String password, String role) {
        String insertSQL = "INSERT OR REPLACE INTO Staff(username, password_hash, role) VALUES(?,?,?)";
        try (Connection conn = db.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setString(1, username);
            pstmt.setString(2, hashPassword(password));
            pstmt.setString(3, role);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Authenticates a staff member. Returns their role on success.
     * Throws AuthenticationFailedException on failure (custom exception, checklist #11).
     */
    public static String authenticate(String username, String password) throws AuthenticationFailedException {
        String query = "SELECT role FROM Staff WHERE username = ? AND password_hash = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, hashPassword(password));
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("role");
                }
            }
        } catch (SQLException e) {
            throw new AuthenticationFailedException(username);
        }
        throw new AuthenticationFailedException(username);
    }

    private static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                String h = Integer.toHexString(0xff & b);
                if (h.length() == 1) hex.append('0');
                hex.append(h);
            }
            return hex.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}