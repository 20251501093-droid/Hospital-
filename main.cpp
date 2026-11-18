#include "HospitalSystem.hpp"
#include "MedicalBill.hpp"
#include "HospitalExceptions.hpp"

#include <iostream>
#include <string>
#include <vector>
#include <limits>
#include <memory>
#include <sstream>

// ============================================================================
// SIMPLE ROLE-BASED ACCESS CONTROL (Credentials stored in simple variables)
// ============================================================================
const std::string ADMIN_USER = "admin";
const std::string ADMIN_PASS = "admin123";

const std::string DOCTOR_USER = "dr_house";
const std::string DOCTOR_PASS = "doc123";

const std::string PHARMACIST_USER = "tech_lisa";
const std::string PHARMACIST_PASS = "tech123";

// Current active session variables
std::string currentUser = "Administrator";
std::string currentRole = "ADMIN";

// Helper to safely read a line of text from console
static std::string readLine(const std::string& prompt) {
    std::cout << prompt;
    std::string line;
    std::getline(std::cin, line);
    return line;
}

// Simple login function: compares directly with stored variables
static void performLogin() {
    std::cout << "\n=================================================================\n";
    std::cout << "                 🔐 HMS TERMINAL LOGIN                           \n";
    std::cout << "=================================================================\n";
    std::cout << " Default Stored Credentials:\n";
    std::cout << "   - Admin:      admin     / admin123  (Full Access)\n";
    std::cout << "   - Doctor:     dr_house  / doc123    (Patients & Prescriptions)\n";
    std::cout << "   - Pharmacist: tech_lisa / tech123   (Pharmacy & Dispensing)\n";
    std::cout << "=================================================================\n";

    while (true) {
        std::string user = readLine("Enter Username: ");
        std::string pass = readLine("Enter Password: ");

        // Simple variable comparison
        if (user == ADMIN_USER && pass == ADMIN_PASS) {
            currentUser = "Administrator";
            currentRole = "ADMIN";
            std::cout << "\n✓ Login Successful! Welcome " << currentUser << " [Role: ADMIN]\n";
            break;
        } else if (user == DOCTOR_USER && pass == DOCTOR_PASS) {
            currentUser = "Dr. Robert House";
            currentRole = "DOCTOR";
            std::cout << "\n✓ Login Successful! Welcome " << currentUser << " [Role: DOCTOR]\n";
            break;
        } else if (user == PHARMACIST_USER && pass == PHARMACIST_PASS) {
            currentUser = "Lisa (Head Pharmacist)";
            currentRole = "PHARMACIST";
            std::cout << "\n✓ Login Successful! Welcome " << currentUser << " [Role: PHARMACIST]\n";
            break;
        } else {
            std::cout << "\n❌ [Invalid Username or Password] Please try again.\n\n";
        }
    }
}

// Simple role permission checker: ADMIN has all access, others check role match
static bool hasAccess(const std::string& requiredRole) {
    if (currentRole == "ADMIN" || currentRole == requiredRole) {
        return true;
    }
    std::cout << "\n⛔ [ACCESS DENIED] Current Role [" << currentRole 
              << "] is not authorized for this action.\n"
              << "   Required Role: [" << requiredRole << "]\n";
    return false;
}

// Helper to safely read an integer from console
static int readInt(const std::string& prompt) {
    while (true) {
        std::cout << prompt;
        std::string line;
        std::getline(std::cin, line);
        try {
            return std::stoi(line);
        } catch (...) {
            std::cout << " [Error] Please enter a valid integer.\n";
        }
    }
}

// Helper to safely read a double from console
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

static void printBanner() {
    std::cout << "\n=================================================================\n";
    std::cout << "     🏥 HOSPITAL MANAGEMENT SYSTEM (C++ OOP TERMINAL EDITION)    \n";
    std::cout << "        Logged In: " << currentUser << " | Role: [" << currentRole << "]\n";
    std::cout << "=================================================================\n";
    std::cout << " 1. Register Patient              (Admin / Doctor)\n";
    std::cout << " 2. Search Patient by ID          (All Roles)\n";
    std::cout << " 3. View All Patients & Records   (All Roles)\n";
    std::cout << " 4. View Hospital Staff & Doctors (All Roles)\n";
    std::cout << " 5. Book Doctor Appointment       (Admin / Doctor)\n";
    std::cout << " 6. Issue Prescription            (Doctor Only)\n";
    std::cout << " 7. Dispense Prescription         (Pharmacist Only)\n";
    std::cout << " 8. Generate Discharge Bill       (Admin Only)\n";
    std::cout << " 9. Tax Assessment & Diamond Demo (Admin Only)\n";
    std::cout << "10. Run Full Automated OOP Demo   (All Roles)\n";
    std::cout << "11. Run Multi-Threaded Simulation (All Roles)\n";
    std::cout << "12. Switch User / Logout          (Change Active Role)\n";
    std::cout << " 0. Exit System\n";
    std::cout << "=================================================================\n";
}

