# OCSMS — Java Swing Implementation Prompt
**Course:** Software Design & Analysis | FAST-NUCES Peshawar  
**Project:** On-Campus Societies Management System (OCSMS)  
**Language:** Java (Swing GUI)  
**Group:** Umer Abdullah (24P-0557), Sudais Rehman Khan (24P-0572)

---

You are helping me implement OCSMS (On-Campus Societies Management System) as a Java Swing desktop application for my Software Design & Analysis assignment at FAST-NUCES Peshawar. I already completed Task 1 which included use case modeling, requirements specification, and class diagrams. Now I need to implement it in Java with a GUI.

---

## System Overview

The On-Campus Societies Management System (OCSMS) is a platform developed to digitize and streamline the administration of all student societies at FAST-NUCES. It covers society registration, membership handling, event coordination, financial tracking, elections, venue reservations, and member feedback.

---

## Assignment Requirements

- Java language only (no Kotlin, Scala, or other JVM languages)
- GUI using Java Swing (JFrame, JPanel, JTable, JTabbedPane, etc.)
- GUI must reflect at least 2-3 core use cases
- Design must be visible in code (classes, attributes, methods, relationships)
- Implement all functional requirements identified in Task 1
- Handle basic errors with user-friendly JOptionPane messages
- Submit on GitHub (public repo) with README.md (compile & run instructions)
- Make a demo video of the project

---

## User Roles (5 Roles with Distinct Permissions)

| Role | Permissions |
|------|-------------|
| `UNIVERSITY_ADMIN` | Full system control; deactivate societies; view all reports |
| `SOCIETY_ADMIN` | Create events; approve/reject members; book venues; run elections |
| `FACULTY_ADVISOR` | Approve budgets; view financial summaries; supervise elections |
| `TREASURER` | Record income/expenses; generate financial reports |
| `MEMBER/STUDENT` | Join societies; register for events; vote; submit feedback |

---

## Domain Model — Classes to Implement

```
User             → id, rollNumber, name, email, passwordHash, role, isLocked, failedAttempts
Society          → id, name, category, description, advisorId, capacity, status (ACTIVE/ARCHIVED), memberIds[]
Membership       → id, studentId, societyId, motivationStatement, status (PENDING/APPROVED/REJECTED), remarks, appliedAt
Event            → id, title, dateTime, venue, capacity, eventType, posterPath, societyId, registeredMemberIds[]
Announcement     → id, title, body, societyId, isDraft, publishedAt
FinanceEntry     → id, societyId, description, amount, date, category (INCOME/EXPENSE), receiptPath
AttendanceRecord → eventId, memberId, status (PRESENT/ABSENT), markedAt, isLateEntry
Election         → id, societyId, position, nominationDeadline, votingDeadline, nominees[], votes{memberId→nomineeId}
Booking          → id, resourceType, resourceName, societyId, dateTime, durationHours, status
Notification     → id, userId, message, notificationType, createdAt, isRead
Feedback         → id, eventId, memberId, rating (1-5), comment, isAnonymous
Certificate      → id, memberId, eventId, verificationCode, generatedAt
```

---

## All 20 Use Cases — Full Detail

### UC-01: User Registration & Login
- **Actors:** All Users
- **Flow:** New user registers with Roll Number, Name, Email, Password, Role. System validates roll number format (e.g. 24P-0557). Password is hashed and account is created. Returning user logs in; system authenticates and redirects to dashboard.
- **Alternate:** Invalid roll number → error message. 5 failed login attempts → account locked for 15 minutes. 30 minutes idle → auto logout.
- **Java GUI:** LoginFrame with two tabs — Login and Register. JTextField for roll number, JPasswordField for password. JComboBox for role selection on register.

### UC-02: Role-Based Access Control
- **Actors:** All Users
- **Flow:** After login, system retrieves user role and generates permission set. All navigation menus filtered based on role. Unauthorized access attempts blocked with error dialog.
- **Java GUI:** MainFrame sidebar buttons shown/hidden based on role. `SessionManager.getInstance().getCurrentUser().getRole()` used everywhere.

