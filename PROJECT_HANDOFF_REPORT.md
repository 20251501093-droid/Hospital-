# Hospital Management System (SWIG & Java OOP) — Complete Project Handoff Report

> **Target Audience:** AI Models / Engineers picking up this codebase.  
> **Repository Path:** `/Users/krishpatel/Desktop/sem-3/HospitalManagementSystem_SWIG copy`  
> **Java Version:** Java 17+ (tested on Java 21)  
> **Native Toolchain:** C++17, CMake 3.18+, SWIG 4.x, macOS (`.dylib`)  
> **Database:** SQLite (`hospital.db` via `sqlite-jdbc-3.42.0.0.jar`)  
> **Security:** RSA-2048 keypair (`public.key`, `private.key`), SHA-256 password hashing

---

## 1. Executive Summary & Architecture

This project is a hybrid **Java Swing GUI + C++ Native Bridge (SWIG) + SQLite + Cryptography** Hospital Management System designed for an academic Object-Oriented Programming (OOP) evaluation checklist (Steps 1–4 completed; Steps 5–8 ready to implement).

### Core Capabilities:
1. **Role-Based Authentication (Swing UI):**
   - Hashed credentials stored in SQLite `Staff` table.
   - Roles: `Admin`, `Doctor`, `Pharmacist`.
2. **Encrypted Medical Records:**
   - Admin registers patients; records are RSA-2048 encrypted into SQLite `SecurePatients` table.
   - Doctors search by patient ID and decrypt records on-the-fly using the RSA Private Key.
3. **C++ Native Discharge Billing (via SWIG):**
   - Calls `MedicalBill.cpp` / `BillingBridge` shared library (`libBillingBridge.dylib`).
   - Demonstrates C++ Object Slicing, Virtual Base Classes (Diamond Problem), and method chaining.
4. **Interactive OOP Concept Showcase:**
   - Admin tab includes live interactive demos for Polymorphism, Object Equality (`equals`/`hashCode`), Immutability, Composition, Singleton Pattern, and Custom Exceptions.

---

## 2. Evaluation Checklist Mapping (Steps 1–4 Completed)

| Evaluation Concept | Implemented In | Demonstration / Details |
| :--- | :--- | :--- |
| **#1 Encapsulation** | `Person.java`, `Doctor.java`, `Patient.java` | Private attributes exposed strictly via public getters/setters. |
| **#2 Abstraction** | `Person.java`, `HospitalStaff.java` | `abstract class Person` with `abstract String getRole()`; cannot be instantiated directly. `HospitalStaff` is an abstract intermediate class. |
| **#3 Inheritance (Hierarchical & Multilevel)** | `Patient`, `Doctor`, `HospitalStaff` -> `Receptionist`, `Pharmacist`, `LabTechnician` | `Person` -> `Doctor` (Hierarchical); `Person` -> `HospitalStaff` -> `Receptionist` / `Pharmacist` / `LabTechnician` (3-level Multilevel). |
| **#4 Polymorphism (Dynamic Method Dispatch)** | `getRole()` & `toString()` across all subclasses | Overridden in `Doctor`, `Patient`, `Receptionist`, `Pharmacist`, `LabTechnician`. Tested via `Person[]` array in UI. |
| **#5 Composition ("Has-A" Relationship)** | `Appointment.java` | `Appointment` HAS-A `Patient` and HAS-A `Doctor`. Validated using `Objects.requireNonNull()`. |
| **#6 Static Members & Final Constants** | `Patient.java` | `static int totalPatients` (shared counter across instances), `public static final String HOSPITAL_BRANCH`. |
| **#7 Immutable Class** | `Prescription.java` | `final class`, all `final` fields, no setters, defensive copy of collections via `Collections.unmodifiableList(List.copyOf(...))`. |
| **#8 Object Equality (`equals` & `hashCode`)** | `Patient.java`, `Prescription.java` | Overridden to compare by domain ID (`patientId`, `prescriptionId`), maintaining the strict hashCode contract. |
| **#9 Nested Inner Class** | `Patient.java` (`MedicalRecord`) | Non-static inner class having direct access to enclosing outer instance via `Patient.this.getName()`. |
| **#10 Singleton Pattern** | `DatabaseManager.java` | Implemented via thread-safe lazy-loaded **Bill Pugh Holder Idiom**. Private constructor prevents external instantiation. |
| **#11 Custom Exception Hierarchy** | `HospitalException.java` and derivatives | Root checked `HospitalException` with specific subclasses (`PatientNotFoundException`, `AuthenticationFailedException`, `InvalidPrescriptionException`). |

---

## 3. Directory Layout & Status of Files

```
HospitalManagementSystem_SWIG copy/
├── Domain Model (Steps 1-4)
│   ├── Person.java                    [MODIFIED: Abstract base class, private fields, abstract getRole()]
│   ├── Patient.java                   [MODIFIED: Extends Person, equals/hashCode, static final, nested class]
│   ├── Doctor.java                    [MODIFIED: Extends Person, getRole(), getters, constructor chaining]
│   ├── HospitalStaff.java             [MODIFIED: Abstract intermediate class, multilevel inheritance]
│   ├── Receptionist.java              [MODIFIED: Extends HospitalStaff, overrides getRole()]
│   ├── Pharmacist.java                [MODIFIED: Extends HospitalStaff, overrides getRole()]
│   └── LabTechnician.java              [MODIFIED: Extends HospitalStaff, overrides getRole()]
├── Features & Exceptions
│   ├── Appointment.java               [MODIFIED: Composition (has-a Patient, has-a Doctor)]
│   ├── Prescription.java              [MODIFIED: Immutable class, unmodifiableList, equals/hashCode]
│   ├── HospitalException.java         [NEW: Custom base checked exception]
│   ├── PatientNotFoundException.java  [NEW: Custom exception with patientId diagnostic data]
│   ├── AuthenticationFailedException.java [NEW: Custom exception for auth failures]
│   └── InvalidPrescriptionException.java [NEW: Custom exception for prescription validation]
├── Infrastructure & Security
│   ├── DatabaseManager.java           [MODIFIED: Converted to Bill Pugh Singleton, try-with-resources, custom exceptions]
│   ├── SecurityHelper.java            [MODIFIED: RSA-2048 key management, try-with-resources stream leak fixes]
│   └── AuthManager.java               [MODIFIED: Uses DatabaseManager Singleton, throws AuthenticationFailedException]
├── Presentation (Swing UI)
│   ├── LoginUI.java                   [MODIFIED: Catching AuthenticationFailedException, memory wipe on password]
│   └── HospitalUI.java                [MODIFIED: Role tabs, interactive OOP concept demo tab, custom exception UI]
├── C++ SWIG Bridge & Build
│   ├── CMakeLists.txt                 [MODIFIED: Linked MedicalBill.i/cpp instead of legacy RobotController]
│   ├── MedicalBill.h                  [EXISTING: C++ Header with Diamond Inheritance & Slicing classes]
│   ├── MedicalBill.cpp                [EXISTING: C++ Implementation]
│   ├── MedicalBill.i                  [EXISTING: SWIG Interface file]
│   ├── BillingBridge.java             [EXISTING: SWIG generated Java wrapper]
│   ├── BillingBridgeJNI.java          [EXISTING: SWIG generated JNI binding]
│   ├── libBillingBridge.dylib         [EXISTING: Compiled native macOS shared library]
│   └── TerminalRunner.java            [EXISTING: Terminal tester for SWIG bridge]
└── Libraries & Storage
    ├── sqlite-jdbc-3.42.0.0.jar       [SQLite JDBC Driver]
    ├── hospital.db                    [SQLite Database file]
    ├── public.key                     [Generated RSA Public Key]
    └── private.key                    [Generated RSA Private Key]
```

