package com.jumbo.trus.player.list;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jumbo.trus.BaseViewModel;
import com.jumbo.trus.Date;
import com.jumbo.trus.Flag;
import com.jumbo.trus.INotificationSender;
import com.jumbo.trus.Model;
import com.jumbo.trus.Result;
import com.jumbo.trus.listener.ChangeListener;
import com.jumbo.trus.notification.Notification;
import com.jumbo.trus.player.Player;
import com.jumbo.trus.repayment.Repayment;
import com.jumbo.trus.repository.FirebaseRepository;
import com.jumbo.trus.validator.Validator;

import java.time.DateTimeException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PlayerListViewModel extends BaseViewModel implements ChangeListener, INotificationSender {

    private static final String TAG = "PlayerViewModel";

    private MutableLiveData<List<Player>> players;
    private FirebaseRepository firebaseRepository;

    public void init() {
        firebaseRepository = new FirebaseRepository(FirebaseRepository.PLAYER_TABLE, this);
        if (players == null) {
            players = new MutableLiveData<>();
            firebaseRepository.loadPlayersFromRepository();
            Log.d(TAG, "init: nacitam hrace");
        }
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
        Collections.sort(models, new Comparator<Player>() {
            @Override
            public int compare(Player o1, Player o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });
        players.setValue(models);
    }

    @Override
    public void alertSent(String message) {
        alert.setValue(message);
    }
}
