package com.jumbo.trus;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.jumbo.trus.notification.Notification;
import com.jumbo.trus.user.User;

public class CustomUserFragment extends Fragment {
    protected User user;
    private MainActivityViewModel mainActivityViewModel;

    protected void initMainActivityViewModel() {
        mainActivityViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        mainActivityViewModel.getUser().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                setUser(user);
            }
        });
    }

    protected void createNotification(Notification notification, INotificationSender iNotificationSender) {
        notification.setUser(user);
        iNotificationSender.sendNotificationToRepository(notification);
    }

    private void setUser(User user) {
        this.user = user;
    }
}