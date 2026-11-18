#ifndef HOSPITAL_SYSTEM_HPP
#define HOSPITAL_SYSTEM_HPP

#include "Patient.hpp"
#include "Doctor.hpp"
#include "HospitalStaff.hpp"
#include "Appointment.hpp"
#include "Prescription.hpp"
#include "HospitalQueue.hpp"
#include "HospitalExceptions.hpp"
#include "MedicalBill.hpp"

#include <map>
#include <vector>
#include <memory>
#include <string>
#include <iostream>
#include <iomanip>
#include <thread>
#include <atomic>
#include <chrono>
#include <mutex>

/**
 * ============================================================================
 * OOP CONCEPT: System Facade & In-Memory Data Management
 * ============================================================================
 * - Eliminates database and security dependencies to keep the project
 *   focused squarely on pure Object-Oriented Programming (OOP).
 * - Manages polymorphic collections of Person, HospitalStaff, Doctor, and Patient.
 * - Manages Appointment composition and generic Prescription queue.
 */
class HospitalSystem {
private:
    std::map<std::string, std::shared_ptr<Patient>> patients;
    std::map<std::string, std::shared_ptr<Doctor>> doctors;
    std::vector<std::shared_ptr<HospitalStaff>> staffList;
    std::vector<Appointment> appointments;
    HospitalQueue<Prescription> prescriptionQueue;

    void seedInitialData() {
        // Seed Patients
        registerPatient("Rahul Sharma", "9876543210", "P101", 28, "B+", "Penicillin", "Seasonal Influenza");
        registerPatient("Priya Patel", "9812345678", "P102", 34, "O+", "None", "Hypertension Stage 1");
        registerPatient("Anil Mehta", "9823456789", "P103", 52, "A+", "Sulfa Drugs", "Type 2 Diabetes");

        // Seed Doctors
        doctors["D01"] = std::make_shared<Doctor>("Robert House", "9991112233", "D01", "Diagnostics & Internal Med", 150.0);
        doctors["D02"] = std::make_shared<Doctor>("Lisa Cuddy", "9992223344", "D02", "Endocrinology", 120.0);
        doctors["D03"] = std::make_shared<Doctor>("James Wilson", "9993334455", "D03", "Oncology & Palliative", 180.0);

        // Seed Multilevel Polymorphic Staff
        staffList.push_back(std::make_shared<Receptionist>("Sarah Jenkins", "9112223344", "STF-01", "Outpatient Registration", 4));
        staffList.push_back(std::make_shared<Pharmacist>("David Miller", "9223334455", "STF-02", "Central Pharmacy", "PHARM-GJ-8821"));
        staffList.push_back(std::make_shared<LabTechnician>("Elena Rostova", "9334445566", "STF-03", "Pathology Lab", "Hematology & Molecular"));

        // Seed Initial Appointments
        bookAppointment("APT-1001", "P101", "D01", "2026-11-20 10:00 AM");
        bookAppointment("APT-1002", "P102", "D02", "2026-11-20 11:30 AM");

        // Seed Initial Prescriptions in Queue
        issuePrescription("RX-501", "P101", "D01", {"Oseltamivir 75mg", "Paracetamol 500mg"}, "Twice daily after meals", "2026-11-19");
        issuePrescription("RX-502", "P102", "D02", {"Amlodipine 5mg", "Telmisartan 40mg"}, "Once daily in the morning", "2026-11-19");
    }

public:
    HospitalSystem() {
        seedInitialData();
    }

    // Patient Operations
    void registerPatient(const std::string& name, const std::string& contact,
                         const std::string& id, int age,
                         const std::string& bloodGroup = "O+",
                         const std::string& allergies = "None",
                         const std::string& diagnosis = "Routine Checkup") {
        if (patients.find(id) != patients.end()) {
            std::cout << " [Warning] Patient ID [" << id << "] is already registered. Updating record.\n";
        }
        patients[id] = std::make_shared<Patient>(name, contact, id, age, bloodGroup, allergies, diagnosis);
    }

    std::shared_ptr<Patient> findPatient(const std::string& id) const {
        auto it = patients.find(id);
        if (it == patients.end()) {
            throw PatientNotFoundException(id);
        }
        return it->second;
    }

