#ifndef DOCTOR_HPP
#define DOCTOR_HPP

#include "Person.hpp"
#include <string>
#include <iostream>
#include <iomanip>

/**
 * ============================================================================
 * OOP CONCEPT: Inheritance & Polymorphism
 * ============================================================================
 * - Doctor IS-A Person
 * - Overrides getRole() and displayDetails()
 * - Encapsulates doctor credentials and consultation fee
 */
class Doctor : public Person {
private:
    std::string doctorId;
    std::string specialization;
    double consultationFee;

public:
    Doctor()
        : Person("Unassigned", "000-0000"),
          doctorId("DOC-000"),
          specialization("General Medicine"),
          consultationFee(0.0) {}

    Doctor(std::string name, std::string contactNumber, std::string doctorId,
           std::string specialization, double consultationFee)
        : Person(std::move(name), std::move(contactNumber)),
          doctorId(std::move(doctorId)),
          specialization(std::move(specialization)),
          consultationFee(consultationFee) {}

    ~Doctor() override = default;

    std::string getDoctorId() const { return doctorId; }
    std::string getSpecialization() const { return specialization; }
    double getConsultationFee() const { return consultationFee; }

    void prescribeMedication(const std::string& patientName, const std::string& medication) const {
        std::cout << "  -> Dr. " << getName() << " (" << specialization
                  << ") prescribed [" << medication << "] for " << patientName
                  << ". Consultation Fee: $" << std::fixed << std::setprecision(2) << consultationFee << "\n";
    }

    // Runtime Polymorphism overrides
    std::string getRole() const override { return "Doctor"; }

    void displayDetails() const override {
        std::cout << " [Doctor]  ID: " << std::left << std::setw(8) << doctorId
                  << " | Name: " << std::setw(18) << ("Dr. " + getName())
                  << " | Spec: " << std::setw(18) << specialization
                  << " | Fee: $" << std::fixed << std::setprecision(2) << consultationFee << "\n";
    }
};

#endif // DOCTOR_HPP
