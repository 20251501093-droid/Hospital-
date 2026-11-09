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