### UC-03: Membership Application
- **Actors:** Students
- **Flow:** Student navigates to Discover Societies. Clicks Apply — system shows motivation statement form (50-500 chars). Application saved with PENDING status and timestamp. Society Admin gets notified.
- **Alternate:** Already a member → "You are already a member." Society full → "This society has reached its member limit." Pending app exists → "Your application is under review."
- **Java GUI:** JTextArea for motivation with live character counter. JTable listing available societies to browse.

### UC-04: Membership Approval / Rejection
- **Actors:** Society Admin
- **Flow:** Admin views Pending Applications table. Selects an application, clicks Approve or Reject. If rejecting, mandatory remarks dialog appears. Status updated, applicant notified via notification system.
- **Alternate:** No pending applications → "No applications at this time."
- **Java GUI:** JTable with columns: Roll No, Name, Society, Applied Date, Motivation. Approve and Reject buttons below table. JOptionPane input dialog for rejection remarks.

### UC-05: Society Creation
- **Actors:** Faculty Advisor, University Admin
- **Flow:** Admin fills society form: name, category, description, advisor, max capacity. System validates no duplicate society name. Society created and visible on discover page.
- **Java GUI:** JTextField, JComboBox for category, JSpinner for capacity.

### UC-06: Event Creation & Management
- **Actors:** Society Admin
- **Flow:** Admin fills event form: title, date/time, venue, capacity, type, poster image. System checks venue availability before saving (conflict check against Bookings). Event published; all society members notified. Cancel event option → confirms → cancels all registrations → notifies members.
- **Alternate:** Venue not available → warn and suggest different slot. Missing fields → highlight and block submission.
- **Java GUI:** JTextField, JSpinner for date/time, JFileChooser for poster (max 5MB). Venue conflict check against Booking list in DataStore.

### UC-07: Event Registration
- **Actors:** Members
- **Flow:** Member views event list, clicks Register. System checks: capacity not exceeded + registration deadline not passed. Registration saved; confirmation notification sent.
- **Alternate:** Full capacity → offer waitlist. Deadline passed → "Registration for this event is closed." Already registered → "You are already registered."
- **Java GUI:** Events JTable → Register button → JOptionPane confirmation.

### UC-08: Announcement / Notice Board
- **Actors:** Society Admin, Members
- **Flow:** Admin composes announcement (title + body, max 2000 chars). Draft toggle saves without notifying; Publish sends to all members. Members see announcements on Notice Board panel.
- **Java GUI:** JTextArea with 2000 char counter. JToggleButton for Draft vs Publish. Scrollable announcement list panel for members.

### UC-09: Budget & Finance Tracking
- **Actors:** Treasurer, Faculty Advisor
- **Flow:** Treasurer creates entry: description, amount, date, category, receipt file. Running balance auto-updates after each entry. Faculty Advisor can view full financial summary anytime. Treasurer can export report for a date range (PDF or Excel).
- **Alternate:** Receipt upload fails → entry saved marked "No Receipt Attached." Balance goes negative → warning (label turns red) but still saves.
- **Java GUI:** JTable of entries + Add Entry form. Running balance JLabel (red when negative). Export button with strategy selection (PDF/Excel).

### UC-10: Attendance Tracking
- **Actors:** Society Admin
- **Flow:** Admin opens event management page → Mark Attendance. JTable lists all registered members. Admin marks each as Present or Absent (toggle button per row). System saves with timestamp; late entries (48h+) flagged. Per-event attendance percentage shown in footer.
- **Java GUI:** JTable with Present/Absent toggle column. Row background green (Present) or red (Absent). Attendance % label at bottom.

### UC-11: Society Profile Page
- **Actors:** All Users
- **Flow:** User clicks society → profile panel shows: name, category, description, member count, events list, advisor name, achievements. Archived societies shown read-only with "ARCHIVED" banner. Authenticated members see Join/Applied/Member status button.
- **Java GUI:** Read-only JPanel with labels. Join button visible for students.

### UC-12: Feedback & Rating
- **Actors:** Members, Society Admin
- **Flow:** After event ends, member opens feedback form. Selects 1-5 star rating + optional written comment. Anonymous by default (opt-in checkbox). Admin views aggregated ratings per event.
- **Alternate:** Feedback window closed (7 days post-event) → "Feedback period ended."
- **Java GUI:** JRadioButtons for stars, JTextArea for comment, JCheckBox for anonymous. Admin view: bar chart of ratings using Graphics2D.

