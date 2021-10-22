package com.jumbo.trus.notification;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jumbo.trus.NotificationListener;
import com.jumbo.trus.repository.FirebaseRepository;

import java.util.List;

public class NotificationViewModel extends ViewModel implements NotificationListener {

    private static final String TAG = "NotificationViewModel";


    private MutableLiveData<List<Notification>> notifications;
    private MutableLiveData<Boolean> isUpdating = new MutableLiveData<>();
    private MutableLiveData<String> alert = new MutableLiveData<>();
    private FirebaseRepository firebaseRepository;

    public void init() {
        firebaseRepository = new FirebaseRepository(FirebaseRepository.NOTIFICATION_TABLE, this);
        if (notifications == null) {
            notifications = new MutableLiveData<>();
            firebaseRepository.loadNotificationsFromRepository();
            Log.d(TAG, "init: načítám notifikace");
        }
    }

    public LiveData<List<Notification>> getNotifications() {
        return notifications;
    }

    public LiveData<Boolean> isUpdating() {
        return isUpdating;
    }

    public LiveData<String> getAlert() {
        return alert;
    }


    @Override
    public void notificationListLoaded(List notificationsList) {
        notifications.setValue(notificationsList);
    }

    @Override
    public void notificationAdded(Notification notification) {

    }
}
