package ocsms.util;

import ocsms.model.*;
import ocsms.model.Election.ElectionPhase;
import ocsms.model.Event.EventStatus;
import ocsms.model.Event.EventType;
import ocsms.model.FinanceEntry.EntryCategory;
import ocsms.model.Membership.MembershipStatus;
import ocsms.model.Society.SocietyCategory;
import ocsms.model.Society.SocietyStatus;
import ocsms.model.User.UserRole;
import ocsms.model.AttendanceRecord.AttendanceStatus;
import ocsms.model.Booking.BookingStatus;
import ocsms.model.Booking.ResourceType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.io.*;

/**
 * SINGLETON PATTERN — DataStore
 * Central in-memory data repository. Always pre-loaded with full demo data
 * matching the OCSMS_Prompt spec. Supabase users/societies are merged on top.
 */
public class DataStore implements Serializable {

    private static final long serialVersionUID = 2L;
    private static DataStore instance;

    // ── Data Collections ────────────────────────────────────────────────────────
    private List<User>             users             = new ArrayList<>();
    private List<Society>          societies         = new ArrayList<>();
    private List<Membership>       memberships       = new ArrayList<>();
    private List<Event>            events            = new ArrayList<>();
    private List<Announcement>     announcements     = new ArrayList<>();
    private List<FinanceEntry>     financeEntries    = new ArrayList<>();
    private List<AttendanceRecord> attendanceRecords = new ArrayList<>();
    private List<Election>         elections         = new ArrayList<>();
    private List<Booking>          bookings          = new ArrayList<>();
    private List<Notification>     notifications     = new ArrayList<>();
    private List<Feedback>         feedbacks         = new ArrayList<>();
    private List<Certificate>      certificates      = new ArrayList<>();

    private DataStore() {}

    /** SINGLETON: returns the single global DataStore instance */
    public static DataStore getInstance() {
        if (instance == null) {
            instance = buildWithDemoData();
        }
        return instance;
    }

    public static void saveData() { /* Supabase sync handled per-controller */ }

