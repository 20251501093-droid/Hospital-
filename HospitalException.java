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

