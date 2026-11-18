#ifndef HOSPITAL_STAFF_HPP
#define HOSPITAL_STAFF_HPP

#include "Person.hpp"
#include <string>
#include <iostream>
#include <iomanip>

/**
 * ============================================================================
 * OOP CONCEPT: Multilevel Inheritance & Abstraction
 * ============================================================================
 * Level 1: Person (Abstract Base Class)
 * Level 2: HospitalStaff (Abstract Intermediate Class)
 * Level 3: Receptionist, Pharmacist, LabTechnician (Concrete Derived Classes)
 */
class HospitalStaff : public Person {
protected:
    std::string staffId;
    std::string department;

public:
    HospitalStaff()
        : Person("Unassigned", "000-0000"), staffId("STF-000"), department("General") {}

    HospitalStaff(std::string name, std::string contactNumber, std::string staffId, std::string department)
        : Person(std::move(name), std::move(contactNumber)),
          staffId(std::move(staffId)),
          department(std::move(department)) {}

    ~HospitalStaff() override = default;

    std::string getStaffId() const { return staffId; }
    std::string getDepartment() const { return department; }
};

// ============================================================================
// CONCRETE SUBCLASS 1: Receptionist
// ============================================================================
class Receptionist : public HospitalStaff {
private:
    int deskNumber;

public:
    Receptionist()
        : HospitalStaff("Receptionist", "000-0000", "STF-REC-01", "Front Desk"), deskNumber(1) {}

    Receptionist(std::string name, std::string contactNumber, std::string staffId,
                 std::string department, int deskNumber)
        : HospitalStaff(std::move(name), std::move(contactNumber), std::move(staffId), std::move(department)),
          deskNumber(deskNumber) {}

    int getDeskNumber() const { return deskNumber; }

    void scheduleToken(const std::string& patientName) const {
        std::cout << "  -> Receptionist " << getName() << " issued queue token for "
                  << patientName << " at Desk #" << deskNumber << ".\n";
    }

    std::string getRole() const override { return "Receptionist"; }

    void displayDetails() const override {
        std::cout << " [Receptionist] ID: " << std::left << std::setw(8) << staffId
                  << " | Name: " << std::setw(16) << getName()
                  << " | Dept: " << std::setw(14) << department
                  << " | Desk: #" << deskNumber << "\n";
    }
};

// ============================================================================
// CONCRETE SUBCLASS 2: Pharmacist
// ============================================================================
class Pharmacist : public HospitalStaff {
private:
    std::string licenseNumber;

public:
    Pharmacist()
        : HospitalStaff("Pharmacist", "000-0000", "STF-PHR-01", "Pharmacy"), licenseNumber("LIC-DEFAULT") {}

    Pharmacist(std::string name, std::string contactNumber, std::string staffId,
               std::string department, std::string licenseNumber)
        : HospitalStaff(std::move(name), std::move(contactNumber), std::move(staffId), std::move(department)),
          licenseNumber(std::move(licenseNumber)) {}

    std::string getLicenseNumber() const { return licenseNumber; }

    void dispenseMedication(const std::string& medicationName) const {
        std::cout << "  -> Pharmacist " << getName() << " [Lic: " << licenseNumber
                  << "] dispensed medication: [" << medicationName << "].\n";
    }

    std::string getRole() const override { return "Pharmacist"; }

    void displayDetails() const override {
        std::cout << " [Pharmacist]   ID: " << std::left << std::setw(8) << staffId
                  << " | Name: " << std::setw(16) << getName()
                  << " | Dept: " << std::setw(14) << department
                  << " | License: " << licenseNumber << "\n";
    }
};

// ============================================================================
// CONCRETE SUBCLASS 3: LabTechnician
// ============================================================================
class LabTechnician : public HospitalStaff {
private:
    std::string labSpecialty;

public:
    LabTechnician()
        : HospitalStaff("LabTech", "000-0000", "STF-LAB-01", "Pathology"), labSpecialty("Clinical Biochemistry") {}

    LabTechnician(std::string name, std::string contactNumber, std::string staffId,
                  std::string department, std::string labSpecialty)
        : HospitalStaff(std::move(name), std::move(contactNumber), std::move(staffId), std::move(department)),
          labSpecialty(std::move(labSpecialty)) {}

    std::string getLabSpecialty() const { return labSpecialty; }

    void conductTest(const std::string& testName, const std::string& patientName) const {
        std::cout << "  -> Lab Tech " << getName() << " conducted [" << testName
                  << "] test for patient " << patientName << " in " << labSpecialty << " lab.\n";
    }

    std::string getRole() const override { return "Lab Technician"; }

    void displayDetails() const override {
        std::cout << " [Lab Tech]     ID: " << std::left << std::setw(8) << staffId
                  << " | Name: " << std::setw(16) << getName()
                  << " | Dept: " << std::setw(14) << department
                  << " | Specialty: " << labSpecialty << "\n";
    }
};

#endif // HOSPITAL_STAFF_HPP
