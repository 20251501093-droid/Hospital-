import java.util.ArrayList;

public class Main {
    static {
        
        System.loadLibrary("BillingBridge"); 
    }
    public static void main(String[] args) {
        System.out.println("=============================================");
        System.out.println(" Hospital Management System - Phase 2 (Collections)");
        System.out.println("=============================================\n");

        // 1. Dynamic Patient Storage using ArrayList
        ArrayList<Patient> patientList = new ArrayList<>();
        patientList.add(new Patient("Alice Smith", "555-0192", "P10023", 30));
        patientList.add(new Patient("John Doe", "555-0199", "P10024", 45));

        // 2. Dynamic Polymorphic Staff Storage using ArrayList
        ArrayList<HospitalStaff> staffList = new ArrayList<>();
        staffList.add(new Receptionist("Sarah Jenkins", "555-0100", "S-001", "Front Desk", 3));
        staffList.add(new Pharmacist("David Miller", "555-0200", "S-045", "Pharmacy", "RX-99234"));
        staffList.add(new LabTechnician("Lisa Rostova", "555-0300", "S-098", "Pathology", "Biochemistry"));

        // 3. Displaying and Iterating Through Collections
        System.out.println("--- Registered Patients ---");
        for (Patient p : patientList) {
            System.out.println("Patient: " + p.getName() + " | Contact: " + p.getContactNumber());
        }

        System.out.println("\n--- Hospital Active Staff ---");
        for (HospitalStaff staff : staffList) {
            System.out.println("Staff ID: " + staff.getStaffId() + " | Department: " + staff.department);
        }

        // 4. Demonstrating Method Overloading
        System.out.println("\n--- Demonstrating Method Overloading ---");
        patientList.get(0).bookAppointment(); // Version 1 (no arguments)
        patientList.get(0).bookAppointment("Tomorrow at 10:00 AM"); // Version 2 (with date argument)

        // 5. Operational Staff Behaviors
        System.out.println("\n--- Departmental Operations ---");
        Doctor doctor = new Doctor("Dr. Robert House", "555-0011", "D405", "Cardiology", 150.00);
        doctor.prescribeMedication();

        Pharmacist pharma = (Pharmacist) staffList.get(1);
        pharma.dispenseMedication("Amoxicillin 500mg");

        LabTechnician tech = (LabTechnician) staffList.get(2);
        tech.conductTest("Complete Blood Count (CBC)");
        System.out.println("\n--- C++ Native Billing (SWIG + Friend Operator) ---");
        
        MedicalBill bill1 = new MedicalBill("B101", 150.0);
        MedicalBill bill2 = new MedicalBill("B102", 75.5);
        
        // This calls the C++ friend operator+ in the background!
        MedicalBill total = bill1.add(bill2); 
        
        System.out.println("Bill 1: " + bill1.getBillId() + " | $" + bill1.getAmount());
        System.out.println("Bill 2: " + bill2.getBillId() + " | $" + bill2.getAmount());
        System.out.println("Combined Bill: " + total.getBillId() + " | $" + total.getAmount());
    
    }
}