### UC-13: Search & Discovery
- **Actors:** Students, Guests
- **Flow:** User enters keyword or selects category filter. System returns matching societies within 2 seconds. Active/inactive filter available. Archived societies appear greyed out. Guest can search but cannot apply — prompted to login.
- **Java GUI:** JTextField search + JComboBox category + JToggleButton active filter. Results in scrollable JTable.

### UC-14: Resource Booking
- **Actors:** Society Admin, University Admin
- **Flow:** Admin selects resource type, date, time slot. System checks conflicts against existing bookings. If no conflict → booking confirmed, notification sent. If conflict → warning dialog + suggestion of nearest available slot.
- **Special:** Bookings must be made at least 24 hours in advance.
- **Java GUI:** JComboBox resource type, JSpinner date, JComboBox time slot. Conflict detection logic in BookingController.

### UC-15: Election & Voting Module
- **Actors:** Members, Faculty Advisor
- **Flow:** Admin creates election: position name, nomination start/end, voting start/end. Members submit nominations during nomination phase. Voting phase opens: each member sees nominee list, casts exactly ONE vote. Vote enforced by roll number (cannot vote twice). Results auto-published when voting phase ends. Tie → admin notified to decide tiebreaker.
- **Alternate:** Duplicate vote attempt → "You have already cast your vote." No nominations → admin can extend nomination period.
- **Java GUI:** Create Election form, Nominations panel, Voting panel with JRadioButtons. Results panel with winner display and vote counts.

### UC-16: Society Performance Dashboard
- **Actors:** Faculty Advisor, University Admin
- **Flow:** Dashboard shows KPIs: active members, events held, attendance %, budget use. Filter by society and date range. Export report as PDF or Excel.
- **Java GUI:** KPI metric cards (custom JPanel), bar chart painted with Graphics2D. JComboBox society filter, JSpinner date range.

### UC-17: Certificate Generation
- **Actors:** Members, Society Admin
- **Flow:** After attendance is marked (UC-10), system auto-generates certificates. Certificate includes: member name, society name, event name, date, unique verification code. Member notified; certificate available for download on dashboard.
- **Java GUI:** Download button in member dashboard → JFileChooser save dialog. PDF generated using iText library OR formatted plain text file.

### UC-18: Society Deactivation & Archiving
- **Actors:** University Admin only
- **Flow:** Admin views society profile → clicks Deactivate. System asks for reason + JOptionPane confirm dialog. On confirm: society status = ARCHIVED, active events cancelled, all members notified, data becomes read-only.
- **Special:** Archived data retained minimum 3 years.
- **Java GUI:** Deactivate button visible only to UNIVERSITY_ADMIN role.

### UC-19: Multi-Society Joint Events
- **Actors:** Society Admins
- **Flow:** Creating admin checks "Make Joint Event" on event form. Selects up to 4 other society admins to invite. Invited admins get notification and accept/decline. Accepted co-admins gain edit access to the event. Joint event appears on all participating societies' profiles.
- **Java GUI:** Checkbox on event form + multi-select society invite dialog.

### UC-20: Notification System
- **Actors:** All Users
- **Flow:** Triggered by: membership decisions, event creation, announcements, vote results, booking confirmations, certificate availability. In-app notification panel shows unread count badge. Notification log with timestamps shown per user. Email notifications simulated by writing to a log file.
- **Java GUI:** Bell icon button in toolbar with painted unread count. NotificationPanel: scrollable list of notifications, mark-as-read button.

---

## Design Patterns to Implement

### 1. Observer Pattern
- **Problem it solves:** When membership is approved, event is created, or vote is cast — multiple UI panels need to update automatically without being tightly coupled to each other.
- **Implementation:**
  - `NotificationListener` interface with `update(Notification n)` method
  - `NotificationService` (Subject) maintains list of listeners
  - `NotificationPanel`, `DashboardPanel` implement `NotificationListener`
  - Any controller calls `NotificationService.getInstance().notify(n)`
  - All registered panels update automatically
