package com.jumbo.trus.player.edit;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jumbo.trus.BaseViewModel;
import com.jumbo.trus.Date;
import com.jumbo.trus.Flag;
import com.jumbo.trus.INotificationSender;
import com.jumbo.trus.Model;
import com.jumbo.trus.listener.ChangeListener;
import com.jumbo.trus.notification.Notification;
import com.jumbo.trus.player.Player;
import com.jumbo.trus.repository.FirebaseRepository;
import com.jumbo.trus.user.User;

import java.time.DateTimeException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PlayerEditViewModel extends BaseViewModel implements ChangeListener, INotificationSender {

    private static final String TAG = "PlayerViewModel";

    private FirebaseRepository firebaseRepository;
    private Player pickedPlayer;
    private MutableLiveData<Player> player = new MutableLiveData<>();
    private boolean changeAlertLocked;

    public void init() {
        changeAlertLocked = true;
        firebaseRepository = new FirebaseRepository(FirebaseRepository.PLAYER_TABLE, this);
        firebaseRepository.loadPlayersFromRepository();
        Log.d(TAG, "init: nacitam hrace");

    }

    public void editPlayerInRepository(final String name, final boolean fan, final String datum, User user) {
        isUpdating.setValue(true);
        Date date = new Date();
        Player player = pickedPlayer;
        player.setName(name);
        player.setFan(fan);
        try {
            long millis = date.convertTextDateToMillis(datum);
            player.setDateOfBirth(millis);
            firebaseRepository.editModel(player);
        } catch (DateTimeException e) {
            Log.e(TAG, "addPlayerToRepository: toto by nemělo nastat, ošetřeno validací", e);
            alert.setValue("Datum musí být ve formátu " + Date.DATE_PATTERN);
            return;
        }
        changeAlertLocked = true;
        String text = "narozen " + pickedPlayer.returnBirthdayInStringFormat();
        sendNotificationToRepository(new Notification("Upraven " + (pickedPlayer.isFan() ? "fanoušek" : "hráč") + pickedPlayer.getName(), text, user));
    }

    public void removePlayerFromRepository(User user) {
        isUpdating.setValue(true);
        try {
            firebaseRepository.removeModel(pickedPlayer);
        } catch (Exception e) {
            Log.e(TAG, "removePlayerFromRepository: chyba při mazání hráče", e);
            alert.setValue("Chyba při posílání požadavku do DB");
            return;
        }
        changeAlertLocked = true;
        String text = "narozen " + pickedPlayer.returnBirthdayInStringFormat();
        sendNotificationToRepository(new Notification("Smazán " + (pickedPlayer.isFan() ? "fanoušek" : "hráč") + pickedPlayer.getName(), text, user));
    }

    private void sendChangedPlayerAlert() {
        if (!changeAlertLocked) {
            alert.setValue("Právě někdo jiný upravil hráče. Reloaduji nové údaje...");
        }
        else {
            changeAlertLocked = false;
        }
    }

    public void setPickedPlayer(Player player) {
        pickedPlayer = player;
        setPlayer(player);
    }

    private void setPlayer(Player player) {
        this.player.setValue(player);
    }

    public LiveData<Player> getPlayer() {
        return player;
    }

    private void updatePickedPlayer(List<Player> players) {
        pickedPlayer = findPlayerFromRepo(pickedPlayer, players);
        setPlayer(pickedPlayer);
        sendChangedPlayerAlert();
    }

    private Player findPlayerFromRepo(Player player, List<Player> players) {
        if (players != null) {
            for (Player repoPlayer : players) {
                if (repoPlayer.equals(player)) {
                    return repoPlayer;
                }
            }
        }
        return player;
    }

    @Override
    public void sendNotificationToRepository(Notification notification) {
        firebaseRepository.addNotification(notification);
    }

    @Override
    public void itemAdded(Model model) {

    }

    @Override
    public void itemChanged(Model model) {
        alert.setValue("Hráč " + model.getName() + " úspěšně upraven");
        isUpdating.setValue(false);
        closeFragment.setValue(true);
    }

    @Override
    public void itemDeleted(Model model) {
        alert.setValue("Hráč " + model.getName() + " úspěšně smazán");
        isUpdating.setValue(false);
        closeFragment.setValue(true);
    }

    @Override
    public void itemListLoaded(List models, Flag flag) {
        Collections.sort(models, new Comparator<Player>() {
            @Override
            public int compare(Player o1, Player o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });
        updatePickedPlayer(models);
    }

    @Override
    public void alertSent(String message) {
        alert.setValue(message);
    }
}