---

## 4. Full Source Code of All Created & Modified Files

### 4.1 `Person.java`
```java
import java.util.Objects;

/**
 * ABSTRACT BASE CLASS — demonstrates Abstraction (evaluation checklist #2).
 * Cannot be instantiated directly. Forces subclasses to implement getRole().
 *
 * OOP Concepts demonstrated:
 * - Abstraction: class is abstract, getRole() is abstract
 * - Encapsulation: private fields with public getters
 * - Polymorphism: getRole() is overridden in every subclass
 */
public abstract class Person {
    // Encapsulation: fields are PRIVATE (not protected), accessed via getters only
    private String name;
    private String contactNumber;

    // Default constructor — used by subclass default constructors
    protected Person() {
        this.name = "Unknown";
        this.contactNumber = "000-0000";
    }

    // Parameterized constructor — called via super() in subclasses
    protected Person(String name, String contactNumber) {
        this.name = name;
        this.contactNumber = contactNumber;
    }

    // Encapsulation: public getters for private fields
    public String getName() { return name; }
    public String getContactNumber() { return contactNumber; }

    /**
     * ABSTRACT METHOD — each subclass MUST provide its own implementation.
     * This is the heart of runtime polymorphism in this hierarchy.
     */
    public abstract String getRole();

    @Override
    public String toString() {
        return getRole() + ": " + name + " (" + contactNumber + ")";
    }
}
```

---

### 4.2 `Patient.java`
```java
import java.util.Objects;

/**
 * PATIENT — extends Person (Inheritance, checklist #3).
 *
 * OOP Concepts demonstrated:
 * - Inheritance: Patient IS-A Person
 * - Static final constant: HOSPITAL_BRANCH (accessed without object creation)
 * - Static variable: totalPatients (shared counter across all instances)
 * - Nested inner class: MedicalRecord
 * - equals/hashCode override: value equality by patientId (checklist #8)
 * - Polymorphism: overrides getRole() from abstract Person
 */
public class Patient extends Person {
    private final String patientId;
    private final int age;

    // Static final constant — accessed as Patient.HOSPITAL_BRANCH (no object needed)
    public static final String HOSPITAL_BRANCH = "IIIT Vadodara Main Campus";

    // Static variable — shared across ALL Patient instances
    private static int totalPatients = 0;

    // Main constructor — calls super() for Person fields
    public Patient(String name, String contactNumber, String patientId, int age) {
        super(name, contactNumber);
        this.patientId = patientId;
        this.age = age;
        totalPatients++;
    }

    // Getters (Encapsulation)
    public String getPatientId() { return patientId; }
    public int getAge() { return age; }

    // Static method — can be called without an object: Patient.getTotalPatients()
    public static int getTotalPatients() { return totalPatients; }

    // Polymorphism: concrete implementation of abstract getRole()
    @Override
    public String getRole() { return "Patient"; }

    // ============ equals/hashCode override (checklist #8) ============
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Patient patient = (Patient) o;
        return Objects.equals(patientId, patient.patientId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(patientId);
    }

    // ============ Nested Inner Class (checklist concept) ============
    public class MedicalRecord {
        private final String bloodGroup;

        public MedicalRecord(String bloodGroup) {
            this.bloodGroup = bloodGroup;
        }

        public String getBloodGroup() { return bloodGroup; }

        // Accesses enclosing Patient's name via Person getter
        public String getPatientName() {
            return Patient.this.getName();
        }
    }
}
```

---

### 4.3 `Doctor.java`
```java
/**
 * DOCTOR — extends Person (Inheritance, checklist #3).
 *
 * OOP Concepts demonstrated:
 * - Inheritance: Doctor IS-A Person
 * - Constructor chaining: calls super(name, contactNumber)
 * - Polymorphism: overrides getRole()
 * - Encapsulation: private fields with public getters
 */
public class Doctor extends Person {
    private String doctorId;
    private String specialization;
    private double consultationFee;

    // Default constructor — constructor chaining via super()
    public Doctor() {
        super();
        this.doctorId = "Unassigned";
        this.specialization = "General";
        this.consultationFee = 0.0;
    }

    // Parameterized constructor
    public Doctor(String name, String contactNumber, String doctorId, String specialization, double consultationFee) {
        super(name, contactNumber);
        this.doctorId = doctorId;
        this.specialization = specialization;
        this.consultationFee = consultationFee;
    }

    // Getters (Encapsulation)
    public String getDoctorId() { return doctorId; }
    public String getSpecialization() { return specialization; }
    public double getConsultationFee() { return consultationFee; }

    // Polymorphism: concrete implementation of abstract getRole()
    @Override
    public String getRole() { return "Doctor"; }
}
```

---

### 4.4 `HospitalStaff.java`
```java
/**
 * HOSPITAL STAFF — abstract intermediate class (Multilevel Inheritance).
 * Person (abstract) -> HospitalStaff (abstract) -> Receptionist / Pharmacist / LabTechnician
 *
 * OOP Concepts demonstrated:
 * - Multilevel Inheritance: HospitalStaff IS-A Person
 * - Abstraction: class is abstract, getRole() is still abstract (delegated to children)
 */
public abstract class HospitalStaff extends Person {
    protected String staffId;
    protected String department;

    // Default constructor
    protected HospitalStaff() {
        super();
        this.staffId = "Unassigned";
        this.department = "General";
    }

    // Parameterized constructor
    protected HospitalStaff(String name, String contactNumber, String staffId, String department) {
        super(name, contactNumber);
        this.staffId = staffId;
        this.department = department;
    }

    // Getters (Encapsulation)
    public String getStaffId() { return staffId; }
    public String getDepartment() { return department; }
}
```

---

### 4.5 `Receptionist.java`
```java
/**
 * RECEPTIONIST — concrete subclass of HospitalStaff.
 * Person -> HospitalStaff -> Receptionist (3 levels of inheritance)
 */
public class Receptionist extends HospitalStaff {
    private int deskNumber;

    public Receptionist() {
        super();
        this.deskNumber = 0;
    }

    public Receptionist(String name, String contactNumber, String staffId, String department, int deskNumber) {
        super(name, contactNumber, staffId, department);
        this.deskNumber = deskNumber;
    }

    public int getDeskNumber() { return deskNumber; }

    // Polymorphism: concrete getRole() implementation
    @Override
    public String getRole() { return "Receptionist"; }
}
```

---

