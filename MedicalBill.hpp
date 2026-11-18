#ifndef MEDICALBILL_HPP
#define MEDICALBILL_HPP

#include <string>
#include <iostream>
#include <sstream>
#include <iomanip>

/**
 * ============================================================================
 * OOP CONCEPTS DEMONSTRATED IN THIS MODULE:
 * 1. Operator Overloading: operator+, operator<<
 * 2. Friend Functions: Non-member functions granted access to private/protected state
 * 3. Object Slicing: VIPBill sliced to MedicalBill by value vs preserved by pointer
 * 4. Multiple Inheritance & Virtual Base Classes: Resolving the Diamond Problem
 * 5. Dynamic Dispatch & Virtual Functions: AbstractTax hierarchy
 * 6. "this" Pointer: Method chaining pattern returning this
 * ============================================================================
 */

class MedicalBill {
protected:
    std::string billId;
    double amount;

public:
    MedicalBill() : billId("B000"), amount(0.0) {}
    MedicalBill(std::string id, double amt) : billId(std::move(id)), amount(amt) {}
    virtual ~MedicalBill() = default;

    std::string getBillId() const { return billId; }
    double getAmount() const { return amount; }
    void setAmount(double amt) { amount = amt; }

    // Operator Overloading via Friend Functions
    inline friend MedicalBill operator+(const MedicalBill& b1, const MedicalBill& b2) {
        return MedicalBill(b1.billId + "+" + b2.billId, b1.amount + b2.amount);
    }

    inline friend std::ostream& operator<<(std::ostream& os, const MedicalBill& bill) {
        os << "BILL [" << bill.billId << "] | Total Amount: $"
           << std::fixed << std::setprecision(2) << bill.amount;
        return os;
    }

    MedicalBill add(const MedicalBill& other) const {
        return *this + other;
    }

    virtual void printInfo() const {
        std::cout << " [MedicalBill (Base)] ID: " << billId
                  << " | Amount: $" << std::fixed << std::setprecision(2) << amount << "\n";
    }
};

// ============================================================================
// OOP CONCEPT: Object Slicing Demonstration Class
// VIPBill is a derived class with an additional loungeAccess member.
// ============================================================================
class VIPBill : public MedicalBill {
private:
    std::string loungeAccess;

public:
    VIPBill(std::string id, double amt, std::string lounge)
        : MedicalBill(std::move(id), amt), loungeAccess(std::move(lounge)) {}
    ~VIPBill() override = default;

    std::string getLoungeAccess() const { return loungeAccess; }

    void printInfo() const override {
        std::cout << " [VIPBill (Derived)]  ID: " << billId
                  << " | Amount: $" << std::fixed << std::setprecision(2) << amount
                  << " | VIP Lounge: " << loungeAccess << "\n";
    }
};

// ============================================================================
// OOP CONCEPT: Diamond Problem & Virtual Base Classes
//              AbstractTax (virtual base)
//               /       \
//         MedicalTax    VIPTax
//              \         /
//           FinalDischargeTax
// ============================================================================

// 1. VIRTUAL BASE CLASS
class AbstractTax {
protected:
    double taxAmount;

public:
    AbstractTax() : taxAmount(0.0) {}
    virtual ~AbstractTax() = default;

    virtual std::string getTaxDetails() const = 0;
    virtual double getTaxAmount() const { return taxAmount; }
};

// Left branch: inherits virtual public AbstractTax
class MedicalTax : virtual public AbstractTax {
public:
    MedicalTax() { this->taxAmount = 50.0; }
    std::string getTaxDetails() const override {
        return "Standard Healthcare GST (5% base): $50.00";
    }
};

// Right branch: inherits virtual public AbstractTax
class VIPTax : virtual public AbstractTax {
public:
    VIPTax() { this->taxAmount = 100.0; }
    std::string getTaxDetails() const override {
        return "Luxury Hospital Suite Surcharge: $100.00";
    }
};