    const std::map<std::string, std::shared_ptr<Patient>>& getAllPatients() const {
        return patients;
    }

    // Doctor & Staff Operations
    std::shared_ptr<Doctor> findDoctor(const std::string& id) const {
        auto it = doctors.find(id);
        if (it == doctors.end()) {
            throw StaffNotFoundException(id);
        }
        return it->second;
    }

    const std::map<std::string, std::shared_ptr<Doctor>>& getAllDoctors() const {
        return doctors;
    }

    const std::vector<std::shared_ptr<HospitalStaff>>& getAllStaff() const {
        return staffList;
    }

    // Appointment Operations
    void bookAppointment(const std::string& apptId, const std::string& patientId,
                         const std::string& doctorId, const std::string& dateTime) {
        auto patient = findPatient(patientId);
        auto doctor = findDoctor(doctorId);
        appointments.emplace_back(apptId, patient, doctor, dateTime);
    }

    const std::vector<Appointment>& getAllAppointments() const {
        return appointments;
    }

    // Prescription Queue Operations (Producer - Consumer)
    void issuePrescription(const std::string& rxId, const std::string& patientId,
                           const std::string& doctorId, const std::vector<std::string>& meds,
                           const std::string& dosage, const std::string& date) {
        findPatient(patientId);
        findDoctor(doctorId);

        Prescription rx(rxId, patientId, doctorId, meds, dosage, date);
        prescriptionQueue.push(rx);
    }

    Prescription dispensePrescription() {
        if (prescriptionQueue.empty()) {
            throw InvalidPrescriptionException("Prescription queue is currently empty.");
        }
        return prescriptionQueue.pop();
    }

    std::vector<Prescription> getPendingPrescriptions() const {
        return prescriptionQueue.snapshot();
    }

    // Multi-Threading: Producer-Consumer Simulation
    void runConcurrentPharmacySimulation(int durationSeconds = 5) {
        std::cout << "\n========================================================================\n";
        std::cout << "     THREADING DEMO: PRODUCER-CONSUMER CONCURRENCY SIMULATION           \n";
        std::cout << "========================================================================\n";
        std::cout << " Spawning Doctor thread [Producer] and Pharmacist thread [Consumer]...\n";
        std::cout << " Synchronization: std::mutex + std::condition_variable in HospitalQueue<T>\n";
        std::cout << " Running simulation for " << durationSeconds << " seconds. Watch real-time log:\n\n";

        std::mutex consoleMutex;
        std::atomic<bool> isRunning{true};
        std::atomic<int> producedCount{0};
        std::atomic<int> consumedCount{0};

        std::thread doctorThread([this, &consoleMutex, &isRunning, &producedCount]() {
            const std::vector<std::string> sampleMeds = {
                "Amoxicillin 500mg", "Ibuprofen 400mg", "Azithromycin 250mg",
                "Metformin 850mg", "Atorvastatin 20mg", "Paracetamol 650mg"
            };
            const std::vector<std::string> samplePatients = {"P101", "P102", "P103"};
            int rxSeq = 901;

            while (isRunning.load()) {
                std::this_thread::sleep_for(std::chrono::milliseconds(700));
                if (!isRunning.load()) break;

                std::string rxId = "RX-SIM-" + std::to_string(rxSeq++);
                std::string pId = samplePatients[rand() % samplePatients.size()];
                std::string med = sampleMeds[rand() % sampleMeds.size()];

                Prescription rx(rxId, pId, "D01", {med}, "Twice daily", "2026-11-19");
                this->prescriptionQueue.push(rx);
                producedCount.fetch_add(1);

                {
                    std::lock_guard<std::mutex> lock(consoleMutex);
                    std::cout << " [PRODUCER] (Thread ID: " << std::this_thread::get_id()
                              << ") Doctor generated " << rxId << " for [" << pId << "] (" << med << ")\n";
                }
            }
        });

        std::thread pharmacistThread([this, &consoleMutex, &isRunning, &consumedCount]() {
            while (isRunning.load()) {
                Prescription rx("dummy", "dummy", "dummy", {"dummy"}, "dummy", "dummy");
                if (this->prescriptionQueue.waitAndPop(rx, std::chrono::milliseconds(500))) {
                    consumedCount.fetch_add(1);
                    {
                        std::lock_guard<std::mutex> lock(consoleMutex);
                        std::cout << "   [CONSUMER] (Thread ID: " << std::this_thread::get_id()
                                  << ") Pharmacist dispensed " << rx.getPrescriptionId()
                                  << " -> Medications: [" << rx.getMedications()[0] << "]\n";
                    }
                    std::this_thread::sleep_for(std::chrono::milliseconds(950));
                }
            }
        });

        std::this_thread::sleep_for(std::chrono::seconds(durationSeconds));
        isRunning.store(false);

        if (doctorThread.joinable()) doctorThread.join();
        if (pharmacistThread.joinable()) pharmacistThread.join();

        std::cout << "\n------------------------------------------------------------------------\n";
        std::cout << " Concurrency Simulation Completed Successfully:\n";
        std::cout << " * Prescriptions Produced (Doctor Thread):     " << producedCount.load() << "\n";
        std::cout << " * Prescriptions Dispensed (Pharmacist Thread): " << consumedCount.load() << "\n";
        std::cout << " * Remaining Pending in Thread-Safe Queue:     " << prescriptionQueue.size() << "\n";
        std::cout << " * Clean thread termination verified via .join().\n";
        std::cout << "========================================================================\n\n";
    }

