#ifndef MEDICALBILL_H
#define MEDICALBILL_H

#include <string>
#include <iostream>

class MedicalBill {
public:
    std::string billId;
    double amount;

    MedicalBill(std::string id, double amt);
    MedicalBill();

    friend MedicalBill operator+(const MedicalBill& b1, const MedicalBill& b2);
    friend std::ostream& operator<<(std::ostream& os, const MedicalBill& bill);

    std::string getBillId();
    double getAmount();
    MedicalBill add(const MedicalBill& other) const;

    virtual void printInfo() const;
};

std::string processDischarge(std::string billId, double amount, std::string vipLounge);

// --- OBJECT SLICING ---
class VIPBill : public MedicalBill {
public:
    std::string loungeAccess;
    VIPBill(std::string id, double amt, std::string lounge);
    void printInfo() const override;
};

void runAdvancedCppDemos();


// 1. VIRTUAL BASE CLASS 
class AbstractTax {
protected:
    double taxAmount;
public:
    AbstractTax();
    virtual ~AbstractTax() = default;
    
    // 2. VIRTUAL FUNCTION
    virtual std::string getTaxDetails() = 0; 
};

class MedicalTax : virtual public AbstractTax { 
public:
    MedicalTax();
    std::string getTaxDetails() override;
};

class VIPTax : virtual public AbstractTax { 
public:
    VIPTax();
    std::string getTaxDetails() override;
};

// MULTIPLE INHERITANCE using Virtual Base Classes
class FinalDischargeTax : public MedicalTax, public VIPTax {
private:
    std::string referenceId;
public:
    FinalDischargeTax(std::string ref);
    
    // 3. "this" POINTER
    FinalDischargeTax* setReferenceId(std::string ref); 
    
    std::string getTaxDetails() override;
};

#endif