### 4.6 `Pharmacist.java`
```java
/**
 * PHARMACIST — concrete subclass of HospitalStaff.
 * Person -> HospitalStaff -> Pharmacist (3 levels of inheritance)
 */
public class Pharmacist extends HospitalStaff {
    private String licenseNumber;

    public Pharmacist() {
        super();
        this.licenseNumber = "Unknown";
    }

    public Pharmacist(String name, String contactNumber, String staffId, String department, String licenseNumber) {
        super(name, contactNumber, staffId, department);
        this.licenseNumber = licenseNumber;
    }

    public String getLicenseNumber() { return licenseNumber; }

    // Polymorphism: concrete getRole() implementation
    @Override
    public String getRole() { return "Pharmacist"; }
}
```

---

### 4.7 `LabTechnician.java`
```java
/**
 * LAB TECHNICIAN — concrete subclass of HospitalStaff.
 * Person -> HospitalStaff -> LabTechnician (3 levels of inheritance)
 */
public class LabTechnician extends HospitalStaff {
    private String labSpecialty;

    public LabTechnician() {
        super();
        this.labSpecialty = "General";
    }

    public LabTechnician(String name, String contactNumber, String staffId, String department, String labSpecialty) {
        super(name, contactNumber, staffId, department);
        this.labSpecialty = labSpecialty;
    }

    public String getLabSpecialty() { return labSpecialty; }

    // Polymorphism: concrete getRole() implementation
    @Override
    public String getRole() { return "Lab Technician"; }
}
```

---

### 4.8 `Appointment.java`
```java
import java.util.Objects;

/**
 * APPOINTMENT — demonstrates Composition (checklist #5).
 *
 * An Appointment HAS-A Patient and HAS-A Doctor.
 * This is a "has-a" relationship (composition), NOT "is-a" (inheritance).
 *
 * OOP Concepts demonstrated:
 * - Composition: owns references to Patient and Doctor
 * - Objects.requireNonNull: enforces non-null parts at construction time
 */
public class Appointment {
    private final String appointmentId;
    private final Patient patient;    // Composition: HAS-A Patient
    private final Doctor doctor;      // Composition: HAS-A Doctor
    private final String dateTime;
    private String status;

    public Appointment(String appointmentId, Patient patient, Doctor doctor, String dateTime) {
        this.appointmentId = Objects.requireNonNull(appointmentId, "Appointment ID cannot be null");
        this.patient = Objects.requireNonNull(patient, "Patient cannot be null");
        this.doctor = Objects.requireNonNull(doctor, "Doctor cannot be null");
        this.dateTime = Objects.requireNonNull(dateTime, "DateTime cannot be null");
        this.status = "Scheduled";
    }

    // Getters
    public String getAppointmentId() { return appointmentId; }
    public Patient getPatient() { return patient; }
    public Doctor getDoctor() { return doctor; }
    public String getDateTime() { return dateTime; }
    public String getStatus() { return status; }

    public void updateStatus(String newStatus) {
        this.status = newStatus;
    }

    @Override
    public String toString() {
        return "Appointment " + appointmentId + " | " + patient.getName()
                + " with Dr. " + doctor.getName() + " at " + dateTime + " [" + status + "]";
    }
}
```

---

### 4.9 `Prescription.java`
```java
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * IMMUTABLE CLASS — demonstrates evaluation checklist #7.
 *
 * Rules followed for immutability:
 * 1. Class is declared 'final' — cannot be subclassed
 * 2. All fields are 'final' — set once in constructor, never changed
 * 3. No setter methods exist
 * 4. Mutable collections (List) are defensively copied and wrapped in
 *    Collections.unmodifiableList() to prevent external modification
 * 5. Thread-safe by design — no synchronization needed
 */
public final class Prescription {
    private final String prescriptionId;
    private final String patientId;
    private final String doctorId;
    private final List<String> medications;  // Defensively copied & unmodifiable
    private final String dosage;
    private final String dateIssued;

    public Prescription(String prescriptionId, String patientId, String doctorId,
                        List<String> medications, String dosage, String dateIssued) {
        this.prescriptionId = Objects.requireNonNull(prescriptionId, "Prescription ID required");
        this.patientId = Objects.requireNonNull(patientId, "Patient ID required");
        this.doctorId = Objects.requireNonNull(doctorId, "Doctor ID required");
        // Defensive copy: prevents external list modification from affecting this object
        this.medications = Collections.unmodifiableList(List.copyOf(medications));
        this.dosage = Objects.requireNonNull(dosage, "Dosage required");
        this.dateIssued = Objects.requireNonNull(dateIssued, "Date required");
    }

    // Only getters — NO setters (immutability rule #3)
    public String getPrescriptionId() { return prescriptionId; }
    public String getPatientId() { return patientId; }
    public String getDoctorId() { return doctorId; }
    public List<String> getMedications() { return medications; }  // Already unmodifiable
    public String getDosage() { return dosage; }
    public String getDateIssued() { return dateIssued; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Prescription that = (Prescription) o;
        return Objects.equals(prescriptionId, that.prescriptionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(prescriptionId);
    }

    @Override
    public String toString() {
        return "Rx#" + prescriptionId + " | Patient: " + patientId
                + " | Meds: " + String.join(", ", medications)
                + " | Dosage: " + dosage + " | Date: " + dateIssued;
    }
}
```

---

### 4.10 `HospitalException.java`
```java
/**
 * CUSTOM EXCEPTION BASE CLASS — evaluation checklist #11.
 * All hospital-specific exceptions extend this class.
 *
 * Exception hierarchy:
 *   Exception
 *     └── HospitalException (this class)
 *           ├── PatientNotFoundException
 *           ├── AuthenticationFailedException
 *           └── InvalidPrescriptionException
 */
public class HospitalException extends Exception {
    public HospitalException(String message) {
        super(message);
    }

    public HospitalException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

---

### 4.11 `PatientNotFoundException.java`
```java
/**
 * Thrown when a patient lookup by ID fails.
 * Carries the searched patientId for diagnostic purposes.
 */
public class PatientNotFoundException extends HospitalException {
    private final String patientId;

    public PatientNotFoundException(String patientId) {
        super("Patient with ID '" + patientId + "' not found in the secure database.");
        this.patientId = patientId;
    }

    public String getPatientId() { return patientId; }
}
```

---

### 4.12 `AuthenticationFailedException.java`
```java
/**
 * Thrown when login credentials do not match any staff record.
 */
public class AuthenticationFailedException extends HospitalException {
    public AuthenticationFailedException(String username) {
        super("Authentication failed for user: '" + username + "'. Invalid credentials.");
    }
}
```

---

### 4.13 `InvalidPrescriptionException.java`
```java
/**
 * Thrown when a prescription cannot be created due to validation errors.
 */
public class InvalidPrescriptionException extends HospitalException {
    public InvalidPrescriptionException(String reason) {
        super("Invalid prescription: " + reason);
    }
}
```

---

### 4.14 `DatabaseManager.java`
```java
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
```

---

### 4.15 `SecurityHelper.java`
```java
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;

/**
 * RSA-2048 Encryption Helper.
 *
 * Fixed from original:
 * - FileInputStream now properly closed via try-with-resources (resource leak fix)
 * - Uses StandardCharsets.UTF_8 for portable encoding
 * - Static utility class (all methods static) — consistent with original design
 */
public class SecurityHelper {
    private static final String ALGORITHM = "RSA";
    private static final String PUBLIC_KEY_FILE = "public.key";
    private static final String PRIVATE_KEY_FILE = "private.key";

    private static PublicKey publicKey;
    private static PrivateKey privateKey;

