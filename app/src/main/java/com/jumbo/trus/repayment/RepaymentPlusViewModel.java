package com.jumbo.trus.repayment;

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

import java.util.List;

public class RepaymentPlusViewModel extends BaseViewModel implements ChangeListener, INotificationSender {

    private static final String TAG = "RepaymentPlusViewModel";

    private MutableLiveData<List<Repayment>> repayments;
    private Player pickedPlayer;
    private MutableLiveData<Player> player = new MutableLiveData<>();
    private FirebaseRepository firebaseRepository;

    public void init() {
        firebaseRepository = new FirebaseRepository(FirebaseRepository.PLAYER_TABLE, this);
        if (repayments == null) {
            repayments = new MutableLiveData<>();
            firebaseRepository.loadPlayersFromRepository();
            Log.d(TAG, "init: nacitam hrace");
        }
    }

    public void AddRepaymentToPlayerInRepository(final int amount, final String note, User user) {
        isUpdating.setValue(true);
        Date date = new Date();
        try {
            long millis = date.getCurrentDateInMillis();
            Repayment repayment = new Repayment(amount, millis, note);
            pickedPlayer.addRepayment(repayment);
            firebaseRepository.editModel(pickedPlayer);
        }
        catch (Exception e) {
            alert.setValue("Chyba při komunikaci s db, zkus to znova ");
            Log.e(TAG, "addPlayerToRepository: toto by nemělo nastat, ošetřeno validací", e);
            isUpdating.setValue(false);
            return;
        }
        String text = "Platba ve výši " + amount + ", poznámka: " + note;
        sendNotificationToRepository(new Notification("Přidána platba hráči " + pickedPlayer.getName(), text, user));
    }

    public void removePlayerRepaymentsInRepository(Repayment repayment, User user) {
        isUpdating.setValue(true);
        try {
            pickedPlayer.removeRepayment(repayment);
            firebaseRepository.editModel(pickedPlayer);
        }
        catch (Exception e) {
            alert.setValue("Chyba při komunikaci s db, zkus to znova ");
            Log.e(TAG, "addPlayerToRepository: toto by nemělo nastat, ošetřeno validací", e);
            isUpdating.setValue(false);
            return;
        }

        String text = "Platba ve výši " + repayment.getAmount() + ", poznámka: " + repayment.getNote();
        sendNotificationToRepository(new Notification("Odebrána platba hráči " + pickedPlayer.getName(), text, user));
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
        repayments.setValue(pickedPlayer.getRepayments());
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

    public LiveData<List<Repayment>> getRepayments() {
        return repayments;
    }


    @Override
    public void itemAdded(Model model) {

    }

    @Override
    public void itemChanged(Model model) {
        alert.setValue("Změna v platbě u hráč " + model.getName() + " úspěšně zapsána");
        isUpdating.setValue(false);
    }

    @Override
    public void itemDeleted(Model model) {

    }

    @Override
    public void itemListLoaded(List models, Flag flag) {

        updatePickedPlayer(models);

    }

    @Override
    public void alertSent(String message) {
        alert.setValue(message);
    }
}
