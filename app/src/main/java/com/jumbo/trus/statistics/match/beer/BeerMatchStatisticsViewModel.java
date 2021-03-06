package com.jumbo.trus.statistics.match.beer;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jumbo.trus.Flag;
import com.jumbo.trus.INotificationSender;
import com.jumbo.trus.Model;
import com.jumbo.trus.listener.ChangeListener;
import com.jumbo.trus.match.Match;
import com.jumbo.trus.notification.Notification;
import com.jumbo.trus.repository.FirebaseRepository;
import com.jumbo.trus.season.Season;
import com.jumbo.trus.statistics.match.MatchHelperStatisticsViewModel;

import java.util.ArrayList;
import java.util.List;

public class BeerMatchStatisticsViewModel extends MatchHelperStatisticsViewModel implements ChangeListener, INotificationSender {

    private static final String TAG = "BeerMatchStatisticsViewModel";

    private MutableLiveData<List<Match>> matches;
    private List<Match> allMatches = new ArrayList<>();
    private FirebaseRepository firebaseRepository;


    public void init() {
        firebaseRepository = new FirebaseRepository(FirebaseRepository.MATCH_TABLE, this, true, true);
        if (matches == null) {
            matches = new MutableLiveData<>();
            firebaseRepository.loadMatchesFromRepository();
            Log.d(TAG, "init: nacitam hrace");
        }
        firebaseRepository.loadSeasonsFromRepository();
    }

    public void removeReg() {
        firebaseRepository.removeListener();
    }


    @Override
    public void changeOrderBy() {
        super.changeOrderBy();
        matches.setValue(filterMatches(allMatches, true));
    }

    @Override
    public void sendNotificationToRepository(Notification notification) {
        firebaseRepository.addNotification(notification);
    }
    public void setSeason (Season season) {
        this.season.setValue(season);
        matches.setValue(filterMatches(allMatches, true));
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
        matches.setValue(filterMatches(allMatches, true));
    }

    public LiveData<List<Match>> getMatches() {
        return matches;
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
        List list = new ArrayList(models);
        Log.d(TAG, "itemListLoaded: zapasy: " + models);
        if (flag.equals(Flag.MATCH)) {
            allMatches = list;
            matches.setValue(filterMatches(list, true));
        } else if (flag.equals(Flag.SEASON)) {
            if (season.getValue() == null) {
                setSeason(returnCurrentSeason(list));
            }
            seasons.setValue(list);
        }
    }

    @Override
    public void alertSent(String message) {
        alert.setValue(message);
    }

}
