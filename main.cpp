#include "HospitalSystem.hpp"
#include "MedicalBill.hpp"
#include "HospitalExceptions.hpp"

#include <iostream>
#include <string>
#include <vector>
#include <sstream>

// ============================================================================
// 1. BASIC VARIABLES STORING LOGIN CREDENTIALS & ROLES
// ============================================================================
std::string adminUser = "admin";
std::string adminPass = "admin123";

std::string doctorUser = "dr_house";
std::string doctorPass = "doc123";

std::string pharmacistUser = "tech_lisa";
std::string pharmacistPass = "tech123";

// Current session variables
std::string currentUser = "";
std::string currentRole = ""; // "ADMIN", "DOCTOR", or "PHARMACIST"

// ============================================================================
// 2. BASIC CONSOLE INPUT HELPERS
// ============================================================================
static std::string readLine(const std::string& prompt) {
    std::cout << prompt;
    std::string line;
    std::getline(std::cin, line);
    return line;
}

static int readInt(const std::string& prompt) {
    while (true) {
        std::cout << prompt;
        std::string line;
        std::getline(std::cin, line);
        try {
            return std::stoi(line);
        } catch (...) {
            std::cout << " [Error] Please enter a valid number.\n";
        }
    }
}

static double readDouble(const std::string& prompt) {
    while (true) {
        std::cout << prompt;
        std::string line;
        std::getline(std::cin, line);
        try {
            return std::stod(line);
        } catch (...) {
            std::cout << " [Error] Please enter a valid decimal number.\n";
        }
    }
}

static void pauseForUser() {
    std::cout << "\nPress Enter to continue...";
    std::string temp;
    std::getline(std::cin, temp);
}

// ============================================================================
// 3. SIMPLE LOGIN VERIFICATION (Direct Variable Comparison)
// ============================================================================
static void performLogin() {
    std::cout << "\n=================================================================\n";
    std::cout << "                 🔐 HMS TERMINAL LOGIN                           \n";
    std::cout << "=================================================================\n";
    std::cout << " Stored User Accounts:\n";
    std::cout << "   - Admin:      admin     / admin123\n";
    std::cout << "   - Doctor:     dr_house  / doc123\n";
    std::cout << "   - Pharmacist: tech_lisa / tech123\n";
    std::cout << "=================================================================\n";

    while (true) {
        std::string enteredId = readLine("Enter Username / ID: ");
        std::string enteredPass = readLine("Enter Password: ");

        // Compare against simple variables
        if (enteredId == adminUser && enteredPass == adminPass) {
            currentUser = "Administrator";
            currentRole = "ADMIN";
            std::cout << "\n✓ Login Successful! Welcome " << currentUser << " [Role: ADMIN]\n";
            break;
        } else if (enteredId == doctorUser && enteredPass == doctorPass) {
            currentUser = "Dr. Robert House";
            currentRole = "DOCTOR";
            std::cout << "\n✓ Login Successful! Welcome " << currentUser << " [Role: DOCTOR]\n";
            break;
        } else if (enteredId == pharmacistUser && enteredPass == pharmacistPass) {
            currentUser = "Lisa (Head Pharmacist)";
            currentRole = "PHARMACIST";
            std::cout << "\n✓ Login Successful! Welcome " << currentUser << " [Role: PHARMACIST]\n";
            break;
        } else {
            std::cout << "\n❌ Invalid ID or Password! Please try again.\n\n";
        }
    }
}

// ============================================================================
// 4. ACTION FUNCTIONS (Clean, Reusable Domain Operations)
// ============================================================================

static void actionRegisterPatient(HospitalSystem& hms) {
    std::cout << "\n--- Register New Patient ---\n";
    std::string id = readLine("Enter Patient ID (e.g., P104): ");
    std::string name = readLine("Enter Full Name: ");
    std::string contact = readLine("Enter Contact Phone: ");
    int age = readInt("Enter Age: ");
    std::string blood = readLine("Enter Blood Group (e.g., O+, A-, B+): ");
    std::string allergies = readLine("Enter Known Allergies (or None): ");
    std::string diagnosis = readLine("Enter Initial Diagnosis/Reason: ");

    hms.registerPatient(name, contact, id, age, blood, allergies, diagnosis);
    std::cout << "\n✓ Patient [" << id << "] registered successfully!\n";
    std::cout << "✓ Total Registered Patients (Static Counter): "
              << Patient::getTotalPatients() << "\n";
    pauseForUser();
}

