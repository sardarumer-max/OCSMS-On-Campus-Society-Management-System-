# On-Campus Society Management System (OCSMS) — Enhancement Prompt

## Objective
Enhance the existing hardcoded OCSMS application into a fully dynamic, database-driven system using Supabase. Remove all hardcoded data except the initial University Admin credentials.

---

## Core Requirements

### 1. Authentication System
- Implement Login and Registration system.
- Only authorized roles can log in:
  - University Admin (default hardcoded)
  - Society Admin
  - President
  - Treasurer

### 2. Default Admin
- One default University Admin exists (hardcoded).
- Admin can:
  - Create societies
  - Assign President credentials
  - Manage all societies

---

## 3. Role-Based Features

### University Admin
- Create new societies
- Assign President (name + roll number + credentials)
- Manage all societies globally

### Society Admin / President
- Approve or reject student join requests
- Manage events
- Track members

### Treasurer
- Add funds
- Record expenses
- Upload slips
- Automatically deduct expense from total budget
- Show remaining balance

---

## 4. Student Flow

### Registration
- Student registers using:
  - Roll Number
  - Password

### After Login
- View all active societies
- Request to join a society
- Submit a motivation letter

### Approval System
- Requests go to President/Admin
- Options:
  - Accept
  - Reject

---

## 5. Event Management
- President can:
  - Create events
  - Mark attendance (Present/Absent in real-time)

---

## 6. Database (Supabase Integration)

### Tables Required:
- Users
- Roles
- Societies
- Memberships
- JoinRequests
- Events
- Attendance
- Finance
- Slips

### Requirements:
- No hardcoded data except admin
- All data fetched/stored via Supabase
- Ensure proper relationships (foreign keys)

---

## 7. UI/UX Design Requirements

- Modern, reactive UI
- Glassmorphism / transparency effects
- Smooth animations
- Attractive color scheme
- Clean dashboards
- Engaging buttons (hover/click effects)

---

## 8. Technical Constraints

- Remove all hardcoded users except admin
- Ensure dynamic CRUD operations
- Proper error handling
- Scalable structure

---

## Deliverable
A fully functional upgraded version of the existing system with:
- Supabase backend
- Role-based authentication
- Dynamic data handling
- Modern UI

---

## Notes
Focus on maintainability, scalability, and clean architecture.
