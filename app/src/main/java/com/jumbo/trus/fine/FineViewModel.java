package com.jumbo.trus.fine;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jumbo.trus.Flag;
import com.jumbo.trus.INotificationSender;
import com.jumbo.trus.Model;
import com.jumbo.trus.Result;
import com.jumbo.trus.listener.ChangeListener;
import com.jumbo.trus.notification.Notification;
import com.jumbo.trus.repository.FirebaseRepository;
import com.jumbo.trus.validator.Validator;

import java.util.List;

public class FineViewModel extends ViewModel implements ChangeListener, INotificationSender {

    private static final String TAG = "FineViewModel";


    private MutableLiveData<List<Fine>> fines;
    private MutableLiveData<Boolean> isUpdating = new MutableLiveData<>();
    private MutableLiveData<String> alert = new MutableLiveData<>();
    private FirebaseRepository firebaseRepository;


    public void init() {
        firebaseRepository = new FirebaseRepository(FirebaseRepository.FINE_TABLE, this);
        if (fines == null) {
            fines = new MutableLiveData<>();
            firebaseRepository.loadFinesFromRepository();
            Log.d(TAG, "init: nacitam pokuty");
        }
    }


    public Result checkNewFineValidation(final String name, final int amount, final boolean forNonPlayers, final Fine fine) {
        Validator validator = new Validator();
        String response = "";
        boolean result = false;
        if (!validator.fieldIsNotEmpty(name)) {
            response = "Není vyplněné jméno";
        }
        else if (!validator.checkNameFormat(name)) {
            response = "Tu pokutu bez píčovin";
        }
        else if (amount == 0) {
            response = "Nelze zadat nulová částka";
        }
        else if (!validator.checkAmount(amount)) {
            response = "Buď si napsal do částky nesmysly nebo je moc vysoká";
        }
        else if (!validator.checkEqualityOfFine(name, amount, forNonPlayers, fine)) {
            response = "Nebyly provedeny žádné změny!";
        }
        else {
            result = true;
        }
        return new Result(result, response);
    }

    public Result addFineToRepository(final String name, int amount, boolean forNonPlayers) {
        isUpdating.setValue(true);
        Result result = new Result(false);
        try {
            Fine fine = new Fine(name, amount, forNonPlayers);
            firebaseRepository.insertNewModel(fine);
        }
        catch (Exception e) {
            Log.e(TAG, "addFineToRepository: toto by nemělo nastat, ošetřeno validací", e);
            result.setText("Neznámá chyba při instantizaci pokuty");
            isUpdating.setValue(false);
            return result;
        }
        result.setText("Přidávám pokutu " + name);
        result.setTrue(true);
        return result;
    }

    public Result editFineInRepository(final String name, int amount, boolean forNonPlayers, Fine fine) {
        isUpdating.setValue(true);
        Result result = new Result(false);
        fine.setName(name);
        fine.setForNonPlayers(forNonPlayers);
        try {
            fine.setAmount(amount);
            firebaseRepository.editModel(fine);
        }
        catch (Exception e) {
            Log.e(TAG, "editFineInRepository: toto by nemělo nastat, ošetřeno validací", e);
            result.setText("Něco se posralo při přidávání do db");
            return result;
        }

        result.setText("Přidávám pokutu " + name);
        result.setTrue(true);
        return result;
    }

    public Result removeFineFromRepository(Fine fine) {
        isUpdating.setValue(true);
        Result result = new Result(false);

        try {
            firebaseRepository.removeModel(fine);
        }
        catch (Exception e) {
            Log.e(TAG, "removeFineFromRepository: chyba při mazání pokuty", e);
            result.setText("Chyba při posílání požadavku do DB");
            return result;
        }

        result.setText("Mažu pokutu " + fine.getName());
        result.setTrue(true);
        return result;
    }

    private void setFineAsAdded (final Fine fine, Flag flag) {
        Log.d(TAG, "setFineAsAdded: " + fine.getName());
        String action = "";
        switch (flag) {
            case FINE_PLUS:
                action = "přidána";
                break;
            case FINE_EDIT:
                action = "upravena";
                break;
            case FINE_DELETE:
                action = "smazána";
                break;
        }
        alert.setValue("Pokuta " + fine.getName() + " úspěšně " + action);
        isUpdating.setValue(false);
    }

    @Override
    public void sendNotificationToRepository(Notification notification) {
        firebaseRepository.addNotification(notification);
    }

    public LiveData<List<Fine>> getFines() {
        return fines;
    }

    public LiveData<Boolean> isUpdating() {
        return isUpdating;
    }

    public LiveData<String> getAlert() {
        return alert;
    }

    @Override
    public void itemAdded(Model model) {
        setFineAsAdded((Fine) model, Flag.FINE_PLUS);
    }

    @Override
    public void itemChanged(Model model) {
        setFineAsAdded((Fine) model, Flag.FINE_EDIT);
    }

    @Override
    public void itemDeleted(Model model) {
        setFineAsAdded((Fine) model, Flag.FINE_DELETE);
    }

    @Override
    public void itemListLoaded(List models, Flag flag) {
        Log.d(TAG, "itemListLoaded: " + models);
        fines.setValue(models);
    }

    @Override
    public void alertSent(String message) {
        alert.setValue(message);
    }

}
