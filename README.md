# OCSMS — On-Campus Societies Management System
**FAST-NUCES Peshawar | SDA Project**  
**Group:** Umer Abdullah (24P-0557) & Sudais Rehman Khan (24P-0572)

---

## How to Compile & Run

### Prerequisites
- Java JDK 11 or higher
- `lib/gson-2.10.1.jar` (included in project)

### Windows
Simply double-click `run.bat` in the project root folder.

### macOS / Linux
```bash
chmod +x run-macos.sh  # One-time setup
./run-macos.sh
```

For detailed macOS setup, see [MACOS_SETUP.md](MACOS_SETUP.md)

### Manual Compile & Run (All Platforms)
**Windows:**
```batch
cd src
javac -encoding UTF-8 -cp ".;..\lib\gson-2.10.1.jar" ocsms\Main.java
java -cp ".;..\lib\gson-2.10.1.jar" ocsms.Main
```

**macOS/Linux:**
```bash
cd src
javac -encoding UTF-8 -cp ".:../lib/gson-2.10.1.jar" ocsms/Main.java
java -cp ".:../lib/gson-2.10.1.jar" ocsms.Main
```

---

## Demo Login Credentials

All accounts use password: **`Password1`**

| Roll Number | Name            | Role              |
|-------------|-----------------|-------------------|
| `00A-0000`  | University Admin| UNIVERSITY_ADMIN  |
| `24P-0557`  | Umer Abdullah   | SOCIETY_ADMIN     |
| `24P-0572`  | Sudais Rehman   | SOCIETY_ADMIN     |
| `24P-0100`  | Dr. Fahad Shah  | FACULTY_ADVISOR   |
| `24P-0200`  | Ali Treasurer   | TREASURER         |
| `24P-0301`  | Hamza Khan      | MEMBER            |
| `24P-0302`  | Zara Tariq      | MEMBER            |
| `24P-0303`  | Bilal Memon     | MEMBER            |

---

## Implemented Use Cases

| UC    | Description                        | Status |
|-------|------------------------------------|--------|
| UC-01 | User Registration & Login          | ✅ Done |
| UC-02 | Role-Based Access Control          | ✅ Done |
| UC-03 | Membership Application             | ✅ Done |
| UC-04 | Membership Approval/Rejection      | ✅ Done |
| UC-05 | Society Creation                   | ✅ Done |
| UC-06 | Event Creation & Management        | ✅ Done |
| UC-07 | Event Registration                 | ✅ Done |
| UC-08 | Announcements / Notice Board       | ✅ Done |
| UC-09 | Budget & Finance Tracking          | ✅ Done |
| UC-10 | Attendance Tracking                | ✅ Done |
| UC-11 | Society Profile Page               | ✅ Done |
| UC-12 | Feedback & Rating                  | ✅ Done |
| UC-13 | Search & Discovery                 | ✅ Done |
| UC-14 | Resource Booking                   | ✅ Done |
| UC-15 | Election & Voting Module           | ✅ Done |
| UC-16 | Society Performance Dashboard      | ✅ Done |
| UC-17 | Certificate Generation             | ✅ Done |
| UC-18 | Society Deactivation & Archiving   | ✅ Done |
| UC-19 | Multi-Society Joint Events         | ✅ Done |
| UC-20 | Notification System                | ✅ Done |

---

## Design Patterns Implemented

| Pattern   | Classes                                          |
|-----------|--------------------------------------------------|
| Singleton | `DataStore`, `SessionManager`                   |
| Observer  | `NotificationService`, `NotificationListener`   |
| Strategy  | `ExportStrategy`, `PDFExportStrategy`, `ExcelExportStrategy` |
| Factory   | `UserFactory`, `NotificationFactory`            |
| MVC       | All `view/`, `controller/`, `model/` packages   |

---

## Demo Flow (Quick Start)

1. **Login** as `00A-0000` (University Admin) with password `Password1`
2. Go to **Societies** — 3 pre-loaded societies visible
3. Go to **Events** — 5 pre-loaded events (upcoming + past)
4. Go to **Elections** — Active ACM President election (Voting phase open)
   - Login as `24P-0301` (student) to cast a vote
5. Go to **Membership** — Pending application from Zara Tariq to approve
6. Go to **Finance** — 3 finance entries for ACM Chapter
