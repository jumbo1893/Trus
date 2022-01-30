package com.jumbo.trus.repayment;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jumbo.trus.BaseViewModel;
import com.jumbo.trus.Flag;
import com.jumbo.trus.INotificationSender;
import com.jumbo.trus.Model;
import com.jumbo.trus.listener.ChangeListener;
import com.jumbo.trus.match.Match;
import com.jumbo.trus.notification.Notification;
import com.jumbo.trus.player.Player;
import com.jumbo.trus.repository.FirebaseRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RepaymentViewModel extends BaseViewModel implements ChangeListener, INotificationSender {

    private static final String TAG = "RepaymentViewModel";

    private MutableLiveData<List<Player>> players;
    private List<Match> matches = new ArrayList<>();
    private FirebaseRepository firebaseRepository;

    public void init() {
        firebaseRepository = new FirebaseRepository(FirebaseRepository.PLAYER_TABLE, this);
        if (players == null) {
            players = new MutableLiveData<>();
            firebaseRepository.loadPlayersFromRepository();
            Log.d(TAG, "init: nacitam hrace");
        }
       firebaseRepository.loadMatchesFromRepository();
    }

    private void filterPlayers(List<Player> players) {
        List<Player> selectedPlayers = new ArrayList<>();
        for (Player player : players) {
            if (!player.isFan()) {
                selectedPlayers.add(player);
            }
        }
        this.players.setValue(enhancePlayers(matches, selectedPlayers));
    }

    private List<Player> enhancePlayers(List<Match> matches, List<Player> selectedPlayers) {
        Log.d(TAG, "enhancePlayers: ");
        if (matches != null || players != null) {
            for (Player player : selectedPlayers) {
                player.calculateAllFinesNumber(matches);
            }
        }
        return selectedPlayers;
    }

    @Override
    public void sendNotificationToRepository(Notification notification) {
        firebaseRepository.addNotification(notification);
    }

    public LiveData<List<Player>> getPlayers() {
        return players;
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
            Collections.sort(list, new Comparator<Match>() {
                @Override
                public int compare(Match o1, Match o2) {
                    return Long.compare(o2.getDateOfMatch(), o1.getDateOfMatch());
                }
            });
            players.setValue(enhancePlayers(list, players.getValue()));
        }
        else if (flag.equals(Flag.PLAYER)) {
            Collections.sort(list, new Comparator<Player>() {
                @Override
                public int compare(Player o1, Player o2) {
                    return o1.getName().compareToIgnoreCase(o2.getName());
                }
            });
            filterPlayers(list);
        }
    }

    @Override
    public void alertSent(String message) {
        alert.setValue(message);
    }
}