    // ── Full Demo Data Seed ─────────────────────────────────────────────────────
    private static DataStore buildWithDemoData() {
        DataStore ds = new DataStore();

        // ── 1. USERS ──────────────────────────────────────────────────────────
        User admin     = new User("00A-0000", "University Admin", "admin@fast.edu.pk",   "Password1", UserRole.UNIVERSITY_ADMIN);
        User umer      = new User("24P-0557", "Umer Abdullah",    "umer@fast.edu.pk",    "Password1", UserRole.SOCIETY_ADMIN);
        User sudais    = new User("24P-0572", "Sudais Rehman",    "sudais@fast.edu.pk",  "Password1", UserRole.SOCIETY_ADMIN);
        User advisor   = new User("24P-0100", "Dr. Fahad Shah",   "fahad@fast.edu.pk",   "Password1", UserRole.FACULTY_ADVISOR);
        User treasurer = new User("24P-0200", "Ali Treasurer",    "ali@fast.edu.pk",     "Password1", UserRole.TREASURER);
        User student1  = new User("24P-0301", "Hamza Khan",       "hamza@fast.edu.pk",   "Password1", UserRole.MEMBER);
        User student2  = new User("24P-0302", "Zara Tariq",       "zara@fast.edu.pk",    "Password1", UserRole.MEMBER);
        User student3  = new User("24P-0303", "Bilal Memon",      "bilal@fast.edu.pk",   "Password1", UserRole.MEMBER);

        ds.users.add(admin);
        ds.users.add(umer);
        ds.users.add(sudais);
        ds.users.add(advisor);
        ds.users.add(treasurer);
        ds.users.add(student1);
        ds.users.add(student2);
        ds.users.add(student3);

        // ── 2. SOCIETIES ──────────────────────────────────────────────────────
        Society acm    = new Society("ACM Chapter",   SocietyCategory.TECHNOLOGY, "Computing & tech society.", advisor.getId(), 50);
        Society drama  = new Society("Drama Club",    SocietyCategory.ARTS,       "Theatre & performing arts.", advisor.getId(), 30);
        Society sports = new Society("Sports Society",SocietyCategory.SPORTS,     "Campus sports & athletics.", advisor.getId(), 60);

        acm.addMember(umer.getId());
        acm.addMember(student1.getId());
        drama.addMember(sudais.getId());
        sports.addMember(student2.getId());

        ds.societies.add(acm);
        ds.societies.add(drama);
        ds.societies.add(sports);

        // ── 3. EVENTS ─────────────────────────────────────────────────────────
        Event e1 = new Event("CodeFest 2025",     LocalDateTime.now().plusDays(14),  "Auditorium A",  100, EventType.WORKSHOP, acm.getId());
        Event e2 = new Event("Web Dev Workshop",  LocalDateTime.now().minusDays(30), "Lab 301",       40,  EventType.WORKSHOP, acm.getId());
        Event e3 = new Event("Annual Drama Night",LocalDateTime.now().plusDays(7),   "Main Hall",     200, EventType.CULTURAL, drama.getId());
        Event e4 = new Event("Football Tournament",LocalDateTime.now().plusDays(21), "Sports Ground", 150, EventType.SPORTS,   sports.getId());
        Event e5 = new Event("AI Seminar",        LocalDateTime.now().minusDays(60), "Room 210",      60,  EventType.SEMINAR,  acm.getId());

        e1.setStatus(EventStatus.UPCOMING);
        e2.setStatus(EventStatus.PAST);
        e3.setStatus(EventStatus.UPCOMING);
        e4.setStatus(EventStatus.UPCOMING);
        e5.setStatus(EventStatus.PAST);

        e1.getRegisteredMemberIds().add(student1.getId());

        ds.events.add(e1); ds.events.add(e2); ds.events.add(e3);
        ds.events.add(e4); ds.events.add(e5);

        // ── 4. MEMBERSHIPS ────────────────────────────────────────────────────
        Membership m1 = new Membership(student1.getId(), acm.getId(),   "I am passionate about computing and ACM activities.");
        m1.setStatus(MembershipStatus.APPROVED);
        Membership m2 = new Membership(student2.getId(), acm.getId(),   "I love technology and want to contribute to ACM.");
        m2.setStatus(MembershipStatus.PENDING);
        Membership m3 = new Membership(student3.getId(), drama.getId(), "Acting and drama is my greatest passion and ambition!");
        m3.setStatus(MembershipStatus.REJECTED);
        m3.setRemarks("Capacity full at time of application.");

        ds.memberships.add(m1); ds.memberships.add(m2); ds.memberships.add(m3);

        // ── 5. FINANCE ENTRIES ────────────────────────────────────────────────
        FinanceEntry f1 = new FinanceEntry(acm.getId(), "Membership Fees", 5000.0, LocalDate.of(2025, 1, 15), EntryCategory.INCOME);
        FinanceEntry f2 = new FinanceEntry(acm.getId(), "Event Supplies",  2000.0, LocalDate.of(2025, 2, 10), EntryCategory.EXPENSE);
        FinanceEntry f3 = new FinanceEntry(acm.getId(), "Sponsorship",     8000.0, LocalDate.of(2025, 3,  5), EntryCategory.INCOME);

        ds.financeEntries.add(f1); ds.financeEntries.add(f2); ds.financeEntries.add(f3);

        // ── 6. ELECTION (Voting phase open, per spec) ─────────────────────────
        Election election = new Election(
                acm.getId(), "President",
                LocalDateTime.now().minusDays(10), LocalDateTime.now().minusDays(3),
                LocalDateTime.now().minusDays(2),  LocalDateTime.now().plusDays(5));
        election.setPhase(ElectionPhase.VOTING);
        election.getNominees().add(student1.getId());
        election.getNominees().add(student2.getId());
        ds.elections.add(election);

        // ── 7. ANNOUNCEMENTS ──────────────────────────────────────────────────
        ds.announcements.add(new Announcement("CodeFest Registration Open",
                "Register now for CodeFest 2025! Prizes worth Rs.50,000 await the winners.", acm.getId(), false));
        ds.announcements.add(new Announcement("Drama Club Auditions",
                "Auditions for Annual Drama Night will be held next week in Room 202.", drama.getId(), false));

        // ── 8. ATTENDANCE RECORDS ─────────────────────────────────────────────
        AttendanceRecord ar1 = new AttendanceRecord(e2.getId(), student1.getId(), AttendanceStatus.PRESENT);
        AttendanceRecord ar2 = new AttendanceRecord(e5.getId(), student1.getId(), AttendanceStatus.PRESENT);
        ds.attendanceRecords.add(ar1);
        ds.attendanceRecords.add(ar2);

        // ── 9. BOOKINGS ───────────────────────────────────────────────────────
        Booking b1 = new Booking(ResourceType.AUDITORIUM, "Auditorium A", acm.getId(), LocalDateTime.now().plusDays(14), 4.0);
        b1.setStatus(BookingStatus.CONFIRMED);
        ds.bookings.add(b1);

        // ── 10. FEEDBACK ──────────────────────────────────────────────────────
        ds.feedbacks.add(new Feedback(e5.getId(), student1.getId(), 4, "Very informative session!", false));
        ds.feedbacks.add(new Feedback(e2.getId(), student1.getId(), 5, "Excellent workshop, learned a lot!", true));

        // ── 11. CERTIFICATES ──────────────────────────────────────────────────
        ds.certificates.add(new Certificate(student1.getId(), e2.getId(), acm.getId()));

        // ── 12. MERGE REMOTE SUPABASE DATA (non-blocking) ─────────────────────
        if (SupabaseConfig.isConfigured()) {
            mergeFromSupabase(ds);
        }

        return ds;
    }

