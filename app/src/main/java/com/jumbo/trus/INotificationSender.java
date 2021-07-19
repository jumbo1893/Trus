package com.jumbo.trus;

import com.jumbo.trus.notification.Notification;

public interface INotificationSender {
    void createNotification(Notification notification);
}
