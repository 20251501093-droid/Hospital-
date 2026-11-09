import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

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

    // THREADING: AtomicInteger for thread-safe counter across all instances
    private static final AtomicInteger totalPatients = new AtomicInteger(0);

    // Default constructor
    public Patient() {
        super();
        this.patientId = "Unassigned";
        this.age = 0;
    }

    // Main constructor — calls super() for Person fields
    public Patient(String name, String contactNumber, String patientId, int age) {
        super(name, contactNumber);
        this.patientId = patientId;
        this.age = age;
        totalPatients.incrementAndGet();
    }

    // Getters (Encapsulation)
    public String getPatientId() { return patientId; }
    public int getAge() { return age; }

    // Static method — can be called without an object: Patient.getTotalPatients()
    public static int getTotalPatients() { return totalPatients.get(); }

    // Method Overloading
    public void bookAppointment() {
        System.out.println(getName() + " is booking an appointment.");
    }
    public void bookAppointment(String preferredDate) {
        System.out.println(getName() + " is booking an appointment for " + preferredDate + ".");
    }

    public void viewHistory() {
        System.out.println("Viewing medical history for " + getName());
    }

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
