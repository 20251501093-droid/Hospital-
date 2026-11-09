/**
 * Thrown when a prescription cannot be created due to validation errors.
 */
public class InvalidPrescriptionException extends HospitalException {
    public InvalidPrescriptionException(String reason) {
        super("Invalid prescription: " + reason);
    }
}

