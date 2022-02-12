package com.jumbo.trus.season.list;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jumbo.trus.BaseViewModel;
import com.jumbo.trus.Flag;
import com.jumbo.trus.Model;
import com.jumbo.trus.listener.ChangeListener;
import com.jumbo.trus.repository.FirebaseRepository;
import com.jumbo.trus.season.Season;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SeasonsListViewModel extends BaseViewModel implements ChangeListener {

    private static final String TAG = "SeasonsListViewModel";

    private MutableLiveData<List<Season>> seasons;
    private FirebaseRepository firebaseRepository;


    public void init() {
        firebaseRepository = new FirebaseRepository(FirebaseRepository.SEASON_TABLE, this);
        if (seasons == null) {
            seasons = new MutableLiveData<>();
            firebaseRepository.loadSeasonsFromRepository();
            Log.d(TAG, "init: nacitam hrace");
        }
    }


    public LiveData<List<Season>> getSeasons() {
        return seasons;
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
        Collections.sort(models, new Comparator<Season>() {
            @Override
            public int compare(Season o1, Season o2) {
                return Long.compare(o2.getSeasonStart(), o1.getSeasonStart());
            }
        });
        seasons.setValue(models);
    }

    @Override
    public void alertSent(String message) {
        alert.setValue(message);
    }

}
