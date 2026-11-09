import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * SINGLETON PATTERN — evaluation checklist #10.
 *
 * Implementation: Bill Pugh Holder Idiom (thread-safe lazy initialization).
 * - The inner static Holder class is not loaded until getInstance() is called.
 * - The JVM guarantees that static initialization is thread-safe.
 *
 * Drawback (for viva): Tight coupling — all classes depend on a single global
 * instance. This makes unit testing difficult because you cannot easily replace
 * the DatabaseManager with a mock object without using a DI framework.
 */
public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:hospital.db";

    // Private constructor — prevents external instantiation (Singleton rule)
    private DatabaseManager() {}

    // Thread-safe lazy Singleton using Bill Pugh Holder Idiom
    private static class Holder {
        private static final DatabaseManager INSTANCE = new DatabaseManager();
    }

    public static DatabaseManager getInstance() {
        return Holder.INSTANCE;
    }

    // Provides a connection — callers must close it via try-with-resources
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    /**
     * Encrypts and saves a patient record to SQLite.
     * Uses a SINGLE connection for both table creation and insert (optimised).
     * Throws HospitalException instead of silently printing errors.
     */
    public void saveSecurePatient(Patient p, String bloodGroup) throws HospitalException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS SecurePatients ("
                + "id TEXT PRIMARY KEY, "
                + "enc_name TEXT, "
                + "enc_age TEXT, "
                + "enc_contact TEXT, "
                + "enc_blood_group TEXT)";

        String insertSQL = "INSERT OR REPLACE INTO SecurePatients"
                + "(id, enc_name, enc_age, enc_contact, enc_blood_group) VALUES(?,?,?,?,?)";

        // Single connection for both operations (optimised from original 2-connection approach)
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);

            try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
                pstmt.setString(1, p.getPatientId());
                pstmt.setString(2, SecurityHelper.encrypt(p.getName()));
                pstmt.setString(3, SecurityHelper.encrypt(String.valueOf(p.getAge())));
                pstmt.setString(4, SecurityHelper.encrypt(p.getContactNumber()));
                pstmt.setString(5, SecurityHelper.encrypt(bloodGroup));
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new HospitalException("Database error while saving patient: " + e.getMessage(), e);
        }
    }

    /**
     * Fetches and decrypts a patient record by ID.
     * Throws PatientNotFoundException (custom exception) instead of returning null.
     */
    public String[] fetchAndDecryptPatient(String patientId) throws PatientNotFoundException, HospitalException {
        String query = "SELECT * FROM SecurePatients WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, patientId);

            // ResultSet in try-with-resources (resource leak fix)
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new String[]{
                        SecurityHelper.decrypt(rs.getString("enc_name")),
                        SecurityHelper.decrypt(rs.getString("enc_age")),
                        SecurityHelper.decrypt(rs.getString("enc_contact")),
                        SecurityHelper.decrypt(rs.getString("enc_blood_group"))
                    };
                }
            }
        } catch (SQLException e) {
            throw new HospitalException("Database read error: " + e.getMessage(), e);
        }

        // Throws custom exception instead of returning null (checklist #11)
        throw new PatientNotFoundException(patientId);
    }
}