package com.jumbo.trus;

import com.jumbo.trus.notification.Notification;

public interface INotificationSender {

    void sendNotificationToRepository(Notification notification);
}
