package com.jumbo.trus.fine.detail.edit;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jumbo.trus.BaseViewModel;
import com.jumbo.trus.Flag;
import com.jumbo.trus.INotificationSender;
import com.jumbo.trus.Model;
import com.jumbo.trus.fine.Fine;
import com.jumbo.trus.listener.ChangeListener;
import com.jumbo.trus.notification.Notification;
import com.jumbo.trus.repository.FirebaseRepository;
import com.jumbo.trus.user.User;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FineEditViewModel extends BaseViewModel implements ChangeListener, INotificationSender {

    private static final String TAG = "FineEditViewModel";

    private FirebaseRepository firebaseRepository;
    private Fine pickedFine;
    private MutableLiveData<Fine> fine = new MutableLiveData<>();
    private boolean changeAlertLocked;

    public void init() {
        changeAlertLocked = true;
        firebaseRepository = new FirebaseRepository(FirebaseRepository.FINE_TABLE, this);
        firebaseRepository.loadFinesFromRepository();
        Log.d(TAG, "init: nacitam pokuty");

    }

    public void editFineInRepository(final String name, int amount, boolean forNonPlayers, User user) {
        isUpdating.setValue(true);
        Fine fine = pickedFine;
        fine.setName(name);
        fine.setForNonPlayers(forNonPlayers);
        try {
            fine.setAmount(amount);
            firebaseRepository.editModel(fine);
        }
        catch (Exception e) {
            Log.e(TAG, "editFineInRepository: toto by nemělo nastat, ošetřeno validací", e);
            alert.setValue("Něco se posralo při přidávání do db");
            return;
        }
        String text = (forNonPlayers ? "Pokuta pro nehrající" : "Pokuta pro hrající") + ", výše pokuty " + amount + " Kč";
        sendNotificationToRepository(new Notification("Upravena pokuta " + name, text, user));
    }

    public void removeFineFromRepository(User user) {
        isUpdating.setValue(true);
        try {
            firebaseRepository.removeModel(pickedFine);
        }
        catch (Exception e) {
            Log.e(TAG, "removeFineFromRepository: chyba při mazání pokuty", e);
            alert.setValue("Chyba při posílání požadavku do DB");
            return;
        }
        String text = (pickedFine.isForNonPlayers() ? "Pokuta pro nehrající" : "Pokuta pro hrající") + ", výše pokuty " + pickedFine.getAmount() + " Kč";
        sendNotificationToRepository(new Notification("Smazána pokuta " + pickedFine.getName(), text, user));
    }

    private void sendChangedMatchAlert() {
        if (!changeAlertLocked) {
            alert.setValue("Právě někdo jiný upravil pokutu. Reloaduji nové údaje...");
        }
        else {
            changeAlertLocked = false;
        }
    }

    public void setPickedFine(Fine fine) {
        pickedFine = fine;
        setFine(fine);
    }

    private void setFine(Fine fine) {
        this.fine.setValue(fine);
    }

    public LiveData<Fine> getFine() {
        return fine;
    }

    private void updatePickedFine(List<Fine> fines) {
        pickedFine = findFineFromRepo(pickedFine, fines);
        setFine(pickedFine);
        sendChangedMatchAlert();
    }

    private Fine findFineFromRepo(Fine fine, List<Fine> fines) {
        if (fines != null) {
            for (Fine repoFine : fines) {
                if (repoFine.equals(fine)) {
                    return repoFine;
                }
            }
        }
        return fine;
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
        alert.setValue("Pokuta " + model.getName() + " úspěšně upravena");
        isUpdating.setValue(false);
        closeFragment.setValue(true);
    }

    @Override
    public void itemDeleted(Model model) {
        alert.setValue("Pokuta " + model.getName() + " úspěšně smazána");
        isUpdating.setValue(false);
        closeFragment.setValue(true);
    }

    @Override
    public void itemListLoaded(List models, Flag flag) {
        Collections.sort(models, new Comparator<Fine>() {
            @Override
            public int compare(Fine o1, Fine o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });
        updatePickedFine(models);
    }

    @Override
    public void alertSent(String message) {
        alert.setValue(message);
    }
}
