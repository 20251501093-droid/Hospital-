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

    public void conductTest(String testType) {
        System.out.println(getName() + " is currently running a " + testType + " test in the " + labSpecialty + " lab.");
    }

    // Polymorphism: concrete getRole() implementation
    @Override
    public String getRole() { return "Lab Technician"; }
}