    // Static initializer — executes once when the class is first loaded
    static {
        try {
            if (new File(PUBLIC_KEY_FILE).exists() && new File(PRIVATE_KEY_FILE).exists()) {
                loadKeys();
            } else {
                generateAndSaveKeys();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize RSA security: " + e.getMessage(), e);
        }
    }

    private static void generateAndSaveKeys() throws Exception {
        System.out.println("[Security] First boot detected. Generating RSA-2048 key pair...");
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
        keyGen.initialize(2048);
        KeyPair pair = keyGen.generateKeyPair();
        publicKey = pair.getPublic();
        privateKey = pair.getPrivate();

        try (FileOutputStream fos = new FileOutputStream(PUBLIC_KEY_FILE)) {
            fos.write(publicKey.getEncoded());
        }
        try (FileOutputStream fos = new FileOutputStream(PRIVATE_KEY_FILE)) {
            fos.write(privateKey.getEncoded());
        }
        System.out.println("[Security] RSA keys saved successfully.\n");
    }

    // FIXED: FileInputStream properly closed with try-with-resources
    private static void loadKeys() throws Exception {
        byte[] pubKeyBytes;
        try (FileInputStream fis = new FileInputStream(PUBLIC_KEY_FILE)) {
            pubKeyBytes = fis.readAllBytes();
        }
        KeyFactory kf = KeyFactory.getInstance(ALGORITHM);
        publicKey = kf.generatePublic(new X509EncodedKeySpec(pubKeyBytes));

        byte[] privKeyBytes;
        try (FileInputStream fis = new FileInputStream(PRIVATE_KEY_FILE)) {
            privKeyBytes = fis.readAllBytes();
        }
        privateKey = kf.generatePrivate(new PKCS8EncodedKeySpec(privKeyBytes));
    }

    // FIXED: Uses StandardCharsets.UTF_8 for portable encoding
    public static String encrypt(String data) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed: " + e.getMessage(), e);
        }
    }

    public static String decrypt(String encryptedData) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
            return new String(cipher.doFinal(decodedBytes), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed: " + e.getMessage(), e);
        }
    }
}
```

---

### 4.16 `AuthManager.java`
```java
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
```

---

### 4.17 `LoginUI.java`
```java
import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

/**
 * ENTRY POINT — Secure staff login window.
 *
 * OOP Concepts demonstrated:
 * - Custom exception handling: catches AuthenticationFailedException
 * - Security: password char array cleared after use (finally block)
 */
public class LoginUI {

    public static void main(String[] args) {
        AuthManager.setupStaffTable();
        SwingUtilities.invokeLater(() -> new LoginUI().createAndShowGUI());
    }