int main() {
    HospitalSystem hms;

    // Initial Login at startup
    performLogin();

    while (true) {
        printBanner();
        int choice = readInt("Select an option (0-12): ");

        switch (choice) {
            case 1: {
                if (!hasAccess("DOCTOR")) break; // Admin or Doctor can register
                std::cout << "\n--- [1] Register New Patient ---\n";
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
                break;
            }

            case 2: {
                std::cout << "\n--- [2] Search Patient by ID ---\n";
                std::cout << "Hint: Try existing IDs (P101, P102, P103) or enter 'GHOST-99' to trigger custom exception.\n";
                std::string id = readLine("Enter Patient ID to search: ");
                try {
                    auto patient = hms.findPatient(id);
                    std::cout << "\n[Match Found]\n";
                    patient->displayDetails();
                } catch (const PatientNotFoundException& e) {
                    std::cout << "\n[Exception Caught]: " << e.what() << "\n";
                }
                break;
            }

            case 3: {
                std::cout << "\n--- [3] All Registered Patients & Nested Medical Records ---\n";
                std::cout << "Hospital Branch (Static Const): " << Patient::HOSPITAL_BRANCH << "\n";
                std::cout << "Total Count (Static Variable):  " << Patient::getTotalPatients() << "\n\n";

                const auto& patients = hms.getAllPatients();
                for (const auto& pair : patients) {
                    pair.second->displayDetails();
                    std::cout << "-----------------------------------------------------------------\n";
                }
                break;
            }

            case 4: {
                std::cout << "\n--- [4] Hospital Staff & Doctors (Runtime Polymorphism) ---\n";
                std::cout << "Demonstrating dynamic dispatch through base Person* pointers:\n\n";

                std::cout << ">> DOCTORS <<\n";
                for (const auto& pair : hms.getAllDoctors()) {
                    pair.second->displayDetails();
                }

                std::cout << "\n>> MULTILEVEL STAFF (Receptionists, Pharmacists, Lab Technicians) <<\n";
                for (const auto& staff : hms.getAllStaff()) {
                    staff->displayDetails();
                }
                break;
            }

            case 5: {
                if (!hasAccess("DOCTOR")) break; // Admin or Doctor
                std::cout << "\n--- [5] Book Doctor Appointment (Composition HAS-A) ---\n";
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
                break;
            }

            case 6: {
                if (!hasAccess("DOCTOR")) break; // Doctor or Admin
                std::cout << "\n--- [6] Issue Prescription (Producer to Generic Queue) ---\n";
                std::string rxId = "RX-" + std::to_string(600 + (rand() % 300));
                std::cout << "Prescription ID: " << rxId << "\n";
                std::string pId = readLine("Enter Patient ID (e.g., P101): ");
                std::string dId = readLine("Enter Doctor ID (e.g., D01): ");
                std::string medInput = readLine("Enter Medications (comma-separated, e.g., Amoxicillin 500mg, Ibuprofen 400mg): ");
                std::string dosage = readLine("Enter Dosage Instructions: ");
                std::string date = readLine("Enter Date Issued (e.g., 2026-11-19): ");

                std::vector<std::string> meds;
                std::stringstream ss(medInput);
                std::string item;
                while (std::getline(ss, item, ',')) {
                    // trim whitespace
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
                break;
            }

            case 7: {
                if (!hasAccess("PHARMACIST")) break; // Pharmacist or Admin
                std::cout << "\n--- [7] Dispense Prescription (Consumer Queue) ---\n";
                try {
                    Prescription dispensed = hms.dispensePrescription();
                    std::cout << "✓ Pharmacist successfully popped & dispensed prescription:\n";
                    std::cout << "  " << dispensed << "\n";
                    std::cout << "✓ Remaining in Queue: " << hms.getPendingPrescriptions().size() << "\n";
                } catch (const InvalidPrescriptionException& e) {
                    std::cout << "[Queue Status]: " << e.what() << "\n";
                }
                break;
            }

            case 8: {
                if (!hasAccess("ADMIN")) break; // Admin Only
                std::cout << "\n--- [8] Discharge Billing & Object Slicing Demonstration ---\n";
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
                break;
            }

            case 9: {
                if (!hasAccess("ADMIN")) break; // Admin Only
                std::cout << "\n--- [9] Diamond Inheritance & Virtual Base Classes Demo ---\n";
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
                break;
            }

            case 10: {
                hms.runFullOOPShowcase();
                break;
            }

            case 11: {
                int secs = readInt("Enter simulation duration in seconds (recommended 3-6): ");
                if (secs <= 0) secs = 4;
                hms.runConcurrentPharmacySimulation(secs);
                break;
            }

            case 12: {
                performLogin();
                break;
            }

            case 0: {
                std::cout << "\nExiting Hospital Management System. Goodbye!\n";
                return 0;
            }

            default: {
                std::cout << "\nInvalid choice. Please select an option between 0 and 12.\n";
                break;
            }
        }

        std::cout << "\nPress Enter to return to main menu...";
        std::cin.get();
    }

    return 0;
}
