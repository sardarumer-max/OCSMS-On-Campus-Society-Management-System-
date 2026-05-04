cd /path/to/OCSMS-On-Campus-Society-Management-System-
chmod +x run.sh
./run.sh# 🚀 QUICK START GUIDE - macOS

## What's Ready

✅ **Your OCSMS project is ready for macOS!**

Your specified color scheme is fully applied throughout the entire application:
```
🎨 #093C5D (Deep Blue)   - Main backgrounds
🎨 #3B7597 (Mid Blue)    - Card backgrounds  
🎨 #6FD1D7 (Teal)        - Borders & accents
🎨 #5DF8D8 (Mint/Cyan)   - Highlights & buttons
```

---

## 3-Step Setup

### Step 1: Install Java (if not installed)
```bash
# Option A: Homebrew (easiest)
brew install openjdk@17

# Option B: Visit oracle.com and download JDK 17+
# Then follow the installation wizard
```

**Verify:**
```bash
java -version
```

### Step 2: Navigate to Project
```bash
cd /path/to/OCSMS-On-Campus-Society-Management-System-
```

### Step 3: Run the Application
```bash
chmod +x run.sh    # First time only
./run.sh           # Compiles & runs
```

**That's it!** The application should launch with your beautiful blue/teal color scheme.

---

## What You Get

### 🎨 Beautiful UI
- Dark blue backgrounds (#093C5D)
- Teal borders and accents (#6FD1D7)
- Mint/cyan buttons and highlights (#5DF8D8)
- White text for perfect readability

### 🖥️ Cross-Platform
- Runs on Windows (`run.bat`)
- Runs on macOS (`run.sh`)
- Runs on Linux (`run.sh`)
- Fonts automatically adapt to your OS

### 📱 Responsive Design
- Login form with styled fields
- Dashboard with KPI cards
- Data tables with proper coloring
- All buttons with hover effects

---

## Demo Credentials

Use any of these to test (password: `Password1`):

| Role | Roll # |
|------|--------|
| University Admin | 00A-0000 |
| Society Admin | 24P-0557 |
| Faculty Advisor | 24P-0100 |
| Member | 24P-0301 |

---

## Troubleshooting

### "java: command not found"
```bash
# If installed via Homebrew
echo 'export PATH="/usr/local/opt/openjdk@17/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc
java -version
```

### "Permission denied" on run.sh
```bash
chmod +x run.sh
./run.sh
```

### Compilation errors
1. Make sure you're in the project root
2. Check that `lib/gson-2.10.1.jar` exists
3. Try: `cd src && javac -encoding UTF-8 -cp ".:../lib/gson-2.10.1.jar" ocsms/Main.java`

---

## Additional Guides

For more detailed information:

1. **[MACOS_SETUP.md](MACOS_SETUP.md)** - Complete setup guide
2. **[COLOR_GUIDE.md](COLOR_GUIDE.md)** - Color palette details
3. **[VISUAL_REFERENCE.md](VISUAL_REFERENCE.md)** - Visual examples
4. **[IMPLEMENTATION_COMPLETE.md](IMPLEMENTATION_COMPLETE.md)** - Full technical summary

---

## What Was Changed

### Code Updates (0 breaking changes)
- ✅ All fonts updated to cross-platform "SansSerif"
- ✅ Color scheme confirmed throughout application
- ✅ No functionality changes

### New Files Created
- ✅ `run.sh` - Executable script for macOS/Linux
- ✅ 5 documentation files

---

## File Structure

```
OCSMS-On-Campus-Society-Management-System-/
├── run.sh                      ← Use this for macOS/Linux
├── run.bat                     ← Windows users use this
├── MACOS_SETUP.md              ← macOS setup guide
├── COLOR_SCHEME_UPDATE.md      ← Technical details
├── COLOR_GUIDE.md              ← Color reference
├── VISUAL_REFERENCE.md         ← UI examples
├── IMPLEMENTATION_COMPLETE.md  ← Full summary
├── lib/gson-2.10.1.jar         ← Supabase JSON library
└── src/ocsms/                  ← Source code
    ├── Main.java               ← Application entry point
    ├── view/                   ← UI components
    ├── model/                  ← Data models
    ├── controller/             ← MVC controllers
    └── util/UITheme.java       ← Color & font definitions
```

---

## Verification Checklist

After running `./run.sh`, verify:

- [ ] Application window opens
- [ ] Login screen has dark blue background
- [ ] Login button is mint/cyan colored
- [ ] Input fields have teal borders
- [ ] Text is white on dark background
- [ ] All components display colors correctly

---

## Next Steps

1. ✅ Install Java
2. ✅ Run `./run.sh`
3. ✅ Verify the color scheme
4. ✅ Test the application
5. ✅ Share success! 🎉

---

## Support

**Issues on macOS?** → See [MACOS_SETUP.md#Troubleshooting](MACOS_SETUP.md)

**Want to customize colors?** → See [COLOR_GUIDE.md#How-to-Customize](COLOR_GUIDE.md)

**Need technical details?** → See [IMPLEMENTATION_COMPLETE.md](IMPLEMENTATION_COMPLETE.md)

---

**Ready?** Run this command:
```bash
./run.sh
```

Enjoy your beautiful OCSMS application! 🚀
