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

    public void dispenseMedication(String medicationName) {
        System.out.println(getName() + " (License: " + licenseNumber + ") is preparing to dispense: " + medicationName);
    }

    // Polymorphism: concrete getRole() implementation
    @Override
    public String getRole() { return "Pharmacist"; }
}
