package com.jumbo.trus.listener;

import com.jumbo.trus.notification.Notification;

import java.util.List;

public interface NotificationListener {
    void notificationListLoaded(List notifications);
    void notificationAdded(Notification notification);
}
