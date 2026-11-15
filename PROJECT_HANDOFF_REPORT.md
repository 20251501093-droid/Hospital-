# Hospital Management System (HMS v2.0) — Complete Engineering & Handoff Report

> **Project Version:** 2.0 (Multithreaded + Modern Swing UI + Native C++ Bridge)  
> **Course/Evaluation:** Object-Oriented Programming (Academic Checklist Steps 1–6 Completed)  
> **Repository Path:** `/Users/dhruvpatel/Desktop/hms`  
> **Target Runtimes:** Java 17+ (Tested on OpenJDK 25.0.5), C++17, SQLite 3.x, macOS (Apple Silicon / arm64)  
> **Report Timestamp:** 2026-11-09T18:25:00+05:30  

---

## 1. Executive Summary & Architectural Overview

The Hospital Management System is a hybrid, multi-tier enterprise desktop application combining **Java Swing GUI**, **C++17 native performance modules (SWIG JNI)**, **SQLite transactional database**, and **RSA-2048 / SHA-256 military-grade cryptography**.

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                       PRESENTATION LAYER (Swing GUI)                        │
│   LoginUI (Gradient Card)  │  HospitalUI (Collapsible Sidebar + Dashboard)  │
│   UITheme  │  RoundedButton  │  PlaceholderTextField  │  LoadingDialog      │
└──────────────────────────────────────┬──────────────────────────────────────┘
                                       │ Non-blocking EDT dispatches
┌──────────────────────────────────────▼──────────────────────────────────────┐
│                    CONCURRENCY & THREADING ENGINE                           │
│  HospitalTaskManager (4-Thread ExecutorService: HMS-Worker-1..4)            │
│  PrescriptionQueue (LinkedBlockingQueue: Doctor Producer -> Pharma Consumer)│
│  ActivityLog (ConcurrentLinkedDeque: Lock-free event streaming)             │
│  Patient.totalPatients (AtomicInteger: Lock-free CAS counter)               │
└───────────────────┬─────────────────────────────────────┬───────────────────┘
                    │                                     │
┌───────────────────▼────────────────┐   ┌────────────────▼───────────────────┐
│     SECURITY & DATA PERSISTENCE    │   │      C++ NATIVE JNI BRIDGE         │
│  DatabaseManager (Bill Pugh Pugh)  │   │  libBillingBridge.dylib (SWIG)     │
│  SecurityHelper (RSA-2048 Asymm)   │   │  MedicalBill.cpp / MedicalBill.h   │
│  AuthManager (SHA-256 Hashing)     │   │  - Diamond Virtual Base Classes    │
│  hospital.db (SQLite)              │   │  - C++ Object Slicing Demos        │
└────────────────────────────────────┘   └────────────────────────────────────┘
```

### Key Milestones Completed:
1. **Zero-Error Build Pipeline**: Complete restoration of all missing source files, removal of incompatible bytecode, integration of `sqlite-jdbc-3.42.0.0.jar`.
2. **Apple Silicon Code Signing**: Resolved macOS `disallowed by system policy` gatekeeper blocks via ad-hoc entitlement re-signing (`codesign --force --sign - libBillingBridge.dylib`).
3. **Enterprise UI Modernization**: Replaced generic Swing widgets with custom rounded cards, dark sidebar navigation (`#1B2838`), live stat indicators, and animated loading spinners.
4. **Complete Concurrency Overhaul**: Decoupled expensive cryptographic operations (RSA keygen/encryption) and SQL queries from the Event Dispatch Thread (EDT) using background worker pools.
5. **GitHub Distribution Architecture**: Provided autonomous `build.sh` pipeline, clean `.gitignore`, and comprehensive documentation.

---

## 2. Complete File Inventory & Module Mapping

The root codebase contains **26 clean Java source files**, native C++ headers/sources, build descriptors, and binary assets.

