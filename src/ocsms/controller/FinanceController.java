package ocsms.controller;

import ocsms.model.FinanceEntry;
import ocsms.model.FinanceEntry.EntryCategory;
import ocsms.util.DataStore;
import ocsms.util.ValidationUtil;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CONTROLLER (MVC) — FinanceController
 * Handles finance entry creation and reporting for UC-09.
 */
public class FinanceController {

    /**
     * Adds a new finance entry.
     * UC-09: validates positive amount, date, receipt (optional).
     */
    public String addEntry(String societyId, String description, double amount,
                            LocalDate date, EntryCategory category, String receiptPath) {

        if (!ValidationUtil.isNotBlank(description)) return "Description cannot be empty.";
        if (!ValidationUtil.isValidAmount(amount))   return "Amount must be a positive number.";
        if (date == null)                             return "Date is required.";

        // Receipt validation (optional but must be valid format if provided)
        if (receiptPath != null && !receiptPath.isBlank()) {
            if (!ValidationUtil.isValidReceiptFile(receiptPath)) {
                return "Receipt must be a .pdf or .jpg file.";
            }
        }

        FinanceEntry entry = new FinanceEntry(societyId, description.trim(), amount, date, category);
        if (receiptPath != null && !receiptPath.isBlank()) {
            entry.setReceiptPath(receiptPath);
        }

        DataStore.getInstance().getFinanceEntries().add(entry);
        return null; // success
    }

    /** Returns all finance entries for a society */
    public List<FinanceEntry> getEntriesForSociety(String societyId) {
        return DataStore.getInstance().getFinanceEntries().stream()
                .filter(e -> e.getSocietyId().equals(societyId))
                .collect(Collectors.toList());
    }

    /** Returns entries filtered by date range */
    public List<FinanceEntry> getEntriesForRange(String societyId, LocalDate from, LocalDate to) {
        return getEntriesForSociety(societyId).stream()
                .filter(e -> !e.getDate().isBefore(from) && !e.getDate().isAfter(to))
                .collect(Collectors.toList());
    }

    /** Calculates running balance (total income - total expenses) */
    public double getBalance(String societyId) {
        return getEntriesForSociety(societyId).stream()
                .mapToDouble(e -> e.getCategory() == EntryCategory.INCOME ? e.getAmount() : -e.getAmount())
                .sum();
    }

    /** Total income for a society */
    public double getTotalIncome(String societyId) {
        return getEntriesForSociety(societyId).stream()
                .filter(e -> e.getCategory() == EntryCategory.INCOME)
                .mapToDouble(FinanceEntry::getAmount).sum();
    }

    /** Total expenses for a society */
    public double getTotalExpenses(String societyId) {
        return getEntriesForSociety(societyId).stream()
                .filter(e -> e.getCategory() == EntryCategory.EXPENSE)
                .mapToDouble(FinanceEntry::getAmount).sum();
    }
}
