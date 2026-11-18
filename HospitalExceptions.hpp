#ifndef HOSPITAL_EXCEPTIONS_HPP
#define HOSPITAL_EXCEPTIONS_HPP

#include <exception>
#include <string>

/**
 * ============================================================================
 * OOP CONCEPT: Custom Exception Hierarchy
 * ============================================================================
 * - Base custom exception inherits from std::exception
 * - Derived specialized exceptions provide domain-specific error details
 * - Demonstrates inheritance, exception handling, and overriding what()
 */
class HospitalException : public std::exception {
protected:
    std::string message;

public:
    explicit HospitalException(std::string msg) : message(std::move(msg)) {}
    
    const char* what() const noexcept override {
        return message.c_str();
    }
};

class PatientNotFoundException : public HospitalException {
public:
    explicit PatientNotFoundException(const std::string& id)
        : HospitalException("PatientNotFoundException: No patient found with ID [" + id + "]") {}
};

class InvalidPrescriptionException : public HospitalException {
public:
    explicit InvalidPrescriptionException(const std::string& reason)
        : HospitalException("InvalidPrescriptionException: " + reason) {}
};

class StaffNotFoundException : public HospitalException {
public:
    explicit StaffNotFoundException(const std::string& id)
        : HospitalException("StaffNotFoundException: No hospital staff found with ID [" + id + "]") {}
};

#endif // HOSPITAL_EXCEPTIONS_HPP