```
/Users/dhruvpatel/Desktop/hms/
├── Core Domain Model
│   ├── Person.java                      # [Checklist #1, #2] Abstract base class, private encapsulated state
│   ├── Patient.java                     # [Checklist #3, #6, #8, #9] Inheritance, AtomicInteger, Inner class
│   ├── Doctor.java                      # [Checklist #3, #4] Constructor chaining super(), getRole(), fee logic
│   ├── HospitalStaff.java               # [Checklist #2, #3] Abstract intermediate class (Multilevel inheritance)
│   ├── Receptionist.java                # [Checklist #3, #4] Concrete staff subclass, desk allocation
│   ├── Pharmacist.java                  # [Checklist #3, #4] Concrete staff subclass, medication dispensing
│   └── LabTechnician.java               # [Checklist #3, #4] Concrete staff subclass, test execution
│
├── Domain Features & Exceptions
│   ├── Appointment.java                 # [Checklist #5] Composition ("Has-A" Patient and Doctor)
│   ├── Prescription.java                # [Checklist #7, #8] Immutable class, Collections.unmodifiableList
│   ├── HospitalException.java           # [Checklist #11] Root checked exception base
│   ├── PatientNotFoundException.java    # [Checklist #11] Custom diagnostic checked exception
│   ├── AuthenticationFailedException.java # [Checklist #11] Security domain checked exception
│   └── InvalidPrescriptionException.java  # [Checklist #11] Validation domain checked exception
│
├── Persistence & Cryptography
│   ├── DatabaseManager.java             # [Checklist #10] Thread-safe Bill Pugh Singleton, SQLite JDBC
│   ├── SecurityHelper.java              # RSA-2048 keypair generation, PKCS8/X509 serialization
│   └── AuthManager.java                 # SHA-256 salted password hashing & role authorization
│
├── Concurrency & Multithreading Layer
│   ├── HospitalTaskManager.java         # Fixed 4-thread ExecutorService with named Worker ThreadFactory
│   ├── PrescriptionQueue.java           # Thread-safe LinkedBlockingQueue Producer-Consumer pipeline
│   └── ActivityLog.java                 # Thread-safe ConcurrentLinkedDeque with CopyOnWriteArrayList listeners
│
├── Modern Swing UI Presentation Layer
│   ├── UITheme.java                     # Central design tokens (palette, typography, padding, card factory)
│   ├── RoundedButton.java               # Anti-aliased custom vector button with hover & click state machine
│   ├── PlaceholderTextField.java        # Inner class PlaceholderPasswordField, custom focus borders
│   ├── LoadingDialog.java               # Non-blocking animated indeterminate spinner modal
│   ├── LoginUI.java                     # Gradient background, non-blocking auth dispatch via task manager
│   └── HospitalUI.java                  # Role-aware multi-card dashboard, sidebar navigation, real-time counters
│
├── C++ SWIG Native Bridge
│   ├── MedicalBill.h                    # C++ Header: Virtual Base Classes, Diamond problem, Object slicing
│   ├── MedicalBill.cpp                  # C++ Logic: Overloaded operators, method chaining, discharge summary
│   ├── MedicalBill.i                    # SWIG interface file declaring exported JNI bindings
│   ├── BillingBridge.java               # SWIG-generated high-level Java class
│   ├── BillingBridgeJNI.java            # SWIG-generated low-level native JNI bindings
│   ├── libBillingBridge.dylib           # Ad-hoc signed arm64 native shared library
│   ├── CMakeLists.txt                   # CMake cross-platform build specification
│   └── TerminalRunner.java              # Standalone CLI harness for validating the C++ bridge directly
│
└── DevOps & Launch Scripts
    ├── build.sh                         # Autonomous end-to-end setup, compile, sign, and execution script
    ├── .gitignore                       # Production ignore rules (excludes .class, .dylib, .key, .db)
    ├── README.md                        # Formatted GitHub project documentation
    └── sqlite-jdbc-3.42.0.0.jar         # SQLite JDBC driver bundle
```

---

## 3. Comprehensive OOP Evaluation Checklist Matrix

