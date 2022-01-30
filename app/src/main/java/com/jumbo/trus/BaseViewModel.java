package com.jumbo.trus;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BaseViewModel extends ViewModel {

    protected MutableLiveData<Boolean> isUpdating = new MutableLiveData<>();
    protected MutableLiveData<String> alert = new MutableLiveData<>();
    protected MutableLiveData<Boolean> closeFragment = new MutableLiveData<>();

    public LiveData<Boolean> isUpdating() {
        return isUpdating;
    }

    public LiveData<String> getAlert() {
        return alert;
    }

    public LiveData<Boolean> closeFragment() {
        return closeFragment;
    }

}
