#!/bin/bash
# =============================================================
#  Hospital Management System — Build & Run Script
#  Works on macOS, Linux, and Windows (Git Bash / WSL)
# =============================================================

set -e

JAR="sqlite-jdbc-3.42.0.0.jar"
JAR_URL="https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.42.0.0/sqlite-jdbc-3.42.0.0.jar"
LIB="libBillingBridge"

echo "============================================="
echo " Hospital Management System — Build Script"
echo "============================================="
echo ""

# --- Step 1: Download SQLite JDBC if missing ---
if [ ! -f "$JAR" ]; then
    echo "[1/4] Downloading SQLite JDBC driver..."
    if command -v curl &> /dev/null; then
        curl -L -o "$JAR" "$JAR_URL"
    elif command -v wget &> /dev/null; then
        wget -O "$JAR" "$JAR_URL"
    else
        echo "ERROR: Neither curl nor wget found. Please download manually:"
        echo "  $JAR_URL"
        exit 1
    fi
    echo "      Downloaded $JAR"
else
    echo "[1/4] SQLite JDBC driver found ✓"
fi

# --- Step 2: Build native C++ library if missing ---
if [ -f "${LIB}.dylib" ] || [ -f "${LIB}.so" ]; then
    echo "[2/4] Native library found ✓"
    # Re-sign on macOS if needed
    if [[ "$OSTYPE" == "darwin"* ]]; then
        codesign --force --sign - ${LIB}.dylib 2>/dev/null || true
    fi
else
    echo "[2/4] Native library not found. Attempting to build..."
    if command -v cmake &> /dev/null && command -v swig &> /dev/null; then
        mkdir -p build && cd build
        cmake .. && make
        cp ${LIB}.* .. 2>/dev/null || true
        cd ..
        echo "      Native library built ✓"
    else
        echo "WARNING: cmake/swig not installed. C++ billing features will be disabled."
        echo "         Install with: brew install cmake swig (macOS) or apt install cmake swig (Linux)"
    fi
fi

# --- Step 3: Compile Java sources ---
echo "[3/4] Compiling Java sources..."
javac -cp ".:${JAR}" *.java
echo "      Compilation successful ✓"

# --- Step 4: Launch ---
echo "[4/4] Launching Hospital Management System..."
echo ""
echo "  Login Credentials:"
echo "  ─────────────────────────────────────"
echo "  Admin:      admin / admin123"
echo "  Doctor:     dr_house / doc123"
echo "  Pharmacist: tech_lisa / tech123"
echo "  ─────────────────────────────────────"
echo ""

java -cp ".:${JAR}" \
     -Djava.library.path=. \
     --enable-native-access=ALL-UNNAMED \
     LoginUI
