# OCSMS macOS Setup & Run Guide

## Color Scheme
Your project now uses the following color palette throughout the entire UI:
- **Deep Blue (C1)**: `#093C5D` - Primary background
- **Mid Blue (C2)**: `#3B7597` - Card backgrounds & hover states  
- **Teal (C3)**: `#6FD1D7` - Accents & borders
- **Mint/Cyan (C4)**: `#5DF8D8` - Primary accent & highlights

These colors are applied to all components including buttons, panels, tables, and text fields.

## Font Updates
Fonts have been updated to use **SansSerif** (cross-platform compatible) instead of Segoe UI:
- Works on **Windows, macOS, and Linux**
- Will automatically use system fonts (San Francisco on macOS, Segoe UI on Windows)

## Prerequisites

You'll need the following installed on your Mac:

### 1. Java Development Kit (JDK)
Install Java 11 or later:

**Option A: Using Homebrew (Recommended)**
```bash
brew install openjdk@17
```

**Option B: Download from Oracle**
- Visit: https://www.oracle.com/java/technologies/downloads/
- Download JDK 17 or later
- Install and follow the installation wizard

**Verify Installation:**
```bash
java -version
```

### 2. Git (if not installed)
```bash
brew install git
```

## Running the Project on macOS

### Method 1: Using the Shell Script (Easiest)

```bash
cd /path/to/OCSMS-On-Campus-Society-Management-System-
chmod +x run.sh
./run.sh
```

The script will:
1. ✓ Check Java installation
2. ✓ Compile all Java files with UTF-8 encoding
3. ✓ Include the Gson library in the classpath
4. ✓ Launch the application

### Method 2: Manual Compilation & Execution

```bash
cd /path/to/OCSMS-On-Campus-Society-Management-System-/src
javac -encoding UTF-8 -cp ".:../lib/gson-2.10.1.jar" ocsms/Main.java
java -cp ".:../lib/gson-2.10.1.jar" ocsms.Main
```

### Method 3: Using an IDE

**IntelliJ IDEA** (Recommended)
1. Open the project folder
2. Let IntelliJ detect the Java files
3. Configure the SDK (JDK 17+)
4. Run `ocsms.Main` class

**VS Code**
1. Install Extension Pack for Java
2. Open the project
3. Run the Main.java file

## Troubleshooting

### Issue: "java: command not found"
**Solution:**
```bash
# If installed via Homebrew, add to PATH
echo 'export PATH="/usr/local/opt/openjdk@17/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc
```

### Issue: "Cannot find symbol" or compilation errors
**Solution:**
- Ensure you're in the `/src` directory when compiling
- Check that `lib/gson-2.10.1.jar` exists in the project root

### Issue: Gradle/Maven errors
**Note:** This is a pure Java Swing project. No Gradle or Maven needed. Use the `run.sh` script directly.

## Project Structure

```
OCSMS-On-Campus-Society-Management-System-/
├── run.sh                 # macOS/Linux run script
├── run.bat               # Windows run script
├── lib/
│   └── gson-2.10.1.jar   # JSON library (Supabase integration)
├── src/
│   └── ocsms/
│       ├── Main.java              # Entry point
│       ├── controller/            # MVC Controllers
│       ├── model/                 # Data models
│       ├── view/                  # UI frames & panels
│       ├── service/               # Business logic
│       ├── util/                  # Utilities & theme
│       └── pattern/               # Design patterns
└── supabase_schema.sql    # Database schema

```

## Additional Notes

- **Database**: Uses Supabase (cloud PostgreSQL)
- **UI Framework**: Java Swing with custom glassmorphic theme
- **Build System**: None required - pure Java compilation
- **Cross-Platform**: Works on Windows, macOS, and Linux

## Contact & Support

For issues specific to macOS, ensure:
1. Java version is 11 or higher
2. You're using the `run.sh` script (not `run.bat`)
3. The script has execute permissions: `chmod +x run.sh`

Enjoy using OCSMS! 🎓
