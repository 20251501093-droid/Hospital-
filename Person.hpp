#ifndef PERSON_HPP
#define PERSON_HPP

#include <string>
#include <iostream>

/**
 * ============================================================================
 * OOP CONCEPT: Abstraction & Encapsulation
 * ============================================================================
 * - Abstract Base Class: Contains pure virtual functions (= 0)
 * - Private member variables with public getters/setters (Encapsulation)
 * - Virtual destructor ensures proper cleanup of derived classes
 */
class Person {
private:
    std::string name;
    std::string contactNumber;

protected:
    Person() : name("Unknown"), contactNumber("000-0000") {}
    Person(std::string name, std::string contactNumber)
        : name(std::move(name)), contactNumber(std::move(contactNumber)) {}

public:
    virtual ~Person() = default;

    // Encapsulation: Getters
    std::string getName() const { return name; }
    std::string getContactNumber() const { return contactNumber; }

    // Encapsulation: Setters
    void setName(const std::string& newName) {
        if (!newName.empty()) name = newName;
    }
    void setContactNumber(const std::string& newContact) {
        if (!newContact.empty()) contactNumber = newContact;
    }

    // Abstraction & Runtime Polymorphism: Pure virtual methods
    virtual std::string getRole() const = 0;
    virtual void displayDetails() const = 0;
};

#endif // PERSON_HPP
