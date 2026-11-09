/**
 * RECEPTIONIST — concrete subclass of HospitalStaff.
 * Person -> HospitalStaff -> Receptionist (3 levels of inheritance)
 */
public class Receptionist extends HospitalStaff {
    private int deskNumber;

    public Receptionist() {
        super();
        this.deskNumber = 0;
    }

    public Receptionist(String name, String contactNumber, String staffId, String department, int deskNumber) {
        super(name, contactNumber, staffId, department);
        this.deskNumber = deskNumber;
    }

    public int getDeskNumber() { return deskNumber; }

    // Polymorphism: concrete getRole() implementation
    @Override
    public String getRole() { return "Receptionist"; }
}
