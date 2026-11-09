#include "MedicalBill.h"
#include <sstream>

MedicalBill::MedicalBill(std::string id, double amt) : billId(id), amount(amt) {}
MedicalBill::MedicalBill() : billId("B000"), amount(0.0) {}

std::string MedicalBill::getBillId() { return billId; }
double MedicalBill::getAmount() { return amount; }

std::ostream& operator<<(std::ostream& os, const MedicalBill& bill) {
    os << "BILL ID: " << bill.billId << " | AMOUNT DUE: $" << bill.amount;
    return os;
}

VIPBill::VIPBill(std::string id, double amt, std::string lounge) : MedicalBill(id, amt), loungeAccess(lounge) {}


AbstractTax::AbstractTax() { this->taxAmount = 0.0; } 

MedicalTax::MedicalTax() { this->taxAmount = 50.0; }
std::string MedicalTax::getTaxDetails() { return "Standard Medical Tax Applied"; }

VIPTax::VIPTax() { this->taxAmount = 100.0; }
std::string VIPTax::getTaxDetails() { return "Premium VIP Tax Applied"; }

FinalDischargeTax::FinalDischargeTax(std::string ref) {
    this->referenceId = ref; 
    this->taxAmount = 150.0; 
}

// Returning "this" pointer for method chaining
FinalDischargeTax* FinalDischargeTax::setReferenceId(std::string ref) {
    this->referenceId = ref; 
    return this;             
}

std::string FinalDischargeTax::getTaxDetails() {
    return "Combined Hospital Tax Assessed for Ref: " + this->referenceId;
}

std::string processDischarge(std::string billId, double amount, std::string vipLounge) {
    std::ostringstream receipt;
    
    // Original Slicing Logic
    VIPBill fullBill(billId, amount, vipLounge);
    MedicalBill insuranceClaim = fullBill; 

    // Pointer to Objects & Pointer to Derived Classes
    FinalDischargeTax finalTax(billId);
    finalTax.setReferenceId(billId); 
    
    AbstractTax* basePtr;    
    basePtr = &finalTax;     

    receipt << "--- HOSPITAL DISCHARGE SUMMARY ---\n";
    receipt << "Patient opted for VIP upgrade (" << fullBill.loungeAccess << ").\n\n";
    
    receipt << "[Insurance Claim Generated - Non-Medical Charges Sliced]\n";
    receipt << insuranceClaim << "\n\n";
    
    receipt << "[Government Taxes - Dynamic Dispatch via Virtual Functions]\n";
    
    receipt << ">> " << basePtr->getTaxDetails() << " (Amount: $" << 150.0 << ")\n";
    receipt << "----------------------------------";

    return receipt.str(); 
}


MedicalBill MedicalBill::add(const MedicalBill& other) const { return MedicalBill("COMBINED-TOTAL", this->amount + other.amount); }
void MedicalBill::printInfo() const { std::cout << "Standard Bill - " << billId << std::endl; }
void VIPBill::printInfo() const { std::cout << "VIP Bill - " << billId << " | Access: " << loungeAccess << std::endl; }
void runAdvancedCppDemos() { std::cout << "Advanced C++ Demos initialized and moved to GUI." << std::endl; }