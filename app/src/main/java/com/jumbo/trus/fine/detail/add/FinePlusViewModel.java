package com.jumbo.trus.fine.detail.add;

import android.util.Log;

import com.jumbo.trus.BaseViewModel;
import com.jumbo.trus.Flag;
import com.jumbo.trus.INotificationSender;
import com.jumbo.trus.Model;
import com.jumbo.trus.fine.Fine;
import com.jumbo.trus.listener.ChangeListener;
import com.jumbo.trus.notification.Notification;
import com.jumbo.trus.repository.FirebaseRepository;
import com.jumbo.trus.user.User;

import java.util.List;

public class FinePlusViewModel extends BaseViewModel implements ChangeListener, INotificationSender {

    private static final String TAG = "PlayerPlusViewModel";

    private FirebaseRepository firebaseRepository;

    public void init() {
        firebaseRepository = new FirebaseRepository(FirebaseRepository.FINE_TABLE, this);
    }

    public void addFineToRepository(final String name, int amount, boolean forNonPlayers, User user) {
        isUpdating.setValue(true);
        try {
            Fine fine = new Fine(name, amount, forNonPlayers);
            firebaseRepository.insertNewModel(fine);
        }
        catch (Exception e) {
            Log.e(TAG, "addFineToRepository: toto by nemělo nastat, ošetřeno validací", e);
            alert.setValue("Neznámá chyba při instantizaci pokuty");
            isUpdating.setValue(false);
            return;
        }
        String text = (forNonPlayers ? "Pokuta pro nehrající" : "Pokuta pro hrající") + ", výše pokuty " + amount + " Kč";
        sendNotificationToRepository(new Notification("Přidána pokuta " + name, text, user));
    }

    private void setFineAsAdded(final Fine fine) {
        Log.d(TAG, "setFineAsAdded: " + fine.getName());
        alert.setValue("Pokuta " + fine.getName() + " úspěšně přidána");
        isUpdating.setValue(false);
    }

    @Override
    public void sendNotificationToRepository(Notification notification) {
        firebaseRepository.addNotification(notification);
    }

    @Override
    public void itemAdded(Model model) {
        setFineAsAdded((Fine) model);
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
