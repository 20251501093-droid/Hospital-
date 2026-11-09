import java.util.Objects;
public class Appointment {
    private final String appointmentId;
    private final Patient patient;    
    private final Doctor doctor;      
    private final String dateTime;
    private String status;

    public Appointment(String appointmentId, Patient patient, Doctor doctor, String dateTime) {
        this.appointmentId = Objects.requireNonNull(appointmentId, "Appointment ID cannot be null");
        this.patient = Objects.requireNonNull(patient, "Patient cannot be null");
        this.doctor = Objects.requireNonNull(doctor, "Doctor cannot be null");
        this.dateTime = Objects.requireNonNull(dateTime, "DateTime cannot be null");
        this.status = "Scheduled";
    }

   
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