// MULTIPLE INHERITANCE: Combines MedicalTax and VIPTax
class FinalDischargeTax : public MedicalTax, public VIPTax {
private:
    std::string referenceId;

public:
    FinalDischargeTax(std::string ref)
        : AbstractTax(), MedicalTax(), VIPTax(), referenceId(std::move(ref)) {
        this->taxAmount = 150.0;
    }

    // "this" Pointer demonstration: enables fluent method chaining
    FinalDischargeTax* setReferenceId(std::string ref) {
        this->referenceId = std::move(ref);
        return this;
    }

    std::string getReferenceId() const { return referenceId; }

    std::string getTaxDetails() const override {
        return "Combined Hospital Assessment for Ref [" + this->referenceId + "]: Total Tax $" + std::to_string((int)taxAmount);
    }
};

// Helper function to generate discharge receipts
inline std::string processDischarge(const std::string& billId, double amount, const std::string& vipLounge) {
    std::ostringstream receipt;
    
    VIPBill fullBill(billId, amount, vipLounge);
    MedicalBill insuranceClaim = fullBill; // Object slicing to base

    FinalDischargeTax finalTax(billId);
    finalTax.setReferenceId(billId);

    const AbstractTax* taxPtr = &finalTax;

    receipt << "=========================================================\n";
    receipt << "           HOSPITAL DISCHARGE & BILLING RECEIPT          \n";
    receipt << "=========================================================\n";
    receipt << " Patient Plan: VIP Care (" << fullBill.getLoungeAccess() << ")\n";
    receipt << " Base Bill & Room: $" << std::fixed << std::setprecision(2) << amount << "\n\n";

    receipt << " [1] Insurance Claim (Sliced base MedicalBill charges):\n";
    receipt << "     " << insuranceClaim << "\n\n";

    receipt << " [2] Statutory Taxes (Dynamic Dispatch via Virtual Base Class):\n";
    receipt << "     " << taxPtr->getTaxDetails() << "\n";
    receipt << "     Tax Amount: $" << std::fixed << std::setprecision(2) << taxPtr->getTaxAmount() << "\n\n";

    double grandTotal = amount + taxPtr->getTaxAmount();
    receipt << " ---------------------------------------------------------\n";
    receipt << "  FINAL PAYABLE AMOUNT: $" << std::fixed << std::setprecision(2) << grandTotal << "\n";
    receipt << "=========================================================\n";

    return receipt.str();
}

// Helper function to demonstrate object slicing interactively
inline void demonstrateObjectSlicing() {
    std::cout << "\n==========================================================\n";
    std::cout << "          OOP CONCEPT: OBJECT SLICING DEMONSTRATION       \n";
    std::cout << "==========================================================\n";
    std::cout << "1. Creating derived VIPBill object:\n";
    VIPBill vipObj("VIP-777", 2500.0, "Presidential Suite Suite-A");
    vipObj.printInfo();

    std::cout << "\n2. Slicing by assigning VIPBill to base MedicalBill BY VALUE:\n";
    std::cout << "   MedicalBill slicedBill = vipObj;  // Slicing occurs!\n";
    MedicalBill slicedBill = vipObj;
    std::cout << "   Calling slicedBill.printInfo():\n   ";
    slicedBill.printInfo();
    std::cout << "   -> Notice: 'loungeAccess' member was SLICED AWAY during copy!\n";

    std::cout << "\n3. Polymorphic access BY POINTER / REFERENCE (No Slicing):\n";
    std::cout << "   MedicalBill* polyPtr = &vipObj;   // Dynamic Dispatch\n";
    MedicalBill* polyPtr = &vipObj;
    std::cout << "   Calling polyPtr->printInfo():\n   ";
    polyPtr->printInfo();
    std::cout << "   -> Notice: Full VIPBill implementation executed via vtable!\n";
    std::cout << "==========================================================\n\n";
}

#endif // MEDICALBILL_HPP