static void actionSearchPatient(HospitalSystem& hms) {
    std::cout << "\n--- Search Patient by ID ---\n";
    std::cout << "Hint: Try P101, P102, P103, or enter 'GHOST-99' for custom exception demo.\n";
    std::string id = readLine("Enter Patient ID: ");
    try {
        auto patient = hms.findPatient(id);
        std::cout << "\n[Match Found]\n";
        patient->displayDetails();
    } catch (const PatientNotFoundException& e) {
        std::cout << "\n[Exception Caught]: " << e.what() << "\n";
    }
    pauseForUser();
}

static void actionViewPatients(HospitalSystem& hms) {
    std::cout << "\n--- All Registered Patients & Nested Medical Records ---\n";
    std::cout << "Hospital Branch: " << Patient::HOSPITAL_BRANCH << "\n";
    std::cout << "Total Count:     " << Patient::getTotalPatients() << "\n\n";

    const auto& patients = hms.getAllPatients();
    for (const auto& pair : patients) {
        pair.second->displayDetails();
        std::cout << "-----------------------------------------------------------------\n";
    }
    pauseForUser();
}

static void actionViewStaff(HospitalSystem& hms) {
    std::cout << "\n--- Hospital Staff & Doctors (Runtime Polymorphism) ---\n\n";
    std::cout << ">> DOCTORS <<\n";
    for (const auto& pair : hms.getAllDoctors()) {
        pair.second->displayDetails();
    }

    std::cout << "\n>> MULTILEVEL STAFF (Receptionist, Pharmacist, Lab Tech) <<\n";
    for (const auto& staff : hms.getAllStaff()) {
        staff->displayDetails();
    }
    pauseForUser();
}

static void actionBookAppointment(HospitalSystem& hms) {
    std::cout << "\n--- Book Doctor Appointment (Composition HAS-A) ---\n";
    std::string apptId = "APT-" + std::to_string(1000 + (rand() % 9000));
    std::cout << "Generated Appointment ID: " << apptId << "\n";
    std::string pId = readLine("Enter Patient ID (e.g., P101): ");
    std::string dId = readLine("Enter Doctor ID (e.g., D01, D02, D03): ");
    std::string date = readLine("Enter Preferred Date & Time (e.g., 2026-11-21 02:00 PM): ");

    try {
        hms.bookAppointment(apptId, pId, dId, date);
        std::cout << "\n✓ Appointment successfully scheduled!\n";
        std::cout << "✓ Recent Appointments List:\n";
        for (const auto& appt : hms.getAllAppointments()) {
            std::cout << "  * " << appt << "\n";
        }
    } catch (const HospitalException& e) {
        std::cout << "\n[Error Booking Appointment]: " << e.what() << "\n";
    }
    pauseForUser();
}

static void actionIssuePrescription(HospitalSystem& hms) {
    std::cout << "\n--- Issue Prescription (Doctor Producer -> Queue) ---\n";
    std::string rxId = "RX-" + std::to_string(600 + (rand() % 300));
    std::cout << "Prescription ID: " << rxId << "\n";
    std::string pId = readLine("Enter Patient ID (e.g., P101): ");
    std::string dId = readLine("Enter Doctor ID (e.g., D01): ");
    std::string medInput = readLine("Enter Medications (comma-separated, e.g., Amoxicillin 500mg, Paracetamol 650mg): ");
    std::string dosage = readLine("Enter Dosage Instructions: ");
    std::string date = readLine("Enter Date Issued (e.g., 2026-11-19): ");

    std::vector<std::string> meds;
    std::stringstream ss(medInput);
    std::string item;
    while (std::getline(ss, item, ',')) {
        size_t first = item.find_first_not_of(" \t");
        size_t last = item.find_last_not_of(" \t");
        if (first != std::string::npos && last != std::string::npos) {
            meds.push_back(item.substr(first, last - first + 1));
        }
    }

    try {
        hms.issuePrescription(rxId, pId, dId, meds, dosage, date);
        std::cout << "\n✓ Immutable Prescription created and pushed to HospitalQueue<Prescription>!\n";
        std::cout << "✓ Current Pending in Queue: " << hms.getPendingPrescriptions().size() << "\n";
    } catch (const HospitalException& e) {
        std::cout << "\n[Error]: " << e.what() << "\n";
    }
    pauseForUser();
}

static void actionDispensePrescription(HospitalSystem& hms) {
    std::cout << "\n--- Dispense Prescription (Pharmacist Consumer Queue) ---\n";
    try {
        Prescription dispensed = hms.dispensePrescription();
        std::cout << "✓ Pharmacist successfully popped & dispensed prescription:\n";
        std::cout << "  " << dispensed << "\n";
        std::cout << "✓ Remaining in Queue: " << hms.getPendingPrescriptions().size() << "\n";
    } catch (const InvalidPrescriptionException& e) {
        std::cout << "[Queue Status]: " << e.what() << "\n";
    }
    pauseForUser();
}

