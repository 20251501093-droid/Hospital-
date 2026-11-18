@echo off
REM =============================================================
REM  Hospital Management System — C++ OOP Terminal Edition
REM  Windows Build Script (MinGW / g++ / clang++)
REM =============================================================

echo ==========================================================
echo  Building Hospital Management System (C++ Terminal App)
echo ==========================================================

where g++ >nul 2>nul
if %ERRORLEVEL% EQU 0 (
    echo Detected Compiler: g++
    g++ -std=c++17 -Wall -Wextra -O2 main.cpp -o hms.exe
    goto RUN
)

where clang++ >nul 2>nul
if %ERRORLEVEL% EQU 0 (
    echo Detected Compiler: clang++
    clang++ -std=c++17 -Wall -Wextra -O2 main.cpp -o hms.exe
    goto RUN
)

echo ERROR: Neither g++ nor clang++ was found in PATH.
echo Please install MinGW-w64 or Visual Studio C++ build tools.
pause
exit /b 1

:RUN
echo Compilation successful! Launching...
echo ==========================================================
hms.exe
