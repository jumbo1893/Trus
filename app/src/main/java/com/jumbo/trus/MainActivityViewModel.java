package com.jumbo.trus;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jumbo.trus.user.User;

public class MainActivityViewModel extends ViewModel {
    private MutableLiveData<User> user;

    public void init() {
        if (user == null) {
            user = new MutableLiveData<>();
        }
    }

    public LiveData<User> getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user.setValue(user);
    }
}

