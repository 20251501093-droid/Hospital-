cmake_minimum_required(VERSION 3.18)
project(HospitalBillingBridge CXX)

set(CMAKE_CXX_STANDARD 17)

# Find Java, JNI, and SWIG automatically
find_package(Java REQUIRED)
find_package(JNI REQUIRED)
find_package(SWIG REQUIRED)
include(UseSWIG)

include_directories(${JNI_INCLUDE_DIRS} .)

# Compile the SWIG interface and C++ logic into a shared library
set_property(SOURCE MedicalBill.i PROPERTY CPLUSPLUS ON)
swig_add_library(BillingBridge
  TYPE SHARED
  LANGUAGE java
  SOURCES MedicalBill.i MedicalBill.cpp
)
target_link_libraries(BillingBridge ${JNI_LIBRARIES})