| # | Academic Requirement | Implemented File(s) | Implementation Specifics |
|---|---|---|---|
| **1** | **Encapsulation** | `Person.java`, `Doctor.java`, `Patient.java` | All member variables declared `private` or `protected`. State access strictly moderated via public accessors (`getName()`, `getConsultationFee()`). |
| **2** | **Abstraction** | `Person.java`, `HospitalStaff.java` | `Person` declared `abstract` with abstract method `public abstract String getRole()`. Cannot be instantiated with `new Person()`. |
| **3** | **Inheritance** | All model classes | **Hierarchical:** `Person` -> `Doctor`, `Person` -> `Patient`.<br>**Multilevel:** `Person` -> `HospitalStaff` -> `Receptionist`, `Pharmacist`, `LabTechnician`. |
| **4** | **Polymorphism (Dynamic Dispatch)** | All model subclasses | `getRole()` and `toString()` overridden across all derived classes. Demonstrable through polymorphic iteration over a `Person[]` array. |
| **5** | **Composition ("Has-A")** | `Appointment.java` | `Appointment` HAS-A `Patient` and HAS-A `Doctor`. Ownership enforced at construction time using `Objects.requireNonNull()`. |
| **6** | **Static Members & Constants** | `Patient.java` | Constant: `public static final String HOSPITAL_BRANCH`. Counter: `private static final AtomicInteger totalPatients`. |
| **7** | **Immutable Class** | `Prescription.java` | Class declared `final`. All fields declared `final`. No setters. Mutable `List<String>` is defensively copied via `Collections.unmodifiableList(List.copyOf(...))`. |
| **8** | **Object Equality (`equals`/`hashCode`)** | `Patient.java`, `Prescription.java` | Overridden to evaluate identity by business key (`patientId`, `prescriptionId`) conforming to reflexive, symmetric, and transitive axioms. |
| **9** | **Nested Inner Class** | `Patient.java` (`MedicalRecord`) | Non-static inner class having direct implicit lexical reference to enclosing outer instance via `Patient.this.getName()`. |
| **10** | **Singleton Pattern** | `DatabaseManager.java`, `PrescriptionQueue.java` | Implemented via thread-safe lazy-loaded **Bill Pugh Holder Idiom** (`private static class Holder`). Private constructor guarantees single instance. |
| **11** | **Custom Exception Hierarchy** | `HospitalException.java` and derivatives | Root checked `HospitalException` specialized by `PatientNotFoundException`, `AuthenticationFailedException`, and `InvalidPrescriptionException`. |

---

## 4. Multi-Threading & Concurrency Architecture

### 4.1 EDT Protection via Thread Pool
Swing is single-threaded; any long-running computation on the Event Dispatch Thread (EDT) freezes the UI. In HMS v2.0:
- **`HospitalTaskManager`** initializes an `ExecutorService` configured with a fixed pool of 4 daemon threads (`HMS-Worker-1` through `HMS-Worker-4`).
- Heavy tasks (RSA key decryption, database IO, and C++ JNI bridge calls) run asynchronously inside `runInBackground(Callable<T>, Consumer<T>, Consumer<Exception>)`.
- Results and UI alterations are marshaled back onto the EDT strictly via `SwingUtilities.invokeLater()`.

```
[ User Clicks "Encrypt & Save" ]
               │
               ▼
[ EDT dispatches saveBtn.addActionListener ] ──> UI remains responsive (loading state)
               │
               ▼
[ HospitalTaskManager.runInBackground() ]
               │
               ├──> [ HMS-Worker-2 executes RSA encryption & SQLite Write ]
               │
               ▼ (Completion)
[ SwingUtilities.invokeLater() ] ──> Updates UI text fields & ActivityLog on EDT
```

### 4.2 Producer-Consumer Pipeline (`PrescriptionQueue`)
- **Producer (Doctor):** Doctor constructs an immutable `Prescription` and calls `PrescriptionQueue.getInstance().addPrescription(rx)`.
- **Underlying Buffer:** Backed by `LinkedBlockingQueue<Prescription>`, guaranteeing safe multi-threaded insertions without external `synchronized` blocks.
- **Consumer (Pharmacist):** The Pharmacist UI registers a `QueueListener` via a thread-safe `CopyOnWriteArrayList`. When a doctor submits a prescription, the pharmacist's terminal updates instantly with zero polling.

### 4.3 Atomic Lock-Free Counter
`Patient.totalPatients` uses `java.util.concurrent.atomic.AtomicInteger`. When multiple background threads register patients concurrently, `totalPatients.incrementAndGet()` executes hardware-level **Compare-And-Swap (CAS)** operations, eliminating race conditions without blocking lock overhead.

---

## 5. C++ Native SWIG Bridge Architecture

### 5.1 Object Slicing Demonstration
In `MedicalBill.cpp`, the derived class `VIPBill` inherits from `MedicalBill` and adds `std::string loungeAccess`. When assigned to a base value:
```cpp
VIPBill fullBill(billId, amount, vipLounge);
MedicalBill insuranceClaim = fullBill; // Object slicing: loungeAccess is sliced off
```
Only the base `MedicalBill` member variables survive the copy, simulating insurance slicing non-medical amenities from hospital claims.

