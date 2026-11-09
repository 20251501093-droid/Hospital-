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

