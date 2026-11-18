#ifndef PRESCRIPTION_HPP
#define PRESCRIPTION_HPP

#include "HospitalExceptions.hpp"
#include <string>
#include <vector>
#include <iostream>

/**
 * ============================================================================
 * OOP CONCEPT: Immutable Class & Const-Correctness
 * ============================================================================
 * - Immutable Object Pattern: Private fields, no setters, strict constructor validation
 * - Thread-safe by design for Producer (Doctor) and Consumer (Pharmacist)
 */
class Prescription {
private:
    std::string prescriptionId;
    std::string patientId;
    std::string doctorId;
    std::vector<std::string> medications;
    std::string dosage;
    std::string dateIssued;

public:
    Prescription(std::string prescriptionId,
                 std::string patientId,
                 std::string doctorId,
                 std::vector<std::string> medications,
                 std::string dosage,
                 std::string dateIssued)
        : prescriptionId(std::move(prescriptionId)),
          patientId(std::move(patientId)),
          doctorId(std::move(doctorId)),
          medications(std::move(medications)),
          dosage(std::move(dosage)),
          dateIssued(std::move(dateIssued)) {
        if (this->medications.empty()) {
            throw InvalidPrescriptionException("Prescription must contain at least one medication.");
        }
        if (this->prescriptionId.empty() || this->patientId.empty()) {
            throw InvalidPrescriptionException("Prescription and Patient IDs cannot be empty.");
        }
    }

    std::string getPrescriptionId() const { return prescriptionId; }
    std::string getPatientId() const { return patientId; }
    std::string getDoctorId() const { return doctorId; }
    const std::vector<std::string>& getMedications() const { return medications; }
    std::string getDosage() const { return dosage; }
    std::string getDateIssued() const { return dateIssued; }

    inline friend std::ostream& operator<<(std::ostream& os, const Prescription& rx) {
        os << "Rx#" << rx.prescriptionId << " | Patient ID: " << rx.patientId
           << " | Doctor ID: " << rx.doctorId << " | Meds: [";
        for (size_t i = 0; i < rx.medications.size(); ++i) {
            os << rx.medications[i];
            if (i + 1 < rx.medications.size()) os << ", ";
        }
        os << "] | Dosage: " << rx.dosage << " | Date: " << rx.dateIssued;
        return os;
    }
};

#endif // PRESCRIPTION_HPP
