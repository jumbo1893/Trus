package com.jumbo.trus.match.list;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jumbo.trus.BaseViewModel;
import com.jumbo.trus.Flag;
import com.jumbo.trus.Model;
import com.jumbo.trus.listener.ChangeListener;
import com.jumbo.trus.match.Match;
import com.jumbo.trus.repository.FirebaseRepository;
import com.jumbo.trus.season.Season;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MatchListViewModel extends BaseViewModel implements ChangeListener {

    private static final String TAG = "MatchListViewModel";

    private MutableLiveData<List<Match>> matches;
    private List<Match> allMatches = new ArrayList<>();
    private MutableLiveData<List<Season>> seasons;
    private Season selectedSeason;
    private FirebaseRepository firebaseRepository;

    public void init() {
        firebaseRepository = new FirebaseRepository(FirebaseRepository.MATCH_TABLE, this, true, true);
        if (matches == null) {
            matches = new MutableLiveData<>();
            firebaseRepository.loadMatchesFromRepository();
            Log.d(TAG, "init: nacitam zapasy");
        }
        if (seasons == null) {
            seasons = new MutableLiveData<>();
            firebaseRepository.loadSeasonsFromRepository();
            Log.d(TAG, "init: nacitam sezony");
        }
    }

    public LiveData<List<Match>> getMatches() {
        return matches;
    }

    public LiveData<List<Season>> getSeasons() {
        return seasons;
    }

    public void setSelectedSeason(Season season) {
        selectedSeason = season;
        useSeasonsFilter(allMatches);
    }

    private void useSeasonsFilter(List<Match> models) {
        Log.d(TAG, "useSeasonsFilter: " + selectedSeason);
        Log.d(TAG, "useSeasonsFilter: " + seasons.getValue());
        if (selectedSeason == null || selectedSeason.equals(new Season().allSeason())) {
            matches.setValue(models);
            return;
        }
        List<Match> selectedMatches = new ArrayList<>();
        for (Match match : models) {
            if (match.getSeason().equals(selectedSeason)) {
                selectedMatches.add(match);
            }
        }
        matches.setValue(selectedMatches);
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
        isUpdating.setValue(true);
        List list = new ArrayList(models);
        if (flag.equals(Flag.MATCH)) {
            Collections.sort(list, new Comparator<Match>() {
                @Override
                public int compare(Match o1, Match o2) {
                    return Long.compare(o2.getDateOfMatch(), o1.getDateOfMatch());
                }
            });
            Log.d(TAG, "itemListLoaded: zapasy: " + models);
            allMatches = list;
            useSeasonsFilter(list);
        }
        else if (flag.equals(Flag.SEASON)) {
            seasons.setValue(list);
        }
        isUpdating.setValue(false);
    }

    @Override
    public void alertSent(String message) {
        alert.setValue(message);
    }

}
