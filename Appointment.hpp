#ifndef APPOINTMENT_HPP
#define APPOINTMENT_HPP

#include "Patient.hpp"
#include "Doctor.hpp"
#include <string>
#include <memory>
#include <iostream>
#include <iomanip>

/**
 * ============================================================================
 * OOP CONCEPT: Composition / Aggregation (HAS-A relationship)
 * ============================================================================
 * An Appointment HAS-A Patient and HAS-A Doctor.
 * Demonstrates object collaboration using modern smart pointers (std::shared_ptr).
 * Also demonstrates Operator Overloading with friend std::ostream& operator<<.
 */
class Appointment {
private:
    std::string appointmentId;
    std::shared_ptr<Patient> patient;
    std::shared_ptr<Doctor> doctor;
    std::string dateTime;
    std::string status;

public:
    Appointment(std::string appointmentId,
                std::shared_ptr<Patient> patient,
                std::shared_ptr<Doctor> doctor,
                std::string dateTime)
        : appointmentId(std::move(appointmentId)),
          patient(std::move(patient)),
          doctor(std::move(doctor)),
          dateTime(std::move(dateTime)),
          status("Scheduled") {}

    std::string getAppointmentId() const { return appointmentId; }
    std::shared_ptr<Patient> getPatient() const { return patient; }
    std::shared_ptr<Doctor> getDoctor() const { return doctor; }
    std::string getDateTime() const { return dateTime; }
    std::string getStatus() const { return status; }

    void updateStatus(const std::string& newStatus) {
        status = newStatus;
    }

    // Inline friend operator overloading for stream output
    inline friend std::ostream& operator<<(std::ostream& os, const Appointment& appt) {
        std::string pName = appt.patient ? appt.patient->getName() : "None";
        std::string dName = appt.doctor ? ("Dr. " + appt.doctor->getName()) : "None";

        os << "[Appointment #" << appt.appointmentId << "] "
           << "Patient: " << pName
           << " | Doctor: " << dName
           << " | Time: " << appt.dateTime
           << " | Status: " << appt.status;
        return os;
    }
};

#endif // APPOINTMENT_HPP
