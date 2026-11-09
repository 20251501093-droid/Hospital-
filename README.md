# 🏥 Hospital Management System

> A hybrid **Java Swing + C++ Native (SWIG) + SQLite + RSA Cryptography** Hospital Management System with **multi-threading**, modern UI, and role-based access control.

![Java](https://img.shields.io/badge/Java-17%2B-orange)
![C++](https://img.shields.io/badge/C%2B%2B-17-blue)
![SQLite](https://img.shields.io/badge/SQLite-3.x-green)
![SWIG](https://img.shields.io/badge/SWIG-4.x-red)

---

## ✨ Features

### 🔐 Security
- **RSA-2048 encryption** for all patient records
- **SHA-256 hashed passwords** for staff authentication
- Role-based access control (Admin, Doctor, Pharmacist)

### 🎨 Modern UI
- Polished gradient login screen
- Dark sidebar navigation with live dashboard
- Custom rounded buttons, placeholder text fields
- Real-time activity feed

### ⚡ Multi-Threading
- **SwingWorker** — background encryption/decryption without UI freezing
- **ExecutorService Thread Pool** — 4 named worker threads (`HMS-Worker-N`)
- **BlockingQueue** — producer-consumer pattern (Doctor → Pharmacist prescription queue)
- **AtomicInteger** — thread-safe patient counter
- **ConcurrentLinkedDeque** — thread-safe activity log

### 🔗 C++ Native Bridge (SWIG)
- Discharge billing computed in C++ via JNI
- Demonstrates: Object Slicing, Virtual Base Classes, Diamond Inheritance, `this` pointer

### 📚 OOP Concepts Demonstrated
| Concept | Implementation |
|---------|---------------|
| Abstraction | `abstract class Person`, `abstract getRole()` |
| Encapsulation | Private fields, public getters |
| Inheritance | 3-level: `Person → HospitalStaff → Receptionist/Pharmacist/LabTechnician` |
| Polymorphism | `getRole()` overridden in every subclass |
| Composition | `Appointment` HAS-A `Patient` and `Doctor` |
| Immutable Class | `Prescription` — final class, final fields, unmodifiable list |
| Singleton | `DatabaseManager` — Bill Pugh Holder Idiom |
| Custom Exceptions | `HospitalException` hierarchy with 3 subclasses |
| equals/hashCode | `Patient` and `Prescription` by domain ID |
| Nested Class | `Patient.MedicalRecord` inner class |
| Static Members | `Patient.HOSPITAL_BRANCH`, `Patient.totalPatients` |

---

## 🚀 Quick Start

### Prerequisites
- **JDK 17+** (tested on JDK 21/25)
- **macOS / Linux / Windows** (with Git Bash or WSL)

### One-Command Launch
```bash
git clone https://github.com/YOUR_USERNAME/hospital-management-system.git
cd hospital-management-system
chmod +x build.sh
./build.sh
```

### Manual Build
```bash
# 1. Download SQLite JDBC driver (if not present)
curl -L -o sqlite-jdbc-3.42.0.0.jar \
  https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.42.0.0/sqlite-jdbc-3.42.0.0.jar

# 2. Compile all Java files
javac -cp ".:sqlite-jdbc-3.42.0.0.jar" *.java

# 3. Run
java -cp ".:sqlite-jdbc-3.42.0.0.jar" -Djava.library.path=. --enable-native-access=ALL-UNNAMED LoginUI
```

> **Note for Windows:** Replace `:` with `;` in classpath: `-cp ".;sqlite-jdbc-3.42.0.0.jar"`

### Rebuild C++ Native Library (Optional)
Only needed if `libBillingBridge.dylib` doesn't work on your system:
```bash
brew install cmake swig    # macOS
# or: sudo apt install cmake swig  # Linux

cmake .
make
```

---

## 🔑 Default Login Credentials

| Role | Username | Password | Access |
|------|----------|----------|--------|
| **Admin** | `admin` | `admin123` | Registration, Billing, OOP Demo |
| **Doctor** | `dr_house` | `doc123` | Medical Records, Prescriptions |
| **Pharmacist** | `tech_lisa` | `tech123` | Pharmacy Queue |

---

## 📁 Project Structure

```
hospital-management-system/
├── Domain Model
│   ├── Person.java              # Abstract base class
│   ├── Patient.java             # AtomicInteger counter, equals/hashCode, nested class
│   ├── Doctor.java              # Extends Person
│   ├── HospitalStaff.java       # Abstract intermediate class
│   ├── Receptionist.java        # 3-level inheritance
│   ├── Pharmacist.java          # 3-level inheritance
│   └── LabTechnician.java       # 3-level inheritance
├── Features
│   ├── Appointment.java         # Composition (has-a)
│   ├── Prescription.java        # Immutable class
│   └── *Exception.java          # Custom exception hierarchy
├── Infrastructure
│   ├── DatabaseManager.java     # Singleton (Bill Pugh)
│   ├── SecurityHelper.java      # RSA-2048 encryption
│   └── AuthManager.java         # SHA-256 auth
├── Threading
│   ├── HospitalTaskManager.java # ExecutorService thread pool
│   ├── PrescriptionQueue.java   # BlockingQueue producer-consumer
│   └── ActivityLog.java         # ConcurrentLinkedDeque activity feed
├── UI
│   ├── LoginUI.java             # Gradient login screen
│   ├── HospitalUI.java          # Main dashboard with sidebar
│   ├── UITheme.java             # Centralized colors/fonts
│   ├── RoundedButton.java       # Custom button component
│   ├── PlaceholderTextField.java # Text field with placeholder
│   └── LoadingDialog.java       # Loading spinner overlay
├── C++ Native Bridge
│   ├── MedicalBill.h / .cpp     # C++ billing logic
│   ├── MedicalBill.i            # SWIG interface
│   ├── CMakeLists.txt           # CMake build config
│   └── libBillingBridge.dylib   # Compiled native library
├── build.sh                     # One-command build & run
├── .gitignore
└── README.md
```

---

## 🧵 Threading Architecture

```
                    ┌──────────────────┐
                    │   EDT (Swing)    │ ← UI updates only
                    └──────┬───────────┘
                           │
            ┌──────────────┼──────────────┐
            │              │              │
    ┌───────▼──────┐ ┌────▼─────┐ ┌──────▼──────┐
    │ HMS-Worker-1 │ │ Worker-2 │ │  Worker-3   │  ← ExecutorService
    │  (RSA Enc)   │ │ (DB Ops) │ │  (Billing)  │
    └──────────────┘ └──────────┘ └─────────────┘
                           │
                    ┌──────▼──────┐
                    │ BlockingQ   │ ← Producer-Consumer
                    │ (Rx Queue)  │
                    └─────────────┘
```

---

## 📄 License

This project is for academic purposes (IIIT Vadodara OOP Evaluation).