### 5.2 Virtual Base Classes (Diamond Problem Solution)
To prevent duplicate instances of `AbstractTax` when inheriting through multiple paths:
```cpp
class MedicalTax : virtual public AbstractTax { ... };
class VIPTax     : virtual public AbstractTax { ... };
class FinalDischargeTax : public MedicalTax, public VIPTax { ... };
```
The `virtual` inheritance ensures only one canonical `AbstractTax` subobject exists inside `FinalDischargeTax`, resolving ambiguity at runtime.

---

## 6. How to Launch & Run the Project

### Method A: One-Command Autonomous Script (Recommended)
From the project root:
```bash
chmod +x build.sh
./build.sh
```
This script will:
1. Verify the presence of `sqlite-jdbc-3.42.0.0.jar` (downloads automatically if absent).
2. Verify native shared libraries and re-sign them on macOS.
3. Clean and compile all 26 Java classes.
4. Launch `LoginUI` with all native access and classpath parameters configured.

### Method B: Manual Terminal Execution
```bash
# 1. Clean previous binaries
rm -f *.class

# 2. Compile all source files
javac -cp ".:sqlite-jdbc-3.42.0.0.jar" *.java

# 3. Launch application
java -cp ".:sqlite-jdbc-3.42.0.0.jar" \
     -Djava.library.path=. \
     --enable-native-access=ALL-UNNAMED \
     LoginUI
```

### Seeded Credentials:
| Portal / Role | Username | Password | Accessible Features |
|---|---|---|---|
| **Administrator** | `admin` | `admin123` | Patient Registration (RSA), C++ Discharge Billing, Interactive OOP Demo |
| **Doctor** | `dr_house` | `doc123` | Search & RSA Decrypt Patient Records, Issue Prescriptions (Queue Producer) |
| **Pharmacist** | `tech_lisa` | `tech123` | Pharmacy Queue Terminal (Queue Consumer), Dispense Medications |

---

## 7. Git & GitHub Setup Instructions

The repository is fully configured with an enterprise `.gitignore`. To push this project to your GitHub account:

```bash
cd /Users/dhruvpatel/Desktop/hms

# 1. Initialize Git repository
git init

# 2. Stage all files (sensitive keys and binaries are excluded by .gitignore)
git add -A

# 3. Commit initial release
git commit -m "feat: Hospital Management System v2.0 - Multithreading, Modern UI, SWIG Bridge"

# 4. Set main branch
git branch -M main

# 5. Connect your remote repository (replace with your repository URL)
git remote add origin https://github.com/YOUR_USERNAME/hospital-management-system.git

# 6. Push to GitHub
git push -u origin main
```

---

## 8. Viva & Technical Defense Quick-Reference

### Q: Why use Bill Pugh Singleton for `DatabaseManager` instead of Double-Checked Locking?
> **Answer:** The Bill Pugh Singleton uses an inner static holder class (`private static class Holder`). The JVM does not load `Holder` into memory until `getInstance()` is called, achieving **lazy loading**. Furthermore, class loading in the JVM is inherently thread-safe by specification (JLS 12.4.2), eliminating the need for complex `volatile` barriers or synchronized blocks.
>
> *Trade-off to mention:* Singletons introduce tight coupling across modules, making isolated unit testing harder without dependency injection frameworks (e.g., Spring or Guice).

### Q: How does `Prescription` guarantee true immutability?
> **Answer:** It fulfills all 5 immutability rules:
> 1. Class declared `final` (prevents malicious subclassing).
> 2. All fields declared `private final` (written once during construction).
> 3. Zero setter methods.
> 4. Defensive copying: The collection parameter `medications` is wrapped in `Collections.unmodifiableList(List.copyOf(medications))` to prevent external callers from mutating the internal list.
> 5. Thread-safe by design without synchronization overhead.

### Q: Why did the native C++ library fail initially on macOS, and how was it solved?
> **Answer:** macOS enforces Code Integrity protection via Gatekeeper and `dyld`. When binaries are copied across systems, macOS marks them with quarantine extended attributes and rejects untrusted ad-hoc code signatures. The problem was fixed by clearing quarantine flags and signing the binary locally using `codesign --force --sign - libBillingBridge.dylib`.

### Q: Why not run database encryption directly inside the button click listener?
> **Answer:** Action listeners in Swing execute on the Event Dispatch Thread (EDT). RSA-2048 encryption involves modular exponentiation with 2048-bit primes, which is CPU intensive. Running it on the EDT causes the UI to freeze and stops window repaint events. Delegating to `HospitalTaskManager` (ExecutorService) ensures the UI continues animating smoothly at 60 FPS while background workers complete the task.
