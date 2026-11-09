%module BillingBridge
%{
#include "MedicalBill.h"
%}
%include "std_string.i"

class MedicalBill {
public:
    MedicalBill(std::string id, double amt);
    MedicalBill();
    std::string getBillId();
    double getAmount();
    MedicalBill add(const MedicalBill& other) const;
    void printInfo() const;
};

// THESE MUST BE DOWN HERE, OUTSIDE THE CLASS!
std::string processDischarge(std::string billId, double amount, std::string vipLounge);
void runAdvancedCppDemos();