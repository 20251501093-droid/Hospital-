#!/bin/bash
# =============================================================
#  Hospital Management System — C++ OOP Terminal Edition
#  Universal Cross-Platform Build & Run Script (macOS / Linux)
# =============================================================

set -e

echo "=========================================================="
echo " 🏥 Building Hospital Management System (C++ Terminal App)"
echo "=========================================================="

# Auto-detect available C++ compiler
if command -v clang++ &> /dev/null; then
    CXX="clang++"
elif command -v g++ &> /dev/null; then
    CXX="g++"
elif command -v c++ &> /dev/null; then
    CXX="c++"
else
    echo "ERROR: No C++ compiler found (clang++ or g++ required)."
    echo "Please install Xcode Command Line Tools (macOS) or build-essential (Linux)."
    exit 1
fi

echo "  -> Detected Compiler: $CXX"
echo "  -> Compiling main.cpp with C++17 and -pthread..."

$CXX -std=c++17 -Wall -Wextra -O2 -pthread main.cpp -o hms

echo ""
echo "Compilation complete! Launching Terminal Application..."
echo "=========================================================="
./hms