- **Used in:** UC-20, UC-03, UC-04, UC-06, UC-07, UC-08, UC-15

### 2. Singleton Pattern
- **Problem it solves:** Only ONE session should exist at a time; only ONE data store should hold all in-memory objects.
- **Implementation:**
  - `SessionManager.getInstance()` — holds current logged-in User
  - `DataStore.getInstance()` — holds all ArrayLists (users, societies, events, etc.)
  - Both have private constructors + static `getInstance()` method
- **Used in:** UC-01, UC-02, and every panel that reads/writes data

### 3. Strategy Pattern
- **Problem it solves:** UC-09 and UC-16 need export in PDF and Excel. Without Strategy, if/else blocks would violate Open/Closed Principle.
- **Implementation:**
  - `ExportStrategy` interface with `export(List data, String filePath)` method
  - `PDFExportStrategy` implements ExportStrategy
  - `ExcelExportStrategy` implements ExportStrategy
  - `ExportService.export(ExportStrategy strategy, data)` called from UI
  - User selects format from JComboBox; correct strategy injected at runtime
- **Used in:** UC-09, UC-16

### 4. Factory Pattern
- **Problem it solves:** Creating User objects with 5 different roles involves role-specific setup. Without Factory this logic is duplicated everywhere.
- **Implementation:**
  - `UserFactory.createUser(UserRole role, String rollNumber, String name, ...)`
  - `NotificationFactory.create(NotificationType type, String message, String userId)`
- **Used in:** UC-01 (registration), UC-20 (notification creation)

### 5. MVC Architecture
- Every Swing panel = **View** (no business logic inside ActionListeners)
- Every Controller class = handles logic, updates model, triggers notifications
- Every Model class = plain Java object (POJO) with getters/setters

---

## Project Folder Structure

```
src/
└── ocsms/
    ├── Main.java                        ← Entry point; sets dark LAF; shows LoginFrame
    ├── model/
    │   ├── User.java
    │   ├── Society.java
    │   ├── Membership.java
    │   ├── Event.java
    │   ├── Announcement.java
    │   ├── FinanceEntry.java
    │   ├── AttendanceRecord.java
    │   ├── Election.java
    │   ├── Booking.java
    │   ├── Notification.java
    │   ├── Feedback.java
    │   └── Certificate.java
    ├── controller/
    │   ├── AuthController.java
    │   ├── SocietyController.java
    │   ├── MembershipController.java
    │   ├── EventController.java
    │   ├── FinanceController.java
    │   ├── ElectionController.java
    │   ├── BookingController.java
    │   └── NotificationController.java
    ├── view/
    │   ├── LoginFrame.java
    │   ├── MainFrame.java
    │   └── panels/
    │       ├── DashboardPanel.java
    │       ├── MembershipPanel.java
    │       ├── EventPanel.java
    │       ├── AnnouncementPanel.java
    │       ├── FinancePanel.java
    │       ├── AttendancePanel.java
    │       ├── ElectionPanel.java
    │       ├── BookingPanel.java
    │       ├── SearchPanel.java
    │       └── NotificationPanel.java
    ├── service/
    │   ├── NotificationService.java     ← Observer Subject
    │   ├── AuthService.java
    │   └── ExportService.java           ← Strategy context
    ├── util/
    │   ├── SessionManager.java          ← Singleton
    │   ├── DataStore.java               ← Singleton with pre-loaded demo data
    │   ├── ValidationUtil.java
    │   └── DateUtil.java
    └── pattern/
        ├── observer/
        │   └── NotificationListener.java
        ├── strategy/
        │   ├── ExportStrategy.java
        │   ├── PDFExportStrategy.java
        │   └── ExcelExportStrategy.java
        └── factory/
            ├── UserFactory.java
            └── NotificationFactory.java
```

---

## GUI Design Specifications

