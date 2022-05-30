package com.jumbo.trus.pkfl.stats;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.jumbo.trus.listener.ItemLoadedListener;
import com.jumbo.trus.repository.FirebaseRepository;

public class PkflStatsAllSeasonsViewModel extends PkflStatsViewModel implements ItemLoadedListener {

    private static final String TAG = "PkflStatsAllSeasonsViewModel";

    public void init() {
        Log.d(TAG, "init: ");
        firebaseRepository = new FirebaseRepository(this);
        if (recycleViewList == null) {
            recycleViewList = new MutableLiveData<>();
            firebaseRepository.loadPkflUrlFromRepository();
            isUpdating.setValue(true);
            loadingAlert.setValue("Připojuji se k webu PKFL...");
        }
    }

    @Override
    public void itemLoaded(String value) {
        pkflUrl = value;
        Log.d(TAG, "itemLoaded: " + value);
        if (waitingForLoad) {
            loadSeasonUrlsFromPkfl(false);
            waitingForLoad = false;
        }

    }
}