    public void createAndShowGUI() {
        JFrame frame = new JFrame("IIIT Vadodara - Secure Staff Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 250);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JLabel title = new JLabel("Hospital Staff Portal", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(title);

        JPanel userPanel = new JPanel(new BorderLayout(10, 10));
        userPanel.add(new JLabel("Username:"), BorderLayout.WEST);
        JTextField userField = new JTextField();
        userPanel.add(userField, BorderLayout.CENTER);
        panel.add(userPanel);

        JPanel passPanel = new JPanel(new BorderLayout(10, 10));
        passPanel.add(new JLabel("Password:"), BorderLayout.WEST);
        JPasswordField passField = new JPasswordField();
        passPanel.add(passField, BorderLayout.CENTER);
        panel.add(passPanel);

        JButton loginBtn = new JButton("Authenticate");
        loginBtn.setBackground(new Color(0, 102, 204));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setOpaque(true);
        loginBtn.setBorderPainted(false);
        panel.add(loginBtn);

        frame.add(panel);
        frame.setVisible(true);

        loginBtn.addActionListener(e -> {
            String username = userField.getText();
            char[] passwordChars = passField.getPassword();
            String password = new String(passwordChars);

            try {
                // authenticate() throws AuthenticationFailedException on failure
                String role = AuthManager.authenticate(username, password);
                JOptionPane.showMessageDialog(frame, "Access Granted. Logged in as: " + role,
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                frame.dispose();
                SwingUtilities.invokeLater(() -> new HospitalUI(role).createAndShowGUI());

            } catch (AuthenticationFailedException ex) {
                // Custom exception caught and displayed to user
                JOptionPane.showMessageDialog(frame, ex.getMessage(),
                        "Access Denied", JOptionPane.ERROR_MESSAGE);
            } finally {
                // Security: clear password from memory immediately
                Arrays.fill(passwordChars, '\0');
            }
        });
    }
}
```

---

### 4.18 `HospitalUI.java`
```java
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;
import javax.swing.*;

/**
 * MAIN HOSPITAL DASHBOARD — role-based tabbed interface.
 *
 * Admin gets:  Registration, Billing (C++ bridge), OOP Concepts Demo
 * Doctor gets: Medical Records, Prescription Issuing
 * Pharmacist gets: Pharmacy Terminal
 *
 * OOP Concepts demonstrated across all tabs:
 * - Polymorphism, Composition, Immutable class, equals/hashCode,
 *   Singleton pattern, Custom exceptions, Static variables/constants,
 *   Nested classes, Abstraction
 */
public class HospitalUI {

    // Load the C++ SWIG native library
    static {
        System.loadLibrary("BillingBridge");
    }

    private final String currentUserRole;

    public HospitalUI(String role) {
        this.currentUserRole = role;
    }

    public void createAndShowGUI() {
        // Uses static final constant — no dummy Patient object needed
        JFrame frame = new JFrame(Patient.HOSPITAL_BRANCH + " - Portal [" + currentUserRole + "]");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 650);
        frame.setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        if (currentUserRole.equals("Admin")) {
            tabbedPane.addTab("Registration", buildRegistrationPanel());
            tabbedPane.addTab("Discharge Billing", buildBillingPanel());
            tabbedPane.addTab("OOP Concepts Demo", buildOOPDemoPanel());
        } else if (currentUserRole.equals("Doctor")) {
            tabbedPane.addTab("Medical Records", buildDoctorPanel());
            tabbedPane.addTab("Issue Prescription", buildPrescriptionPanel());
        } else if (currentUserRole.equals("Pharmacist")) {
            tabbedPane.addTab("Pharmacy Terminal", buildPharmacyPanel());
        }

        frame.add(tabbedPane);
        frame.setVisible(true);
    }

    // ======================== ADMIN: REGISTRATION PANEL ========================
    private JPanel buildRegistrationPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel statusLabel = new JLabel("Total Registered Patients (Static Counter): " + Patient.getTotalPatients());
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statusLabel.setForeground(new Color(0, 102, 204));
        panel.add(statusLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        JTextField nameField = new JTextField();
        JTextField idField = new JTextField();
        JTextField ageField = new JTextField();
        JTextField contactField = new JTextField();
        JTextField bloodGroupField = new JTextField();

        formPanel.add(new JLabel("Patient Name:")); formPanel.add(nameField);
        formPanel.add(new JLabel("Patient ID (Searchable):")); formPanel.add(idField);
        formPanel.add(new JLabel("Age:")); formPanel.add(ageField);
        formPanel.add(new JLabel("Contact Number:")); formPanel.add(contactField);
        formPanel.add(new JLabel("Blood Group:")); formPanel.add(bloodGroupField);

        JButton submitButton = new JButton("RSA-2048 Encrypt & Save");
        submitButton.setBackground(new Color(34, 139, 34));
        submitButton.setForeground(Color.WHITE);
        submitButton.setOpaque(true);
        submitButton.setBorderPainted(false);
        formPanel.add(new JLabel("")); formPanel.add(submitButton);

        panel.add(formPanel, BorderLayout.CENTER);

        submitButton.addActionListener((ActionEvent e) -> {
            try {
                // Create Patient (extends Person — inheritance)
                Patient newPatient = new Patient(
                    nameField.getText(), contactField.getText(),
                    idField.getText(), Integer.parseInt(ageField.getText())
                );

                // Nested class — MedicalRecord accesses enclosing Patient
                Patient.MedicalRecord record = newPatient.new MedicalRecord(bloodGroupField.getText());

                // Singleton DatabaseManager encrypts and stores the patient
                DatabaseManager.getInstance().saveSecurePatient(newPatient, record.getBloodGroup());

                statusLabel.setText("Total Registered Patients (Static Counter): " + Patient.getTotalPatients());
                JOptionPane.showMessageDialog(panel,
                    "Patient '" + record.getPatientName() + "' encrypted and stored securely.",
                    "RSA Success", JOptionPane.INFORMATION_MESSAGE);

                nameField.setText(""); idField.setText(""); ageField.setText("");
                contactField.setText(""); bloodGroupField.setText("");

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Age must be a valid number.",
                        "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (HospitalException ex) {
                JOptionPane.showMessageDialog(panel, ex.getMessage(),
                        "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    // ======================== ADMIN: BILLING PANEL (C++ SWIG Bridge) ========================
    private JPanel buildBillingPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        JTextField billIdField = new JTextField("B1001");
        JTextField amountField = new JTextField("1500.00");
        JTextField vipLoungeField = new JTextField("Platinum Lounge");

        inputPanel.add(new JLabel("Bill ID:")); inputPanel.add(billIdField);
        inputPanel.add(new JLabel("Medical Base Amount ($):")); inputPanel.add(amountField);
        inputPanel.add(new JLabel("VIP Add-on (Will be Sliced):")); inputPanel.add(vipLoungeField);

        JButton calcButton = new JButton("Generate Insurance Claim via C++ Bridge");
        calcButton.setBackground(new Color(178, 34, 34));
        calcButton.setForeground(Color.WHITE);
        calcButton.setOpaque(true);
        calcButton.setBorderPainted(false);
        inputPanel.add(new JLabel("")); inputPanel.add(calcButton);

        panel.add(inputPanel, BorderLayout.NORTH);

        JTextArea receiptArea = new JTextArea();
        receiptArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        receiptArea.setBackground(new Color(30, 30, 30));
        receiptArea.setForeground(new Color(0, 255, 0));
        receiptArea.setEditable(false);
        panel.add(new JScrollPane(receiptArea), BorderLayout.CENTER);

        calcButton.addActionListener(e -> {
            try {
                String id = billIdField.getText();
                double amount = Double.parseDouble(amountField.getText());
                String vip = vipLoungeField.getText();
                String receipt = BillingBridge.processDischarge(id, amount, vip);
                receiptArea.setText(receipt);
            } catch (Exception ex) {
                receiptArea.setText("Error calling native C++ bridge: " + ex.getMessage());
            }
        });

        return panel;
    }

    // ======================== ADMIN: OOP CONCEPTS DEMO PANEL ========================
    private JPanel buildOOPDemoPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextArea output = new JTextArea();
        output.setFont(new Font("Monospaced", Font.PLAIN, 13));
        output.setBackground(new Color(30, 30, 30));
        output.setForeground(new Color(0, 255, 128));
        output.setEditable(false);
        output.setLineWrap(true);
        output.setWrapStyleWord(true);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));

        JButton polyBtn = new JButton("Polymorphism");
        JButton equalBtn = new JButton("equals/hashCode");
        JButton immutableBtn = new JButton("Immutable Class");
        JButton composBtn = new JButton("Composition");
        JButton singletonBtn = new JButton("Singleton");
        JButton exceptionBtn = new JButton("Custom Exceptions");

        for (JButton btn : new JButton[]{polyBtn, equalBtn, immutableBtn, composBtn, singletonBtn, exceptionBtn}) {
            btn.setBackground(new Color(60, 63, 65));
            btn.setForeground(Color.WHITE);
            btn.setOpaque(true);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btnPanel.add(btn);
        }

        panel.add(btnPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(output), BorderLayout.CENTER);

        // --- POLYMORPHISM DEMO ---
        polyBtn.addActionListener(e -> {
            output.setText("=== ABSTRACTION & POLYMORPHISM DEMO ===\n\n");
            output.append("[Concept] Person is ABSTRACT — cannot do 'new Person()'.\n");
            output.append("[Concept] Each subclass overrides getRole() differently.\n\n");

            Person[] people = {
                new Doctor("Dr. House", "9876543210", "D001", "Diagnostics", 500.0),
                new Receptionist("Jane", "1234567890", "S001", "Front Desk", 1),
                new Pharmacist("Bob", "5555555555", "S002", "Pharmacy", "PH-100"),
                new LabTechnician("Alice", "4444444444", "S003", "Pathology", "Hematology")
            };

            output.append("Calling toString() via Person reference (dynamic dispatch):\n\n");
            for (Person p : people) {
                output.append("  " + p.toString() + "\n");
            }
            output.append("\n[Result] Same method call, 4 different behaviors.\n");
            output.append("[Hierarchy] Person (abstract) -> Doctor\n");
            output.append("            Person (abstract) -> HospitalStaff (abstract) -> Receptionist, Pharmacist, LabTechnician\n");
        });

        // --- EQUALS / HASHCODE DEMO ---
        equalBtn.addActionListener(e -> {
            output.setText("=== OBJECT EQUALITY DEMO (equals & hashCode) ===\n\n");
            Patient p1 = new Patient("Krish", "123", "PAT-001", 20);
            Patient p2 = new Patient("Krish Patel", "456", "PAT-001", 21);
            Patient p3 = new Patient("Ravi", "789", "PAT-002", 25);

            output.append("p1 = Patient(name='Krish', id='PAT-001')\n");
            output.append("p2 = Patient(name='Krish Patel', id='PAT-001')  // same ID!\n");
            output.append("p3 = Patient(name='Ravi', id='PAT-002')\n\n");

            output.append("Reference equality (==):\n");
            output.append("  p1 == p2 : " + (p1 == p2) + "\n\n");

            output.append("Value equality (equals):\n");
            output.append("  p1.equals(p2) : " + p1.equals(p2) + "  <-- same patientId!\n");
            output.append("  p1.equals(p3) : " + p1.equals(p3) + " <-- different patientId\n\n");

            output.append("hashCode contract (equal objects -> equal hashes):\n");
            output.append("  p1.hashCode() = " + p1.hashCode() + "\n");
            output.append("  p2.hashCode() = " + p2.hashCode() + "  <-- matches p1!\n");
            output.append("  p3.hashCode() = " + p3.hashCode() + "\n\n");

            output.append("[Concept] equals() compares patientId (value equality).\n");
            output.append("[Concept] hashCode() contract: equal objects MUST have equal hashes.\n");
        });

        // --- IMMUTABLE CLASS DEMO ---
        immutableBtn.addActionListener(e -> {
            output.setText("=== IMMUTABLE CLASS DEMO (Prescription) ===\n\n");

            Prescription rx = new Prescription(
                "RX-5001", "PAT-001", "D001",
                List.of("Paracetamol", "Amoxicillin"), "500mg twice daily", "2026-10-13"
            );

            output.append("Created: " + rx + "\n\n");

            output.append("Attempting to modify the medications list...\n");
            try {
                rx.getMedications().add("Ibuprofen");
                output.append("BUG: Modification succeeded (should not happen)!\n");
            } catch (UnsupportedOperationException ex) {
                output.append("UnsupportedOperationException thrown!\n");
                output.append("The list is unmodifiable — immutability enforced.\n\n");
            }

            output.append("Immutability Rules Applied:\n");
            output.append("  1. Class is 'final' — cannot be subclassed\n");
            output.append("  2. All fields are 'final' — set once in constructor\n");
            output.append("  3. No setter methods exist\n");
            output.append("  4. List defensively copied via List.copyOf() + unmodifiableList()\n\n");
            output.append("[Benefit] Thread-safe by design — no synchronization needed.\n");
        });

        // --- COMPOSITION DEMO ---
        composBtn.addActionListener(e -> {
            output.setText("=== COMPOSITION DEMO (has-a) ===\n\n");

            Patient patient = new Patient("Krish", "9876543210", "PAT-001", 20);
            Doctor doctor = new Doctor("Dr. House", "1234567890", "D001", "Diagnostics", 500.0);
            Appointment apt = new Appointment("APT-001", patient, doctor, "2026-10-14 10:00");

            output.append("Patient:     " + patient.getName() + " (ID: " + patient.getPatientId() + ")\n");
            output.append("Doctor:      " + doctor.getName() + " (ID: " + doctor.getDoctorId() + ")\n");
            output.append("Appointment: " + apt + "\n\n");

            apt.updateStatus("In-Consultation");
            output.append("After updateStatus('In-Consultation'):\n");
            output.append("  " + apt + "\n\n");

            apt.updateStatus("Completed");
            output.append("After updateStatus('Completed'):\n");
            output.append("  " + apt + "\n\n");

            output.append("[Concept] Appointment HAS-A Patient and HAS-A Doctor.\n");
            output.append("[Concept] Unlike Inheritance (IS-A), Composition models ownership.\n");
            output.append("[Concept] Objects.requireNonNull() enforces non-null parts.\n");
        });

        // --- SINGLETON PATTERN DEMO ---
        singletonBtn.addActionListener(e -> {
            output.setText("=== SINGLETON PATTERN DEMO ===\n\n");

            DatabaseManager db1 = DatabaseManager.getInstance();
            DatabaseManager db2 = DatabaseManager.getInstance();

            output.append("DatabaseManager.getInstance() called twice:\n");
            output.append("  Instance 1 hash: " + System.identityHashCode(db1) + "\n");
            output.append("  Instance 2 hash: " + System.identityHashCode(db2) + "\n");
            output.append("  Same object?     " + (db1 == db2) + "\n\n");

            output.append("[Pattern] Bill Pugh Holder Idiom\n");
            output.append("  - Inner static class loaded lazily on first getInstance() call.\n");
            output.append("  - JVM guarantees thread-safe static initialization.\n\n");
            output.append("[Drawback] Tight coupling — all classes depend on a global instance.\n");
            output.append("  This makes unit testing harder (difficult to mock without DI).\n");
        });

        // --- CUSTOM EXCEPTION DEMO ---
        exceptionBtn.addActionListener(e -> {
            output.setText("=== CUSTOM EXCEPTION HIERARCHY DEMO ===\n\n");
            output.append("Exception\n");
            output.append("  |-- HospitalException (custom base)\n");
            output.append("        |-- PatientNotFoundException\n");
            output.append("        |-- AuthenticationFailedException\n");
            output.append("        |-- InvalidPrescriptionException\n\n");

            // Demo 1: PatientNotFoundException
            output.append("--- Test 1: Searching for non-existent patient ---\n");
            try {
                DatabaseManager.getInstance().fetchAndDecryptPatient("GHOST-999");
            } catch (PatientNotFoundException ex) {
                output.append("Caught: " + ex.getClass().getSimpleName() + "\n");
                output.append("Message: " + ex.getMessage() + "\n");
                output.append("Patient ID: " + ex.getPatientId() + "\n\n");
            } catch (HospitalException ex) {
                output.append("Caught: " + ex.getMessage() + "\n\n");
            }

            // Demo 2: AuthenticationFailedException
            output.append("--- Test 2: Authenticating with wrong credentials ---\n");
            try {
                AuthManager.authenticate("hacker", "password123");
            } catch (AuthenticationFailedException ex) {
                output.append("Caught: " + ex.getClass().getSimpleName() + "\n");
                output.append("Message: " + ex.getMessage() + "\n\n");
            }

            // Demo 3: InvalidPrescriptionException
            output.append("--- Test 3: Creating invalid prescription ---\n");
            try {
                throw new InvalidPrescriptionException("Medication list cannot be empty.");
            } catch (InvalidPrescriptionException ex) {
                output.append("Caught: " + ex.getClass().getSimpleName() + "\n");
                output.append("Message: " + ex.getMessage() + "\n\n");
            }

            output.append("[Concept] Custom exceptions carry domain-specific data.\n");
            output.append("[Concept] Specific catch blocks handle each failure case differently.\n");
        });

        return panel;
    }

    // ======================== DOCTOR: MEDICAL RECORDS PANEL ========================
    private JPanel buildDoctorPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Enter Patient ID:"));
        JTextField searchField = new JTextField(15);
        searchPanel.add(searchField);

        JButton searchBtn = new JButton("Fetch & Decrypt Record");
        searchBtn.setBackground(new Color(139, 0, 0));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setOpaque(true);
        searchBtn.setBorderPainted(false);
        searchPanel.add(searchBtn);
        panel.add(searchPanel, BorderLayout.NORTH);

        JTextArea displayArea = new JTextArea("Patient data is RSA-encrypted.\nEnter an ID above to decrypt with Private Key.");
        displayArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        displayArea.setBackground(new Color(40, 44, 52));
        displayArea.setForeground(new Color(152, 195, 121));
        displayArea.setEditable(false);
        panel.add(new JScrollPane(displayArea), BorderLayout.CENTER);

        searchBtn.addActionListener(e -> {
            String id = searchField.getText().trim();
            if (id.isEmpty()) return;

            try {
                // Uses custom exception — throws PatientNotFoundException
                String[] data = DatabaseManager.getInstance().fetchAndDecryptPatient(id);
                displayArea.setText("=== SECURE MEDICAL RECORD ===\n\n");
                displayArea.append("Patient ID:  " + id + " (Search Key)\n");
                displayArea.append("Name:        " + data[0] + " (RSA Decrypted)\n");
                displayArea.append("Age:         " + data[1] + " (RSA Decrypted)\n");
                displayArea.append("Contact:     " + data[2] + " (RSA Decrypted)\n");
                displayArea.append("Blood Group: " + data[3] + " (RSA Decrypted)\n");

            } catch (PatientNotFoundException ex) {
                // Custom exception caught specifically
                displayArea.setText("PATIENT NOT FOUND\n\n" + ex.getMessage());
            } catch (HospitalException ex) {
                displayArea.setText("SYSTEM ERROR\n\n" + ex.getMessage());
            }
        });

        return panel;
    }

    // ======================== DOCTOR: PRESCRIPTION PANEL (Immutable) ========================
    private JPanel buildPrescriptionPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        JTextField rxIdField = new JTextField("RX-" + (System.currentTimeMillis() % 10000));
        JTextField patientIdField = new JTextField();
        JTextField medsField = new JTextField("Paracetamol, Amoxicillin");
        JTextField dosageField = new JTextField("500mg twice daily");

        formPanel.add(new JLabel("Prescription ID:")); formPanel.add(rxIdField);
        formPanel.add(new JLabel("Patient ID:")); formPanel.add(patientIdField);
        formPanel.add(new JLabel("Medications (comma-sep):")); formPanel.add(medsField);
        formPanel.add(new JLabel("Dosage:")); formPanel.add(dosageField);

        JButton issueBtn = new JButton("Issue Immutable Prescription");
        issueBtn.setBackground(new Color(0, 102, 204));
        issueBtn.setForeground(Color.WHITE);
        issueBtn.setOpaque(true);
        issueBtn.setBorderPainted(false);
        formPanel.add(new JLabel("")); formPanel.add(issueBtn);

        panel.add(formPanel, BorderLayout.NORTH);

        JTextArea output = new JTextArea();
        output.setFont(new Font("Monospaced", Font.PLAIN, 13));
        output.setBackground(new Color(40, 44, 52));
        output.setForeground(new Color(152, 195, 121));
        output.setEditable(false);
        panel.add(new JScrollPane(output), BorderLayout.CENTER);

        issueBtn.addActionListener(e -> {
            try {
                String rxId = rxIdField.getText().trim();
                String patId = patientIdField.getText().trim();

                if (rxId.isEmpty() || patId.isEmpty()) {
                    throw new InvalidPrescriptionException("Prescription ID and Patient ID are required.");
                }

                List<String> meds = Arrays.asList(medsField.getText().split(",\\s*"));
                if (meds.isEmpty() || meds.get(0).trim().isEmpty()) {
                    throw new InvalidPrescriptionException("At least one medication is required.");
                }

                // Create IMMUTABLE prescription
                Prescription rx = new Prescription(
                    rxId, patId, "DR-CURRENT", meds,
                    dosageField.getText(), java.time.LocalDate.now().toString()
                );

                output.setText("=== PRESCRIPTION ISSUED (IMMUTABLE) ===\n\n");
                output.append(rx.toString() + "\n\n");
                output.append("This Prescription object is now frozen.\n");
                output.append("  - Class is 'final' — cannot be subclassed.\n");
                output.append("  - All fields are 'final' — no setters exist.\n");
                output.append("  - getMedications() returns unmodifiable list.\n");

            } catch (InvalidPrescriptionException ex) {
                // Custom exception for validation failures
                output.setText("VALIDATION ERROR\n\n" + ex.getMessage());
            }
        });

        return panel;
    }