static void actionViewQueue(HospitalSystem& hms) {
    std::cout << "\n--- Pending Prescriptions in Queue ---\n";
    auto queueSnapshot = hms.getPendingPrescriptions();
    if (queueSnapshot.empty()) {
        std::cout << "The prescription queue is currently empty.\n";
    } else {
        std::cout << "Total Pending: " << queueSnapshot.size() << "\n\n";
        for (size_t i = 0; i < queueSnapshot.size(); ++i) {
            std::cout << " [" << (i + 1) << "] " << queueSnapshot[i] << "\n";
        }
    }
    pauseForUser();
}

static void actionDischargeBill() {
    std::cout << "\n--- Discharge Billing & Object Slicing Demonstration ---\n";
    std::string billId = readLine("Enter Bill ID (e.g., BILL-901): ");
    double amount = readDouble("Enter Base Medical/Room Charges ($): ");
    std::string vipOption = readLine("Include VIP Lounge Package? (yes/no): ");

    std::string lounge = (vipOption == "yes" || vipOption == "y" || vipOption == "YES")
                         ? "Skyline Presidential Suite" : "Standard Ward";

    std::cout << "\n" << processDischarge(billId, amount, lounge) << "\n";

    std::string demoChoice = readLine("Would you like to see explicit Object Slicing side-by-side? (yes/no): ");
    if (demoChoice == "yes" || demoChoice == "y") {
        demonstrateObjectSlicing();
    }
    pauseForUser();
}

static void actionTaxDemo() {
    std::cout << "\n--- Diamond Inheritance & Virtual Base Classes Demo ---\n";
    std::cout << "Creating FinalDischargeTax which inherits from MedicalTax and VIPTax,\n";
    std::cout << "both of which virtually inherit from AbstractTax (Virtual Base Class).\n\n";

    std::string ref = readLine("Enter Reference Code for Tax Assessment (e.g., TAX-2026): ");
    FinalDischargeTax tax(ref);

    // Demonstrate "this" pointer chaining
    std::cout << "\n>> Demonstrating 'this' pointer method chaining: tax.setReferenceId(...)\n";
    tax.setReferenceId(ref + "-VERIFIED");

    AbstractTax* baseTaxPtr = &tax;
    std::cout << ">> Virtual Base AbstractTax dynamic dispatch:\n";
    std::cout << "   " << baseTaxPtr->getTaxDetails() << "\n";
    std::cout << "   Tax Assessment Amount: $" << baseTaxPtr->getTaxAmount() << "\n";
    std::cout << ">> Result: No duplicate taxAmount subobject created! Diamond problem resolved.\n";
    pauseForUser();
}

static void actionConcurrencySimulation(HospitalSystem& hms) {
    int secs = readInt("Enter simulation duration in seconds (recommended 3-6): ");
    if (secs <= 0) secs = 4;
    hms.runConcurrentPharmacySimulation(secs);
    pauseForUser();
}

// ============================================================================
// 5. ROLE-SPECIFIC PORTALS (Shows ONLY functions allowed for that role)
// ============================================================================

// --- DOCTOR PORTAL ---
static bool runDoctorPortal(HospitalSystem& hms) {
    while (true) {
        std::cout << "\n=================================================================\n";
        std::cout << "     🩺 HMS [DOCTOR PORTAL] — Logged In: " << currentUser << "\n";
        std::cout << "=================================================================\n";
        std::cout << " 1. Search Patient by ID\n";
        std::cout << " 2. View All Patients & Medical Records\n";
        std::cout << " 3. Register New Patient\n";
        std::cout << " 4. Book Doctor Appointment\n";
        std::cout << " 5. Issue Prescription (Producer)\n";
        std::cout << " 6. View Hospital Staff & Specialists\n";
        std::cout << " 7. Logout / Switch User\n";
        std::cout << " 0. Exit System\n";
        std::cout << "=================================================================\n";

        int choice = readInt("Select an option (0-7): ");
        switch (choice) {
            case 1: actionSearchPatient(hms); break;
            case 2: actionViewPatients(hms); break;
            case 3: actionRegisterPatient(hms); break;
            case 4: actionBookAppointment(hms); break;
            case 5: actionIssuePrescription(hms); break;
            case 6: actionViewStaff(hms); break;
            case 7: return true;  // Logout / Switch User
            case 0: return false; // Exit System
            default: std::cout << "\nInvalid choice. Please select 0 to 7.\n"; break;
        }
    }
}

