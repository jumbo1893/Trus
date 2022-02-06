package com.jumbo.trus.player.add;

import android.util.Log;

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
import java.util.List;

public class PlayerPlusViewModel extends BaseViewModel implements ChangeListener, INotificationSender {

    private static final String TAG = "PlayerPlusViewModel";

    private FirebaseRepository firebaseRepository;

    public void init() {
        firebaseRepository = new FirebaseRepository(FirebaseRepository.PLAYER_TABLE, this);
    }

    public void addPlayerToRepository(final String name, final boolean fan, final String datum, User user) {
        isUpdating.setValue(true);
        Date date = new Date();
        try {
            long millis = date.convertTextDateToMillis(datum);
            Player player = new Player(name, fan, millis);
            firebaseRepository.insertNewModel(player);
        }
        catch (DateTimeException e) {
            Log.e(TAG, "addPlayerToRepository: toto by nemělo nastat, ošetřeno validací", e);
            alert.setValue("Datum musí být ve formátu " + Date.DATE_PATTERN);
            isUpdating.setValue(false);
            return;
        }
        String text = "Byl vytvořen " +  (fan ? "fanoušek" : "hráč") + name + " narozený " + datum;
        sendNotificationToRepository(new Notification("Přidán hráč " + name, text, user));
    }

    private void setPlayerAsAdded(final Player player) {
        Log.d(TAG, "setPlayerAsAdded: " + player.getName());
        alert.setValue("Hráč " + player.getName() + " úspěšně přidán");
        isUpdating.setValue(false);
    }

    @Override
    public void sendNotificationToRepository(Notification notification) {
        firebaseRepository.addNotification(notification);
    }

    @Override
    public void itemAdded(Model model) {
        setPlayerAsAdded((Player) model);
        closeFragment.setValue(true);

    }

    @Override
    public void itemChanged(Model model) {

    }

    @Override
    public void itemDeleted(Model model) {

    }

    @Override
    public void itemListLoaded(List models, Flag flag) {

    }

    @Override
    public void alertSent(String message) {
        alert.setValue(message);
    }
}
