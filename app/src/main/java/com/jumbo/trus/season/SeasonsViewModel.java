package com.jumbo.trus.season;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jumbo.trus.ChangeListener;
import com.jumbo.trus.Date;
import com.jumbo.trus.Flag;
import com.jumbo.trus.Model;
import com.jumbo.trus.Result;
import com.jumbo.trus.User;
import com.jumbo.trus.Validator;
import com.jumbo.trus.notification.Notification;
import com.jumbo.trus.repository.FirebaseRepository;

import java.time.DateTimeException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SeasonsViewModel extends ViewModel implements ChangeListener {

    private static final String TAG = "SettingsViewModel";

    private User user = new User("test_user_notifikace");

    private MutableLiveData<List<Season>> seasons;
    private MutableLiveData<Boolean> isUpdating = new MutableLiveData<>();
    private MutableLiveData<String> alert = new MutableLiveData<>();
    private FirebaseRepository firebaseRepository;


    public void init() {
        firebaseRepository = new FirebaseRepository("season", this);
        if (seasons == null) {
            seasons = new MutableLiveData<>();
            firebaseRepository.loadSeasonsFromRepository();
            Log.d(TAG, "init: nacitam hrace");
        }
    }


    public Result checkNewSeasonValidation(final String name, final String dateStart, final String dateEnd, final Season season) {
        Validator validator = new Validator();
        String response = "";
        boolean result = false;
        if (!validator.fieldIsNotEmpty(name)) {
            response = "Není vyplněné jméno";
        }
        else if (!validator.checkNameFormat(name)) {
            response = "Pojmenuj tu sezonu nějak normálně a bez píčovin";
        }
        else if (!validator.fieldIsNotEmpty(dateStart)) {
            response = "Není vyplněné počáteční datum";
        }
        else if (!validator.isDateInCorrectFormat(dateStart)) {
            response = "Datum musí být ve formátu " + Date.DATE_PATTERN;
        }
        else if (!validator.fieldIsNotEmpty(dateEnd)) {
            response = "Není vyplněné počáteční datum";
        }
        else if (!validator.isDateInCorrectFormat(dateEnd)) {
            response = "Datum musí být ve formátu " + Date.DATE_PATTERN;
        }
        else if (!validator.checkIfStartIsBeforeEnd(dateStart, dateEnd)) {
            response = "Počáteční datum nesmí být nižší než konečné datum";
        }
        else if (!validator.checkSeasonOverlap(dateStart, dateEnd, getSeasons().getValue(), season)) {
            response = "Datum se kryje s jinými sezonami";
        }
        else if (!validator.checkEqualityOfSeason(name, dateStart, dateEnd, season)) {
            response = "Sezona nebyla změněna";
        }
        else {
            result = true;
        }
        return new Result(result, response);
    }

    public Result addSeasonToRepository(final String name, final String dateStart, final String dateEnd) {
        isUpdating.setValue(true);
        Date date = new Date();
        Result result = new Result(false);
        try {
            long millisStart = date.convertTextDateToMillis(dateStart);
            long millisEnd = date.convertTextDateToMillis(dateEnd);
            Season season = new Season(name, millisStart, millisEnd);
            firebaseRepository.insertNewModel(season);
        }
        catch (DateTimeException e) {
            Log.e(TAG, "addSeasonToRepository: toto by nemělo nastat, ošetřeno validací", e);
            result.setText("Datum musí být ve formátu " + Date.DATE_PATTERN);
            isUpdating.setValue(false);
            return result;
        }
        result.setText("Přidávám sezonu " + name);
        result.setTrue(true);
        return result;
    }

    public Result editSeasonInRepository(final String name, final String dateStart, final String dateEnd, Season season) {
        isUpdating.setValue(true);
        Date date = new Date();
        Result result = new Result(false);
        season.setName(name);
        try {
            long millisStart = date.convertTextDateToMillis(dateStart);
            long millisEnd = date.convertTextDateToMillis(dateEnd);
            season.setSeasonStart(millisStart);
            season.setSeasonEnd(millisEnd);
            firebaseRepository.editModel(season);
        }
        catch (DateTimeException e) {
            Log.e(TAG, "editPlayerInRepository: toto by nemělo nastat, ošetřeno validací", e);
            result.setText("Datum musí být ve formátu " + Date.DATE_PATTERN);
            return result;
        }

        result.setText("Přidávám sezonu " + name);
        result.setTrue(true);
        return result;
    }

    public Result removeSeasonFromRepository(Season season) {
        isUpdating.setValue(true);
        Result result = new Result(false);

        try {
            firebaseRepository.removeModel(season);
        }
        catch (Exception e) {
            Log.e(TAG, "removePlayerFromRepository: chyba při mazání sezony", e);
            result.setText("Chyba při posílání požadavku do DB");
            return result;
        }

        result.setText("Mažu sezonu " + season.getName());
        result.setTrue(true);
        return result;
    }

    private void setSeasonAsAdded (final Season season, Flag flag) {
        Log.d(TAG, "setSeasonAsAdded: " + season.getName());
        String action = "";
        switch (flag) {
            case SEASON_PLUS:
                action = "přidána";
                break;
            case SEASON_EDIT:
                action = "upravena";
                break;
            case SEASON_DELETE:
                action = "smazána";
                break;
        }
        alert.setValue("Sezona " + season.getName() + " úspěšně " + action);
        sendNotificationToRepository(prepareNotification("Sezona " + season.getName() + " " + action, user));
        isUpdating.setValue(false);
    }

    private Notification prepareNotification(String text, User user) {
        Notification notification = new Notification(text, user);
        return notification;
    }

    private void sendNotificationToRepository(Notification notification) {
        firebaseRepository.addNotification(notification);
    }

    public LiveData<List<Season>> getSeasons() {
        return seasons;
    }

    public LiveData<Boolean> isUpdating() {
        return isUpdating;
    }

    public LiveData<String> getAlert() {
        return alert;
    }


    @Override
    public void itemAdded(Model model) {
        setSeasonAsAdded((Season) model, Flag.SEASON_PLUS);
    }

    @Override
    public void itemChanged(Model model) {
        setSeasonAsAdded((Season) model, Flag.SEASON_EDIT);
    }

    @Override
    public void itemDeleted(Model model) {
        setSeasonAsAdded((Season) model, Flag.SEASON_DELETE);
    }

    @Override
    public void itemListLoaded(List models, Flag flag) {
        Log.d(TAG, "itemListLoaded: " + models);
        Collections.sort(models, new Comparator<Season>() {
            @Override
            public int compare(Season o1, Season o2) {
                return o1.getSeasonStart() > o2.getSeasonStart() ? -1 : (o1.getSeasonStart() < o2.getSeasonStart()) ? 1 : 0;
            }
        });
        Season season = new Season("Ostatní", 999999999, 999999999);
        models.add(season);

        seasons.setValue(models);
    }

    @Override
    public void alertSent(String message) {
        alert.setValue(message);
    }

}