    // ======================== PHARMACIST: PHARMACY PANEL ========================
    private JPanel buildPharmacyPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextArea output = new JTextArea();
        output.setFont(new Font("Monospaced", Font.PLAIN, 13));
        output.setBackground(new Color(40, 44, 52));
        output.setForeground(new Color(152, 195, 121));
        output.setEditable(false);

        JButton demoBtn = new JButton("View Sample Prescriptions");
        demoBtn.setBackground(new Color(0, 102, 204));
        demoBtn.setForeground(Color.WHITE);
        demoBtn.setOpaque(true);
        demoBtn.setBorderPainted(false);

        panel.add(demoBtn, BorderLayout.NORTH);
        panel.add(new JScrollPane(output), BorderLayout.CENTER);

        demoBtn.addActionListener(e -> {
            output.setText("=== PHARMACY TERMINAL ===\n\n");

            Prescription rx1 = new Prescription(
                "RX-1001", "PAT-001", "D001",
                List.of("Paracetamol 500mg", "Vitamin D3"), "As directed", "2026-10-13"
            );
            Prescription rx2 = new Prescription(
                "RX-1002", "PAT-002", "D001",
                List.of("Amoxicillin 250mg"), "Twice daily for 5 days", "2026-10-13"
            );

            output.append("Pending Prescriptions:\n\n");
            output.append("  1. " + rx1 + "\n");
            output.append("  2. " + rx2 + "\n\n");

            output.append("[Note] Prescriptions are IMMUTABLE — pharmacist cannot alter them.\n");
            output.append("[Note] This ensures prescription integrity between Doctor and Pharmacy.\n");
        });

