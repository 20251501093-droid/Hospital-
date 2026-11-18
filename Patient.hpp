#ifndef PATIENT_HPP
#define PATIENT_HPP

#include "Person.hpp"
#include <string>
#include <iostream>
#include <iomanip>
#include <atomic>

/**
 * ============================================================================
 * OOP CONCEPTS:
 * - Single Inheritance: Patient IS-A Person
 * - Static Members: static variable totalPatients, static constant HOSPITAL_BRANCH
 * - Nested Class: MedicalRecord inner class defined inside Patient
 * - Polymorphism: Overrides pure virtual getRole() and displayDetails()
 * - Method Overloading: bookAppointment() with 0 or 1 parameter
 * ============================================================================
 */
class Patient : public Person {
public:
    // ========================================================================
    // OOP CONCEPT: Nested / Inner Class
    // ========================================================================
    class MedicalRecord {
    private:
        std::string bloodGroup;
        std::string allergies;
        std::string diagnosis;

    public:
        MedicalRecord()
            : bloodGroup("Unknown"), allergies("None"), diagnosis("Pending Evaluation") {}

        MedicalRecord(std::string bloodGroup, std::string allergies, std::string diagnosis)
            : bloodGroup(std::move(bloodGroup)), allergies(std::move(allergies)), diagnosis(std::move(diagnosis)) {}

        std::string getBloodGroup() const { return bloodGroup; }
        std::string getAllergies() const { return allergies; }
        std::string getDiagnosis() const { return diagnosis; }

        void displayRecord() const {
            std::cout << "    [Medical Record] Blood Group: " << bloodGroup
                      << " | Allergies: " << allergies
                      << " | Diagnosis: " << diagnosis << "\n";
        }
    };

private:
    std::string patientId;
    int age;
    MedicalRecord medicalRecord;

    // C++17 inline static atomic variable (shared across all instances)
    inline static std::atomic<int> totalPatients{0};

public:
    // C++17 inline static constant
    inline static const std::string HOSPITAL_BRANCH = "AIIMS Healthcare Vadodara Campus";

    // Constructors
    Patient()
        : Person("Unassigned", "000-0000"), patientId("P000"), age(0), medicalRecord() {
        totalPatients++;
    }

    Patient(std::string name, std::string contactNumber, std::string patientId, int age,
            std::string bloodGroup = "O+", std::string allergies = "None", std::string diagnosis = "General Checkup")
        : Person(std::move(name), std::move(contactNumber)),
          patientId(std::move(patientId)),
          age(age),
          medicalRecord(std::move(bloodGroup), std::move(allergies), std::move(diagnosis)) {
        totalPatients++;
    }

    ~Patient() override = default;

    // Getters & Setters
    std::string getPatientId() const { return patientId; }
    int getAge() const { return age; }
    const MedicalRecord& getMedicalRecord() const { return medicalRecord; }

    void setAge(int newAge) {
        if (newAge >= 0 && newAge <= 150) age = newAge;
    }

    // Static member function
    static int getTotalPatients() {
        return totalPatients.load();
    }

    // Method Overloading #1 (Compile-time Polymorphism)
    void bookAppointment() const {
        std::cout << "  -> " << getName() << " (ID: " << patientId << ") requested appointment (Next Available Slot).\n";
    }

    // Method Overloading #2 (Compile-time Polymorphism)
    void bookAppointment(const std::string& preferredDate) const {
        std::cout << "  -> " << getName() << " (ID: " << patientId << ") requested appointment for: " << preferredDate << "\n";
    }

    // Runtime Polymorphism: Overriding getRole()
    std::string getRole() const override { return "Patient"; }

    // Runtime Polymorphism: Overriding displayDetails()
    void displayDetails() const override {
        std::cout << " [Patient] ID: " << std::left << std::setw(8) << patientId
                  << " | Name: " << std::setw(18) << getName()
                  << " | Age: " << std::setw(4) << age
                  << " | Contact: " << getContactNumber() << "\n";
        medicalRecord.displayRecord();
    }

    bool operator==(const Patient& other) const {
        return this->patientId == other.patientId;
    }
};

#endif // PATIENT_HPP
