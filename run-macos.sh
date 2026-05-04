#!/bin/bash

# OCSMS - On-Campus Societies Management System
# macOS-friendly run script for default terminal use.

set -e

BASE_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$BASE_DIR"

echo "=========================================="
echo "OCSMS - Supabase Enabled (Glassmorphism)"
echo "=========================================="
echo ""

if ! command -v java &> /dev/null; then
    echo "[ERROR] Java is not installed or not in PATH."
    echo "If you installed OpenJDK via Homebrew, add it to your shell profile:"
    echo "  echo 'export PATH=\"/opt/homebrew/opt/openjdk@17/bin:$PATH\"' >> ~/.zshrc"
    echo "  source ~/.zshrc"
    echo "Or run the system wrapper setup:"
    echo "  sudo ln -sfn /opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-17.jdk"
    exit 1
fi

echo "Found Java:"
java --version
echo ""

if [ ! -d "src" ] || [ ! -d "lib" ]; then
    echo "[ERROR] This script must be run from the project root directory."
    echo "Current directory: $BASE_DIR"
    echo "Make sure run-macos.sh is located in the project root next to src/ and lib/."
    exit 1
fi

cd src

echo "Compiling Java files..."
echo ""

javac -encoding UTF-8 -cp ".:../lib/gson-2.10.1.jar" ocsms/Main.java

if [ $? -ge 1 ]; then
    echo ""
    echo "[ERROR] Compilation failed! Please check your code."
    exit 1
fi

echo ""
echo "✓ Compilation successful!"
echo ""
echo "Launching OCSMS..."
echo ""
java -cp ".:../lib/gson-2.10.1.jar" ocsms.Main
