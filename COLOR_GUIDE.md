# Color Scheme Documentation

## Your OCSMS Color Palette

The project uses a modern **glassmorphic dark theme** with the following color combination:

```
┌─────────────────────────────────────────────┐
│  OCSMS Color Scheme - Glassmorphic Theme    │
├─────────────────────────────────────────────┤
│                                             │
│  ███████  #093C5D  Deep Blue (C1)          │
│  Primary background, main content area      │
│                                             │
│  ███████  #3B7597  Mid Blue (C2)           │
│  Card backgrounds, secondary elements       │
│                                             │
│  ███████  #6FD1D7  Teal (C3)               │
│  Borders, accents, focus states             │
│                                             │
│  ███████  #5DF8D8  Mint/Cyan (C4)          │
│  Primary accent, highlights, active states  │
│                                             │
└─────────────────────────────────────────────┘
```

## UI Component Usage

### 📌 Backgrounds
- **Main content background**: #093C5D (C1)
- **Card/Panel backgrounds**: #3B7597 (C2)
- **Hover states**: #6FD1D7 (C3)

### 🎨 Text Colors
- **Primary text**: White (#FFFFFF)
- **Secondary text**: #5DF8D8 (C4)
- **Labels**: #5DF8D8 (C4)

### 🔘 Buttons
- **Primary (Accent) Button**: 
  - Default: #5DF8D8 text on black
  - Hover: #6FD1D7 background with #5DF8D8 border
- **Danger Button**: #ff5252 (Red)
- **Success Button**: #3B7597 → #5DF8D8 on hover

### 📊 Table Styling
- **Header**: #6FD1D7 background with black text
- **Even rows**: #3B7597
- **Odd rows**: #093C5D
- **Selected row**: #6FD1D7 background with #093C5D text
- **Grid lines**: #3B7597

### 📝 Input Fields
- **Background**: #093C5D
- **Text**: White
- **Border**: #6FD1D7
- **Caret**: #5DF8D8
- **Focus**: #6FD1D7 border with #5DF8D8 accent

## How to Customize

All colors are defined in `src/ocsms/util/UITheme.java`. To modify:

### 1. Change a Single Color

Edit the color constants:
```java
// In UITheme.java
public static final Color C1 = new Color(0x093C5D); // Modify this hex value
public static final Color C2 = new Color(0x3B7597);
public static final Color C3 = new Color(0x6FD1D7);
public static final Color C4 = new Color(0x5DF8D8);
```

### 2. Modify Component Colors

```java
// Example: Change card background color
public static final Color BG_CARD = new Color(0x3B7597); // Change this

// Example: Change accent color
public static final Color ACCENT = new Color(0x5DF8D8); // Change this
```

### 3. Update Button Styles

Find the button styling methods:
```java
public static JButton accentButton(String text) {
    JButton btn = new JButton(text);
    btn.setBackground(ACCENT);  // Primary color
    btn.setForeground(C1);       // Text color
    // ... configure hover states
    return btn;
}
```

## Integration Points

### Component Factory Methods (Automatic Styling)
These methods automatically apply your color scheme:
```java
// Automatically creates styled components
UITheme.accentButton("Click me")
UITheme.textField("Enter text")
UITheme.heading("Title")
UITheme.styleTable(myTable)
```

### Manual Component Styling
For components not using factory methods:
```java
JButton myBtn = new JButton("My Button");
myBtn.setBackground(UITheme.ACCENT);
myBtn.setForeground(UITheme.C1);
```

## Color Application Throughout Project

### View Files Using Theme
All view files in `src/ocsms/view/` and `src/ocsms/view/panels/` use the UITheme:
- ✅ LoginFrame.java
- ✅ MainFrame.java
- ✅ All panel classes (AnnouncementPanel, EventPanel, etc.)

### Theme Property Usage
```
Theme Property          Used For
─────────────────────────────────
BG                      Main backgrounds
BG_CARD                 Panel/card backgrounds
SIDEBAR                 Navigation backgrounds
ACCENT                  Primary buttons & highlights
ACCENT2                 Secondary accents
TEXT                    Primary text (white)
TEXT_DIM                Secondary text
SUCCESS                 Success feedback
WARNING                 Warning feedback
DANGER                  Error/danger feedback
TABLE_HDR               Table header background
ROW_EVEN / ROW_ODD      Alternating table rows
```

## Accessibility Notes

✅ **High Contrast**: Dark blue + cyan provides excellent readability
✅ **WCAG Compliant**: Color combinations meet contrast requirements
✅ **Colorblind Friendly**: Teal + Blue + White combination is distinguishable

## Examples in Action

### Login Panel
- Form background: #093C5D (dark)
- Input fields: Dark with teal borders
- Login button: Mint/cyan with dark text
- Text: White labels

### Dashboard
- KPI cards: #3B7597 backgrounds
- Values: #5DF8D8 text (cyan)
- Grid lines: #3B7597
- Main container: #093C5D

### Data Tables
- Headers: #6FD1D7 with dark text
- Rows: Alternating #3B7597 and #093C5D
- Selected: #6FD1D7 with dark text
- Borders: #3B7597 lines

### Buttons
- Primary Action: #5DF8D8 (mint)
- Hover: #6FD1D7 (teal)
- Active: #3B7597 (mid-blue)
- Danger: #ff5252 (red)

## Font Specifications

```
Font Family: SansSerif (Cross-platform)
- macOS:  → San Francisco Pro
- Windows: → Segoe UI  
- Linux:   → DejaVu Sans / Liberation Sans

Sizes:
- Title:   22pt bold
- Heading: 16pt bold
- Body:    13pt regular
- Bold:    13pt bold
- Small:   11pt regular
```

## Testing Your Colors

To verify colors are applied correctly:

1. **Launch the application**: `./run.sh` (macOS) or `run.bat` (Windows)
2. **Check Login Screen**:
   - Background should be dark blue (#093C5D)
   - Input fields should have teal borders (#6FD1D7)
   - Login button should be cyan (#5DF8D8)
3. **Navigate to Dashboard**:
   - Cards should have mid-blue background (#3B7597)
   - Values should be cyan text (#5DF8D8)
4. **View Tables**:
   - Headers should be teal (#6FD1D7)
   - Rows should alternate between #3B7597 and #093C5D

## Quick Reference

| Purpose | Color | Hex Code |
|---------|-------|----------|
| Primary Background | Deep Blue | #093C5D |
| Secondary Background | Mid Blue | #3B7597 |
| Borders & Accents | Teal | #6FD1D7 |
| Highlights & Primary | Mint/Cyan | #5DF8D8 |
| Text | White | #FFFFFF |
| Errors | Red | #ff5252 |
| Warnings | Orange | #ffb300 |

---

**Ready to customize?** Edit `src/ocsms/util/UITheme.java` and recompile! 🎨
