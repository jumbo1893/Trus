package com.jumbo.trus.main;

import android.content.SharedPreferences;
import android.util.Log;

import com.jumbo.trus.notification.Notification;
import com.jumbo.trus.user.User;

import java.util.List;
import java.util.Objects;

public class NotificationBadgeCounter {

    public static final int MAX_NUMBER = 9;
    private boolean firstInit;
    private SharedPreferences preferences;
    private Notification lastNotification;
    private Notification lastReadNotification;
    private User user;

    public NotificationBadgeCounter(SharedPreferences preferences, User user) {
        firstInit = true;
        this.preferences = preferences;
        this.user = user;
    }

    /**
     * @param notifications seznam notifikací z db
     * @return poslední nepřečtená notifikace
     */
    public int returnNumberOfLastNotification(List<Notification> notifications) {
        lastNotification = notifications.get(0); //uložíme první načtenou notifikaci
        String id = preferences.getString("lastReadNotification", "id"); //první notifikaci najdeme ze sharedpref
        if (firstInit) { //první zapnutí appky
            firstInit = false;
            if (!isAnyNotificationSaved(id, "id")) {
                lastReadNotification = lastNotification; //nový user, nastavíme mu posledni přečtenou jako poslední notifikaci
                return 0;
            }
            else {
                setLastReadNotificationFromPref(notifications, id);
            }
        }
        return findNumberOfLastReadNotification(notifications);

    }

    private boolean isAnyNotificationSaved(String id, String def) {
        if (id.equals(def)) {
            return false;
        }
        return true;
    }

    /** Funkce porovná poslední notifikaci uloženou v preferences a pokusí se najít shodu u seznamu notifikací
     * Pokud je shoda nalezená, nastaví se nově lastReadNotifikation
     * @param notifications seznam notifikací z db
     * @param notificationId id z pref
     */
    private void setLastReadNotificationFromPref(List<Notification> notifications, String notificationId) {

        for (Notification notification : notifications) {
            if (notification.getId().equals(notificationId)) {
                lastReadNotification = notification;
                return;
            }
        }
    }

    /**
     * @param notifications seznam notifikací z db
     * @return číslo poslední nepřečtené notifikace, případně max number pokud nebyla nalezena poslední přečtená notifikace
     */
    private int findNumberOfLastReadNotification(List<Notification> notifications) {
        int notificationsUnread = 0;
        if (lastReadNotification == null) {
            return MAX_NUMBER;
        }
        for (int i = 0; i < MAX_NUMBER; i++) {
            if (!notifications.get(i).equals(lastReadNotification)) {
                if (!notifications.get(i).getUser().equals(user)) {
                    notificationsUnread++;
                }
            }
            else {
                return notificationsUnread;
            }
        }
        return notificationsUnread;
    }

    /**
     * nastaví poslední přečtenou notifikaci a uloží ji do preferences
     */
    public void setLastReadNotification() {
        lastReadNotification = lastNotification;
        preferences.edit().putString("lastReadNotification", lastNotification.getId()).apply();
    }
}
