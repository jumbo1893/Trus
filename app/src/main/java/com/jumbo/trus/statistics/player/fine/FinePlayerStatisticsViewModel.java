package com.jumbo.trus.statistics.player.fine;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jumbo.trus.Flag;
import com.jumbo.trus.INotificationSender;
import com.jumbo.trus.Model;
import com.jumbo.trus.listener.ChangeListener;
import com.jumbo.trus.notification.Notification;
import com.jumbo.trus.player.Player;
import com.jumbo.trus.repository.FirebaseRepository;
import com.jumbo.trus.season.Season;
import com.jumbo.trus.statistics.player.PlayerHelperStatisticsViewModel;

import java.util.ArrayList;
import java.util.List;

public class FinePlayerStatisticsViewModel extends PlayerHelperStatisticsViewModel implements ChangeListener, INotificationSender {

    private static final String TAG = "FinePlayerStatisticsViewModel";

    private MutableLiveData<List<Player>> players;
    private List<Player> allPlayers = new ArrayList<>();
    private FirebaseRepository firebaseRepository;


    public void init() {
        firebaseRepository = new FirebaseRepository(FirebaseRepository.PLAYER_TABLE, this, true, true);
        if (players == null) {
            players = new MutableLiveData<>();
            firebaseRepository.loadPlayersFromRepository();
            Log.d(TAG, "init: nacitam hrace");
        }
        firebaseRepository.loadMatchesFromRepository();
        firebaseRepository.loadSeasonsFromRepository();
    }

    @Override
    public void changeOrderBy() {
        super.changeOrderBy();
        players.setValue(filterPlayers(allPlayers, false));
    }


    @Override
    public void sendNotificationToRepository(Notification notification) {
        firebaseRepository.addNotification(notification);
    }

    public LiveData<List<Player>> getPlayers() {
        return players;
    }

    public void setSeason (Season season) {
        this.season.setValue(season);
        players.setValue(filterPlayers(allPlayers, false));
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
        players.setValue(filterPlayers(allPlayers, false));
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
            matches = list;
            players.setValue(filterPlayers(allPlayers, false));
        } else if (flag.equals(Flag.PLAYER)) {
            allPlayers = list;
            players.setValue(filterPlayers(list, false));
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