    /**
     * Merges remote Supabase records into the demo data, preventing duplicates.
     */
    private static void mergeFromSupabase(DataStore ds) {
        try {
            System.out.println("Syncing remote data from Supabase...");
            List<User> remoteUsers = SupabaseClient.fetchTable("users", User[].class);
            for (User r : remoteUsers) {
                boolean exists = ds.users.stream().anyMatch(u -> u.getRollNumber().equalsIgnoreCase(r.getRollNumber()));
                if (!exists) ds.users.add(r);
            }
            List<Society> remoteSoc = SupabaseClient.fetchTable("societies", Society[].class);
            for (Society r : remoteSoc) {
                boolean exists = ds.societies.stream().anyMatch(s -> s.getName().equalsIgnoreCase(r.getName()));
                if (!exists) ds.societies.add(r);
            }
        } catch (Exception ex) {
            System.err.println("Supabase sync error (demo data still loaded): " + ex.getMessage());
        }
    }

    // ── Accessor Methods ────────────────────────────────────────────────────────
    public List<User>             getUsers()             { return users; }
    public List<Society>          getSocieties()         { return societies; }
    public List<Membership>       getMemberships()       { return memberships; }
    public List<Event>            getEvents()            { return events; }
    public List<Announcement>     getAnnouncements()     { return announcements; }
    public List<FinanceEntry>     getFinanceEntries()    { return financeEntries; }
    public List<AttendanceRecord> getAttendanceRecords() { return attendanceRecords; }
    public List<Election>         getElections()         { return elections; }
    public List<Booking>          getBookings()          { return bookings; }
    public List<Notification>     getNotifications()     { return notifications; }
    public List<Feedback>         getFeedbacks()         { return feedbacks; }
    public List<Certificate>      getCertificates()      { return certificates; }

    // ── Lookup Helpers ──────────────────────────────────────────────────────────
    public User findUserById(String id) {
        if (id == null) return null;
        return users.stream().filter(u -> u.getId().equals(id)).findFirst().orElse(null);
    }
    public User findUserByRollNumber(String rollNo) {
        if (rollNo == null) return null;
        return users.stream().filter(u -> u.getRollNumber().equalsIgnoreCase(rollNo)).findFirst().orElse(null);
    }
    public Society findSocietyById(String id) {
        if (id == null) return null;
        return societies.stream().filter(s -> s.getId().equals(id)).findFirst().orElse(null);
    }
    public Event findEventById(String id) {
        if (id == null) return null;
        return events.stream().filter(e -> e.getId().equals(id)).findFirst().orElse(null);
    }
    public Election findElectionById(String id) {
        if (id == null) return null;
        return elections.stream().filter(e -> e.getId().equals(id)).findFirst().orElse(null);
    }
    public Membership findMembershipById(String id) {
        if (id == null) return null;
        return memberships.stream().filter(m -> m.getId().equals(id)).findFirst().orElse(null);
    }
}
