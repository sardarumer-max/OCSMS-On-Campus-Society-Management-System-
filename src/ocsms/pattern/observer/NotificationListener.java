package ocsms.pattern.observer;

import ocsms.model.Notification;

/**
 * OBSERVER PATTERN — NotificationListener (Observer Interface)
 * Any UI panel that needs to react to new notifications implements this interface.
 * Used in: UC-20, UC-03, UC-04, UC-06, UC-07, UC-08, UC-15
 */
public interface NotificationListener {

    /**
     * Called by NotificationService when a new notification is dispatched.
     * @param n The newly created Notification object
     */
    void onNotification(Notification n);
}