        return panel;
    }
}
```

---

### 4.19 `CMakeLists.txt`
```cmake
cmake_minimum_required(VERSION 3.18)
project(HospitalBillingBridge CXX)

set(CMAKE_CXX_STANDARD 17)

# Find Java, JNI, and SWIG automatically
find_package(Java REQUIRED)
find_package(JNI REQUIRED)
find_package(SWIG REQUIRED)
include(UseSWIG)

include_directories(${JNI_INCLUDE_DIRS} .)

# Compile the SWIG interface and C++ logic into a shared library
set_property(SOURCE MedicalBill.i PROPERTY CPLUSPLUS ON)
swig_add_library(BillingBridge
  TYPE SHARED
  LANGUAGE java
  SOURCES MedicalBill.i MedicalBill.cpp
)
target_link_libraries(BillingBridge ${JNI_LIBRARIES})
```

---

## 5. C++ Native Code & SWIG Interface (Existing / Reference)

### 5.1 `MedicalBill.h`
```cpp
#ifndef MEDICALBILL_H
#define MEDICALBILL_H

#include <string>
#include <iostream>

class MedicalBill {
public:
    std::string billId;
    double amount;

    MedicalBill(std::string id, double amt);
    MedicalBill();

    friend MedicalBill operator+(const MedicalBill& b1, const MedicalBill& b2);
    friend std::ostream& operator<<(std::ostream& os, const MedicalBill& bill);

    std::string getBillId();
    double getAmount();
    MedicalBill add(const MedicalBill& other) const;

    virtual void printInfo() const;
};

std::string processDischarge(std::string billId, double amount, std::string vipLounge);

// --- OBJECT SLICING ---
class VIPBill : public MedicalBill {
public:
    std::string loungeAccess;
    VIPBill(std::string id, double amt, std::string lounge);
    void printInfo() const override;
};

void runAdvancedCppDemos();

// 1. VIRTUAL BASE CLASS (Diamond Problem prevention)
class AbstractTax {
protected:
    double taxAmount;
public:
    AbstractTax();
    virtual ~AbstractTax() = default;
    
    // 2. VIRTUAL FUNCTION
    virtual std::string getTaxDetails() = 0; 
};

class MedicalTax : virtual public AbstractTax { 
public:
    MedicalTax();
    std::string getTaxDetails() override;
};

