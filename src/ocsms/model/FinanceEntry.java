package ocsms.model;

import java.time.LocalDate;
import java.util.UUID;

/**
 * MODEL CLASS — FinanceEntry
 * Represents a single income or expense entry for a society's finances.
 */
import java.io.Serializable;
public class FinanceEntry implements Serializable {

    public enum EntryCategory { INCOME, EXPENSE }

    private String id;
    private String societyId;
    private String description;
    private double amount;
    private LocalDate date;
    private EntryCategory category;
    private String receiptPath; // null if no receipt attached

    public FinanceEntry() {
        this.id = UUID.randomUUID().toString();
    }

    public FinanceEntry(String societyId, String description, double amount,
                        LocalDate date, EntryCategory category) {
        this();
        this.societyId = societyId;
        this.description = description;
        this.amount = amount;
        this.date = date;
        this.category = category;
    }

    // ── Getters & Setters ───────────────────────────────────────────────────────

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSocietyId() { return societyId; }
    public void setSocietyId(String societyId) { this.societyId = societyId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public EntryCategory getCategory() { return category; }
    public void setCategory(EntryCategory category) { this.category = category; }

    public String getReceiptPath() { return receiptPath; }
    public void setReceiptPath(String receiptPath) { this.receiptPath = receiptPath; }

    public boolean hasReceipt() { return receiptPath != null && !receiptPath.isEmpty(); }
}