| Property | Value |
|----------|-------|
| Theme | Dark — background `#0a0f1e`, text `#e0eeff`, accent `#00d4ff` |
| Font | Segoe UI, 13pt body, 16pt headings |
| Frame size | Minimum 1200x750, centered on screen |
| Layout | BorderLayout main frame |
| Sidebar | Custom painted dark JPanel (180px wide), nav buttons per role |
| Content area | CardLayout — each panel is a card switched by sidebar |
| Tables | Custom JTable cell renderer for status-based row colors |
| Errors | `JOptionPane.showMessageDialog` for all validation failures |
| Confirms | `JOptionPane.showConfirmDialog` for all destructive actions |
| Notifications | Bell button in toolbar, painted integer badge for unread count |

---

## Validation Rules

- Roll Number: must match `\d{2}P-\d{4}` format (e.g. `24P-0557`)
- Password: minimum 8 characters, at least 1 digit
- Motivation statement: 50 to 500 characters
- Event capacity: positive integer only
- Budget amount: positive decimal, reject negative input
- Poster file: `.jpg` or `.png` only, max 5MB
- Receipt file: `.pdf` or `.jpg` only
- Event date: must be at least 1 day in the future
- Booking: must be made at least 24 hours in advance
- Election: voting period must start after nomination period ends
- Announcement body: maximum 2000 characters

---

## Sample / Demo Data (Pre-load in DataStore)

**Users:**

| Username | Roll No | Role |
|----------|---------|------|
| admin | 00A-0000 | UNIVERSITY_ADMIN |
| umer | 24P-0557 | SOCIETY_ADMIN (ACM Chapter) |
| sudais | 24P-0572 | SOCIETY_ADMIN (Drama Club) |
| advisor | 24P-0100 | FACULTY_ADVISOR |
| treasurer | 24P-0200 | TREASURER |
| student1 | 24P-0301 | MEMBER |
| student2 | 24P-0302 | MEMBER |
| student3 | 24P-0303 | MEMBER |

**Societies:**
- ACM Chapter (category: Technology, capacity: 50, status: ACTIVE)
- Drama Club (category: Arts, capacity: 30, status: ACTIVE)
- Sports Society (category: Sports, capacity: 60, status: ACTIVE)

**Events (mix of past and upcoming):**
- CodeFest 2025 / ACM Chapter / upcoming
- Web Dev Workshop / ACM Chapter / past
- Annual Drama Night / Drama Club / upcoming
- Football Tournament / Sports / upcoming
- AI Seminar / ACM Chapter / past

**Memberships (mix of statuses):**
- student1 applied to ACM Chapter → APPROVED
- student2 applied to ACM Chapter → PENDING
- student3 applied to Drama Club → REJECTED (remarks: "Capacity full")

**Finance entries for ACM Chapter:**
- INCOME: Membership Fees / Rs.5000 / Jan 2025
- EXPENSE: Event Supplies / Rs.2000 / Feb 2025
- INCOME: Sponsorship / Rs.8000 / Mar 2025

**Active Election:**
- Society: ACM Chapter
- Position: President
- Nominees: student1, student2
- Status: Voting Phase Open

---

## Suggested Build Order

1. All model classes (User, Society, Event, etc.)
2. DataStore singleton with demo data pre-loaded
3. SessionManager singleton
4. LoginFrame (UC-01) + AuthController + AuthService
5. MainFrame with sidebar + CardLayout shell
6. MembershipPanel (UC-03 + UC-04) — highest value demo
7. EventPanel (UC-06 + UC-07)
8. FinancePanel (UC-09) + ExportStrategy pattern
9. ElectionPanel (UC-15)
10. NotificationService (Observer pattern) + NotificationPanel (UC-20)
11. AttendancePanel (UC-10) + CertificateService (UC-17)
12. SearchPanel (UC-13) + SocietyProfilePanel (UC-11)
13. BookingPanel (UC-14)
14. DashboardPanel with KPIs (UC-16)

---

## Important Notes

- No database required — all data lives in DataStore (in-memory ArrayLists)
- No Spring, no Hibernate, no external frameworks except iText (PDF) if needed
- All code must follow MVC — zero business logic inside ActionListeners
- Every design pattern must be clearly named in comments so it is visible
- Code must be understandable — I will be asked to explain it in viva
- README.md must include: how to compile (javac), how to run (java), list of demo login credentials, and which use cases are implemented

---

**Now please start by generating all the Model classes first, then I will ask you to continue with the next components one by one.**
