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

    // Method used in Main.java demonstration
    public void prescribeMedication() {
        System.out.println("Dr. " + getName() + " (" + specialization + ") is prescribing medication. Fee: $" + consultationFee);
    }

    // Polymorphism: concrete implementation of abstract getRole()
    @Override
    public String getRole() { return "Doctor"; }
}
