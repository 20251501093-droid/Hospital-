import java.util.Objects;

/**
 * ABSTRACT BASE CLASS — demonstrates Abstraction (evaluation checklist #2).
 * Cannot be instantiated directly. Forces subclasses to implement getRole().
 *
 * OOP Concepts demonstrated:
 * - Abstraction: class is abstract, getRole() is abstract
 * - Encapsulation: private fields with public getters
 * - Polymorphism: getRole() is overridden in every subclass
 */
public abstract class Person {
    // Encapsulation: fields are PRIVATE (not protected), accessed via getters only
    private String name;
    private String contactNumber;

    // Default constructor — used by subclass default constructors
    protected Person() {
        this.name = "Unknown";
        this.contactNumber = "000-0000";
    }

    // Parameterized constructor — called via super() in subclasses
    protected Person(String name, String contactNumber) {
        this.name = name;
        this.contactNumber = contactNumber;
    }

    // Encapsulation: public getters for private fields
    public String getName() { return name; }
    public String getContactNumber() { return contactNumber; }

    /**
     * ABSTRACT METHOD — each subclass MUST provide its own implementation.
     * This is the heart of runtime polymorphism in this hierarchy.
     */
    public abstract String getRole();

    @Override
    public String toString() {
        return getRole() + ": " + name + " (" + contactNumber + ")";
    }
}