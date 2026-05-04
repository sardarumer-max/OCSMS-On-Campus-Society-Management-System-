#!/bin/bash

# OCSMS - On-Campus Societies Management System
# Cross-platform run script for macOS, Linux, and Windows (Git Bash/WSL)

set -e  # Exit on error

echo "=========================================="
echo "OCSMS - Supabase Enabled (Glassmorphism)"
echo "=========================================="
echo ""

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "[ERROR] Java is not installed or not in PATH."
    echo "Please install Java JDK 11 or later."
    exit 1
fi

echo "Found Java:"
java -version
echo ""

# Navigate to src directory
cd src || exit 1

echo "Compiling Java files..."
echo ""

# Compile with proper classpath handling
javac -encoding UTF-8 -cp ".:../lib/gson-2.10.1.jar" ocsms/Main.java

if [ $? -ge 1 ]; then
    echo ""
    echo "[ERROR] Compilation failed! Please check your code."
    cd ..
    exit 1
fi

echo ""
echo "✓ Compilation successful!"
echo ""
echo "Launching OCSMS..."
echo ""

# Run with proper classpath
java -cp ".:../lib/gson-2.10.1.jar" ocsms.Main

# Return to root directory
cd ..
