package com.jumbo.trus.fine.detail.list;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jumbo.trus.BaseViewModel;
import com.jumbo.trus.Flag;
import com.jumbo.trus.Model;
import com.jumbo.trus.fine.Fine;
import com.jumbo.trus.listener.ChangeListener;
import com.jumbo.trus.repository.FirebaseRepository;

import java.util.List;

public class FineListViewModel extends BaseViewModel implements ChangeListener {

    private static final String TAG = "FineListViewModel";


    private MutableLiveData<List<Fine>> fines;
    private FirebaseRepository firebaseRepository;


    public void init() {
        firebaseRepository = new FirebaseRepository(FirebaseRepository.FINE_TABLE, this);
        if (fines == null) {
            fines = new MutableLiveData<>();
            firebaseRepository.loadFinesFromRepository();
            Log.d(TAG, "init: nacitam pokuty");
        }
    }

    public LiveData<List<Fine>> getFines() {
        return fines;
    }

    @Override
    public void itemAdded(Model model) {

    }

    @Override
    public void itemChanged(Model model) {

    }

    @Override
    public void itemDeleted(Model model) {

    }

    @Override
    public void itemListLoaded(List models, Flag flag) {
        Log.d(TAG, "itemListLoaded: " + models);
        fines.setValue(models);
    }

    @Override
    public void alertSent(String message) {
        alert.setValue(message);
    }

}