// --- PHARMACIST PORTAL ---
static bool runPharmacistPortal(HospitalSystem& hms) {
    while (true) {
        std::cout << "\n=================================================================\n";
        std::cout << "     💊 HMS [PHARMACY PORTAL] — Logged In: " << currentUser << "\n";
        std::cout << "=================================================================\n";
        std::cout << " 1. Dispense Next Prescription (Consumer Queue)\n";
        std::cout << " 2. View Pending Prescriptions in Queue\n";
        std::cout << " 3. View All Patients & Medical Records\n";
        std::cout << " 4. Search Patient by ID\n";
        std::cout << " 5. Run Live Multi-Threaded Simulation (Doctor <-> Pharmacist)\n";
        std::cout << " 6. Logout / Switch User\n";
        std::cout << " 0. Exit System\n";
        std::cout << "=================================================================\n";

        int choice = readInt("Select an option (0-6): ");
        switch (choice) {
            case 1: actionDispensePrescription(hms); break;
            case 2: actionViewQueue(hms); break;
            case 3: actionViewPatients(hms); break;
            case 4: actionSearchPatient(hms); break;
            case 5: actionConcurrencySimulation(hms); break;
            case 6: return true;  // Logout / Switch User
            case 0: return false; // Exit System
            default: std::cout << "\nInvalid choice. Please select 0 to 6.\n"; break;
        }
    }
}

// --- ADMIN PORTAL ---
static bool runAdminPortal(HospitalSystem& hms) {
    while (true) {
        std::cout << "\n=================================================================\n";
        std::cout << "     🏥 HMS [ADMIN PORTAL] — Logged In: " << currentUser << "\n";
        std::cout << "=================================================================\n";
        std::cout << " 1. Register New Patient\n";
        std::cout << " 2. Search Patient by ID\n";
        std::cout << " 3. View All Patients & Records\n";
        std::cout << " 4. View Hospital Staff & Doctors\n";
        std::cout << " 5. Book Doctor Appointment\n";
        std::cout << " 6. Issue Prescription\n";
        std::cout << " 7. Dispense Prescription\n";
        std::cout << " 8. View Pharmacy Queue\n";
        std::cout << " 9. Generate Discharge Bill (Operator Overload & Object Slicing)\n";
        std::cout << "10. Tax Assessment (Virtual Base Classes & Diamond Problem)\n";
        std::cout << "11. Run Full Automated OOP Demo\n";
        std::cout << "12. Run Multi-Threaded Simulation (Doctor-Pharmacist Concurrency)\n";
        std::cout << "13. Logout / Switch User\n";
        std::cout << " 0. Exit System\n";
        std::cout << "=================================================================\n";

        int choice = readInt("Select an option (0-13): ");
        switch (choice) {
            case 1:  actionRegisterPatient(hms); break;
            case 2:  actionSearchPatient(hms); break;
            case 3:  actionViewPatients(hms); break;
            case 4:  actionViewStaff(hms); break;
            case 5:  actionBookAppointment(hms); break;
            case 6:  actionIssuePrescription(hms); break;
            case 7:  actionDispensePrescription(hms); break;
            case 8:  actionViewQueue(hms); break;
            case 9:  actionDischargeBill(); break;
            case 10: actionTaxDemo(); break;
            case 11: hms.runFullOOPShowcase(); pauseForUser(); break;
            case 12: actionConcurrencySimulation(hms); break;
            case 13: return true;  // Logout / Switch User
            case 0:  return false; // Exit System
            default: std::cout << "\nInvalid choice. Please select 0 to 13.\n"; break;
        }
    }
}

// ============================================================================
// 6. MAIN FUNCTION
// ============================================================================
int main() {
    HospitalSystem hms;

    while (true) {
        // Step 1: Prompt Login First
        performLogin();

        // Step 2: Show ONLY the functions that the logged-in ID can use
        bool switchUser = false;
        if (currentRole == "DOCTOR") {
            switchUser = runDoctorPortal(hms);
        } else if (currentRole == "PHARMACIST") {
            switchUser = runPharmacistPortal(hms);
        } else {
            switchUser = runAdminPortal(hms);
        }

        // If user chose Exit (0), terminate the program
        if (!switchUser) {
            std::cout << "\nExiting Hospital Management System. Goodbye!\n";
            break;
        }
        // If switchUser is true (Logout), loop repeats and asks for login again
    }

    return 0;
}
