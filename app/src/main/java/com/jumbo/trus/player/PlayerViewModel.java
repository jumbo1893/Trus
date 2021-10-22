package com.jumbo.trus.player;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jumbo.trus.ChangeListener;
import com.jumbo.trus.Date;
import com.jumbo.trus.Flag;
import com.jumbo.trus.INotificationSender;
import com.jumbo.trus.Model;
import com.jumbo.trus.Result;
import com.jumbo.trus.Validator;
import com.jumbo.trus.notification.Notification;
import com.jumbo.trus.repayment.Repayment;
import com.jumbo.trus.repository.FirebaseRepository;

import java.time.DateTimeException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PlayerViewModel extends ViewModel implements ChangeListener, INotificationSender {

    private static final String TAG = "PlayerViewModel";

    private MutableLiveData<List<Player>> players;
    private MutableLiveData<Boolean> isUpdating = new MutableLiveData<>();
    private MutableLiveData<String> alert = new MutableLiveData<>();
    private FirebaseRepository firebaseRepository;

    public void init() {
        firebaseRepository = new FirebaseRepository(FirebaseRepository.PLAYER_TABLE, this);
        if (players == null) {
            players = new MutableLiveData<>();
            firebaseRepository.loadPlayersFromRepository();
            Log.d(TAG, "init: nacitam hrace");
        }
    }

    public Result checkNewPlayerValidation(final String name, final String datum) {
        Validator validator = new Validator();
        String response = "";
        boolean result = false;
        if (!validator.fieldIsNotEmpty(name)) {
            response = "Není vyplněné jméno";
        }
        else if (!validator.checkNameFormat(name)) {
            response = "Asi nemáš jméno delší než 100 znaků nebo v něm nejsou klikyháky co?";
        }
        else if (!validator.fieldIsNotEmpty(datum)) {
            response = "Není vyplněné datum";
        }
        else if (!validator.isDateInCorrectFormat(datum)) {
            response = "Datum musí být ve formátu " + Date.DATE_PATTERN;
        }
        else {
            result = true;
        }
        return new Result(result, response);
    }

    public Result addPlayerToRepository(final String name, final boolean fan, final String datum) {
        isUpdating.setValue(true);
        Date date = new Date();
        Result result = new Result(false);
        try {
            long millis = date.convertTextDateToMillis(datum);
            Player player = new Player(name, fan, millis);
            firebaseRepository.insertNewModel(player);
        }
        catch (DateTimeException e) {
            Log.e(TAG, "addPlayerToRepository: toto by nemělo nastat, ošetřeno validací", e);
            result.setText("Datum musí být ve formátu " + Date.DATE_PATTERN);
            isUpdating.setValue(false);
            return result;
        }
        result.setText("Přidávám hráče " + name);
        result.setTrue(true);
        return result;
    }

    public Result editPlayerInRepository(final String name, final boolean fan, final String datum, Player player) {
        isUpdating.setValue(true);
        Date date = new Date();
        Result result = new Result(false);
        player.setName(name);
        player.setFan(fan);
        try {
            long millis = date.convertTextDateToMillis(datum);
            player.setDateOfBirth(millis);
            firebaseRepository.editModel(player);
        }
        catch (DateTimeException e) {
            Log.e(TAG, "addPlayerToRepository: toto by nemělo nastat, ošetřeno validací", e);
            result.setText("Datum musí být ve formátu " + Date.DATE_PATTERN);
            return result;
        }

        result.setText("Přidávám hráče " + name);
        result.setTrue(true);
        return result;
    }


    public Result removePlayerFromRepository(Player player) {
        isUpdating.setValue(true);
        Result result = new Result(false);

        try {
            firebaseRepository.removeModel(player);
        }
        catch (Exception e) {
            Log.e(TAG, "removePlayerFromRepository: chyba při mazání hráče", e);
            result.setText("Chyba při posílání požadavku do DB");
            return result;
        }

        result.setText("Mažu hráče " + player.getName());
        result.setTrue(true);
        return result;
    }

    public boolean editPlayerRepaymentsInRepository(final int amount, final String note, Player player) {
        isUpdating.setValue(true);
        if (!checkNewRepayment(amount, note)) {
            isUpdating.setValue(false);
            return false;
        }
        Date date = new Date();
        try {
            long millis = date.getCurrentDateInMillis();
            Repayment repayment = new Repayment(amount, millis, note);
            player.addRepayment(repayment);
            firebaseRepository.editModel(player);
        }
        catch (Exception e) {
            alert.setValue("Chyba při komunikaci s db, zkus to znova " + player.getName());
            Log.e(TAG, "addPlayerToRepository: toto by nemělo nastat, ošetřeno validací", e);
            isUpdating.setValue(false);
            return false;
        }

        alert.setValue("Přidávám transakci hráči " + player.getName());
        isUpdating.setValue(false);
        return true;
    }

    public boolean removePlayerRepaymentsInRepository(Repayment repayment, Player player) {
        isUpdating.setValue(true);
        try {
            player.removeRepayment(repayment);
            firebaseRepository.editModel(player);
        }
        catch (Exception e) {
            alert.setValue("Chyba při komunikaci s db, zkus to znova " + player.getName());
            Log.e(TAG, "addPlayerToRepository: toto by nemělo nastat, ošetřeno validací", e);
            isUpdating.setValue(false);
            return false;
        }

        alert.setValue("Odebírám transakci hráči " + player.getName());
        isUpdating.setValue(false);
        return true;
    }

    private boolean checkNewRepayment(int amount, String note) {
        Validator validator = new Validator();
        if (!validator.checkAmount(amount)) {
            alert.setValue("Buď si napsal do částky nesmysly nebo je moc vysoká");
            return false;
        }
        else if (amount == 0) {
            alert.setValue("Nelze zadat nulová částka");
            return false;
        }
        else if (!validator.checkNameFormat(note)) {
            alert.setValue("Poznámku do 100 znaků, dík šéfe");
            return false;
        }
        return true;
    }

    private void setPlayerAsAdded (final Player player, Flag flag) {
        Log.d(TAG, "setPlayerAsAdded: " + player.getName());
        String action = "";
        switch (flag) {
            case PLAYER_PLUS:
                action = "přidán";
                break;
            case PLAYER_EDIT:
                action = "upraven";
                break;
            case PLAYER_DELETE:
                action = "smazán";
                break;
        }
        alert.setValue("Hráč " + player.getName() + " úspěšně " + action);
        isUpdating.setValue(false);
    }

    @Override
    public void sendNotificationToRepository(Notification notification) {
        firebaseRepository.addNotification(notification);
    }

    public LiveData<List<Player>> getPlayers() {
        return players;
    }

    public LiveData<Boolean> isUpdating() {
        return isUpdating;
    }

    public LiveData<String> getAlert() {
        return alert;
    }

    @Override
    public void itemAdded(Model model) {
        setPlayerAsAdded((Player) model, Flag.PLAYER_PLUS);
    }

    @Override
    public void itemChanged(Model model) {
        setPlayerAsAdded((Player) model, Flag.PLAYER_EDIT);
    }

    @Override
    public void itemDeleted(Model model) {
        setPlayerAsAdded((Player) model, Flag.PLAYER_DELETE);
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