class VIPTax : virtual public AbstractTax { 
public:
    VIPTax();
    std::string getTaxDetails() override;
};

// MULTIPLE INHERITANCE using Virtual Base Classes
class FinalDischargeTax : public MedicalTax, public VIPTax {
private:
    std::string referenceId;
public:
    FinalDischargeTax(std::string ref);
    
    // 3. "this" POINTER
    FinalDischargeTax* setReferenceId(std::string ref); 
    
    std::string getTaxDetails() override;
};

#endif
```

### 5.2 `MedicalBill.cpp`
```cpp
#include "MedicalBill.h"
#include <sstream>

MedicalBill::MedicalBill(std::string id, double amt) : billId(id), amount(amt) {}
MedicalBill::MedicalBill() : billId("B000"), amount(0.0) {}

std::string MedicalBill::getBillId() { return billId; }
double MedicalBill::getAmount() { return amount; }

std::ostream& operator<<(std::ostream& os, const MedicalBill& bill) {
    os << "BILL ID: " << bill.billId << " | AMOUNT DUE: $" << bill.amount;
    return os;
}

VIPBill::VIPBill(std::string id, double amt, std::string lounge) : MedicalBill(id, amt), loungeAccess(lounge) {}

AbstractTax::AbstractTax() { this->taxAmount = 0.0; } 

MedicalTax::MedicalTax() { this->taxAmount = 50.0; }
std::string MedicalTax::getTaxDetails() { return "Standard Medical Tax Applied"; }

VIPTax::VIPTax() { this->taxAmount = 100.0; }
std::string VIPTax::getTaxDetails() { return "Premium VIP Tax Applied"; }

FinalDischargeTax::FinalDischargeTax(std::string ref) {
    this->referenceId = ref; 
    this->taxAmount = 150.0; 
}

// Returning "this" pointer for method chaining
FinalDischargeTax* FinalDischargeTax::setReferenceId(std::string ref) {
    this->referenceId = ref; 
    return this;             
}

std::string FinalDischargeTax::getTaxDetails() {
    return "Combined Hospital Tax Assessed for Ref: " + this->referenceId;
}

std::string processDischarge(std::string billId, double amount, std::string vipLounge) {
    std::ostringstream receipt;
    
    // Original Slicing Logic
    VIPBill fullBill(billId, amount, vipLounge);
    MedicalBill insuranceClaim = fullBill; // Object slicing: loungeAccess is sliced off

    // Pointer to Objects & Pointer to Derived Classes
    FinalDischargeTax finalTax(billId);
    finalTax.setReferenceId(billId); 
    
    AbstractTax* basePtr;    
    basePtr = &finalTax;     

    receipt << "--- HOSPITAL DISCHARGE SUMMARY ---\n";
    receipt << "Patient opted for VIP upgrade (" << fullBill.loungeAccess << ").\n\n";
    
    receipt << "[Insurance Claim Generated - Non-Medical Charges Sliced]\n";
    receipt << insuranceClaim << "\n\n";
    
    receipt << "[Government Taxes - Dynamic Dispatch via Virtual Functions]\n";
    
    receipt << ">> " << basePtr->getTaxDetails() << " (Amount: $" << 150.0 << ")\n";
    receipt << "----------------------------------";

    return receipt.str(); 
}

MedicalBill MedicalBill::add(const MedicalBill& other) const { return MedicalBill("COMBINED-TOTAL", this->amount + other.amount); }
void MedicalBill::printInfo() const { std::cout << "Standard Bill - " << billId << std::endl; }
void VIPBill::printInfo() const { std::cout << "VIP Bill - " << billId << " | Access: " << loungeAccess << std::endl; }
void runAdvancedCppDemos() { std::cout << "Advanced C++ Demos initialized and moved to GUI." << std::endl; }
```

### 5.3 `MedicalBill.i`
```swig
%module BillingBridge
%{
#include "MedicalBill.h"
%}
%include "std_string.i"

class MedicalBill {
public:
    MedicalBill(std::string id, double amt);
    MedicalBill();
    std::string getBillId();
    double getAmount();
    MedicalBill add(const MedicalBill& other) const;
    void printInfo() const;
};

std::string processDischarge(std::string billId, double amount, std::string vipLounge);
void runAdvancedCppDemos();
```

---

## 6. How to Build & Run the System

### Prerequisites:
- JDK 17+ installed.
- SQLite JDBC driver `sqlite-jdbc-3.42.0.0.jar` in current directory.
- Compiled native library `libBillingBridge.dylib` in current directory (or rebuild via `cmake . && make`).

### Step-by-Step Commands:

```bash
# 1. (Optional) Rebuild C++ SWIG native bridge if on a new OS / machine:
cmake .
make

# 2. Compile all Java source files with SQLite JAR on classpath:
javac -cp ".:sqlite-jdbc-3.42.0.0.jar" *.java

# 3. Launch Login Swing UI:
java -cp ".:sqlite-jdbc-3.42.0.0.jar" -Djava.library.path=. --enable-native-access=ALL-UNNAMED LoginUI
```

### Seeded Credentials:
- **Admin:** `admin` / `admin123` (Access to Patient Registration, C++ Billing Claim, Interactive OOP Concept Demo)
- **Doctor:** `dr_house` / `doc123` (Access to Search & Decrypt Medical Records, Issue Immutable Prescriptions)
- **Pharmacist:** `tech_lisa` / `tech123` (Access to Pharmacy Terminal to view read-only immutable prescriptions)

---

## 7. Roadmap & Next Tasks (Steps 5–8 for Incoming AI)

The next AI picking up this task should focus on implementing the remaining evaluation checklist items:

1. **Step 5: Advanced Java Types & Abstractions**
   - **Generics:** Create a generic `Repository<T, ID>` interface and an in-memory/DB `GenericRepository<T, ID>` (e.g. `Repository<Patient, String>`, `Repository<Doctor, String>`).
   - **Interfaces:** Create `Billable` (for `MedicalBill`, `Prescription`), `Searchable` (for `Patient`, `Doctor`), and `Exportable` interfaces.
   - **Collections & Streams:** Implement patient filtering / sorting using Java 8+ Streams (`List<Patient>`, `HashMap<String, Patient>`, `filter`, `map`, `sorted`).
2. **Step 6: Multithreading & Concurrency**
   - Use `SwingWorker` for heavy background RSA encryption and DB queries to prevent Swing UI freezing.
   - Implement thread-safe counters (e.g., `AtomicInteger`) and a producer-consumer prescription queue using `BlockingQueue<Prescription>` between Doctor and Pharmacist.
3. **Step 7: Unit Testing & Dependency Injection**
   - Create JUnit 5 test classes verifying `Patient.equals`/`hashCode`, `Prescription` immutability, `DatabaseManager` singleton identity, and `HospitalException` throwing.
   - Introduce constructor-based Dependency Injection for `DatabaseManager` and `SecurityHelper` instead of hardcoded singletons to support mock objects.
4. **Step 8: Additional Design Patterns**
   - **Factory Method:** `StaffFactory.createStaff(String role, ...)` returning `Receptionist`, `Pharmacist`, or `LabTechnician`.
   - **Observer Pattern:** Notify registered Pharmacist listeners whenever a Doctor issues a new `Prescription`.

