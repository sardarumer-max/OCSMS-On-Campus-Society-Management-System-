package ocsms;

import ocsms.view.LoginFrame;

import javax.swing.*;

/**
 * ENTRY POINT — Main
 * Initializes the application, sets system properties (like dark LAF if supported),
 * and launches the LoginFrame on the Event Dispatch Thread.
 */
public class Main {
    public static void main(String[] args) {
        // Attempt to set a cross-platform look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}

        // Save data to 'database.dat' when the application is closed
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Saving database state...");
            ocsms.util.DataStore.saveData();
        }));

        // Launch GUI on EDT
        SwingUtilities.invokeLater(LoginFrame::new);
    }
}