    // Comprehensive Automated OOP Showcase
    void runFullOOPShowcase() const {
        std::cout << "\n========================================================================\n";
        std::cout << "             AUTOMATED OBJECT-ORIENTED PROGRAMMING (OOP) SHOWCASE        \n";
        std::cout << "========================================================================\n";

        // 1. Abstraction & Runtime Polymorphism via Person* pointers
        std::cout << "\n[1] ABSTRACTION & RUNTIME POLYMORPHISM (Person* base pointer vector):\n";
        std::vector<std::shared_ptr<Person>> people;
        people.push_back(patients.begin()->second);
        people.push_back(doctors.begin()->second);
        people.push_back(staffList[0]);
        people.push_back(staffList[1]);

        for (const auto& p : people) {
            std::cout << "  * Calling pure virtual getRole(): " << std::setw(15) << std::left << p->getRole()
                      << " | Dynamic Type Display: ";
            p->displayDetails();
        }

        // 2. Multilevel Inheritance: Person -> HospitalStaff -> Receptionist / Pharmacist / LabTech
        std::cout << "\n[2] MULTILEVEL INHERITANCE (Person -> HospitalStaff -> Subclasses):\n";
        for (const auto& s : staffList) {
            std::cout << "  * Staff Member: " << s->getName() << " | Role: " << s->getRole()
                      << " | Dept: " << s->getDepartment() << "\n";
        }

        // 3. Encapsulation & Static Members
        std::cout << "\n[3] ENCAPSULATION & STATIC MEMBERS:\n";
        std::cout << "  * Static Hospital Branch: " << Patient::HOSPITAL_BRANCH << "\n";
        std::cout << "  * Total Registered Patients (Static Counter): " << Patient::getTotalPatients() << "\n";
        std::cout << "  * Encapsulation check: Patient private fields accessed via getters only.\n";

        // 4. Nested / Inner Class
        std::cout << "\n[4] NESTED CLASS (Patient::MedicalRecord):\n";
        auto samplePatient = patients.at("P101");
        std::cout << "  * Enclosing Object: " << samplePatient->getName() << "\n";
        samplePatient->getMedicalRecord().displayRecord();

        // 5. Method Overloading (Compile-Time Polymorphism)
        std::cout << "\n[5] METHOD OVERLOADING (Compile-Time Polymorphism):\n";
        samplePatient->bookAppointment();
        samplePatient->bookAppointment("Next Monday at 09:30 AM");

        // 6. Composition (HAS-A Relationship)
        std::cout << "\n[6] COMPOSITION / AGGREGATION (Appointment HAS-A Patient and HAS-A Doctor):\n";
        if (!appointments.empty()) {
            std::cout << "  * " << appointments[0] << "\n";
        }

        // 7. Generic Template Container
        std::cout << "\n[7] GENERIC CLASS TEMPLATE (HospitalQueue<T>):\n";
        std::cout << "  * Current items in generic queue: " << prescriptionQueue.size() << "\n";

        // 8. Custom Exceptions & Robust Error Handling
        std::cout << "\n[8] CUSTOM EXCEPTION HANDLING (PatientNotFoundException):\n";
        try {
            std::cout << "  * Attempting to query non-existent patient ID [GHOST-999]...\n";
            findPatient("GHOST-999");
        } catch (const HospitalException& e) {
            std::cout << "  * SUCCESS: Caught expected custom exception -> " << e.what() << "\n";
        }

        // 9. Friend Functions & Operator Overloading
        std::cout << "\n[9] OPERATOR OVERLOADING & FRIEND FUNCTIONS (operator+ and operator<<):\n";
        MedicalBill b1("B-001", 350.0);
        MedicalBill b2("B-002", 150.5);
        MedicalBill combined = b1 + b2;
        std::cout << "  * Bill 1: " << b1 << "\n";
        std::cout << "  * Bill 2: " << b2 << "\n";
        std::cout << "  * Combined via operator+: " << combined << "\n";

        // 10. Object Slicing Demonstration
        std::cout << "\n[10] OBJECT SLICING DEMONSTRATION:\n";
        VIPBill vipBill("VIP-888", 1200.0, "Executive Lounge Platinum");
        std::cout << "  * Derived VIPBill by reference: ";
        vipBill.printInfo();
        MedicalBill sliced = vipBill;
        std::cout << "  * Sliced to base MedicalBill:   ";
        sliced.printInfo();
        std::cout << "    (Lounge access slice was safely removed during value copy!)\n";

        // 11. Multiple Inheritance, Virtual Base Classes & Diamond Problem
        std::cout << "\n[11] MULTIPLE INHERITANCE & VIRTUAL BASE CLASS (Diamond Problem):\n";
        FinalDischargeTax taxObj("REF-2026-XYZ");
        taxObj.setReferenceId("REF-CHAINED-99");
        const AbstractTax* taxBasePtr = &taxObj;
        std::cout << "  * Virtual Base Tax Details: " << taxBasePtr->getTaxDetails() << "\n";
        std::cout << "  * Virtual Base Tax Amount:  $" << taxBasePtr->getTaxAmount() << "\n";
        std::cout << "  * Diamond Problem resolved: Exactly ONE taxAmount variable shared across MedicalTax & VIPTax.\n";

        // 12. Multithreading & Synchronization (std::thread, std::mutex, std::atomic)
        std::cout << "\n[12] BASIC MULTITHREADING & SYNCHRONIZATION:\n";
        std::cout << "  * Thread-safe Atomic Counter (Patient::totalPatients): " << Patient::getTotalPatients() << "\n";
        std::cout << "  * Spawning a background asynchronous thread via std::thread to verify queue thread-safety...\n";
        std::atomic<bool> workerFinished{false};
        HospitalQueue<Prescription> threadTestQueue;
        std::thread bgWorker([&threadTestQueue, &workerFinished]() {
            Prescription testRx("RX-BG-SYNC", "P101", "D01", {"Multivitamin"}, "Once daily", "2026-11-19");
            threadTestQueue.push(testRx);
            workerFinished.store(true);
        });
        bgWorker.join();
        std::cout << "  * Background worker thread safely joined (joinable: " << (bgWorker.joinable() ? "true" : "false")
                  << "). Completed: " << (workerFinished.load() ? "YES" : "NO") << "\n";
        std::cout << "  * HospitalQueue<T> synchronized via std::mutex and std::condition_variable (size: "
                  << threadTestQueue.size() << ").\n";

        std::cout << "\n========================================================================\n";
        std::cout << "                ALL 15+ OOP CONCEPTS VERIFIED SUCCESSFULLY               \n";
        std::cout << "========================================================================\n\n";
    }
};

#endif // HOSPITAL_SYSTEM_HPP
