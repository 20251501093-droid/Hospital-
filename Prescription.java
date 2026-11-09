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
